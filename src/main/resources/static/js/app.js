document.addEventListener('DOMContentLoaded', () => {
    // Map setup
    const DEFAULT_LAT = 34.0522;
    const DEFAULT_LNG = -118.2437;

    const map = L.map('map').setView([DEFAULT_LAT, DEFAULT_LNG], 15);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);

    const markersLayer = L.layerGroup().addTo(map);

    // Icon for the search center
    const centerIcon = L.divIcon({
        className: 'center-marker-icon',
        iconSize: [16, 16],
        iconAnchor: [8, 8]
    });

    // refs
    const statusEl = document.getElementById('status');
    const radiusInput = document.getElementById('radius-input');
    const searchButton = document.getElementById('search-button');

    const sortBySelect = document.getElementById('sort-by');
    const minTimeSelect = document.getElementById('min-time');
    const maxRateSelect = document.getElementById('max-rate');

    const latInput = document.getElementById('lat-input');
    const lngInput = document.getElementById('lng-input');
    const setCenterButton = document.getElementById('set-center-button');

    // State
    let searchCenterMarker = null;
    let searchRadiusCircle = null;

    // Helpers
    function setStatus(msg) {
        statusEl.textContent = msg;
    }

    function getSearchCenter() {
        if (searchCenterMarker) {
            return searchCenterMarker.getLatLng();
        }
        return map.getCenter();
    }

    function updateRadiusCircle() {
        const center = getSearchCenter();
        const radius = parseInt(radiusInput.value, 10) || 500;
        if (!center) return;

        if (!searchRadiusCircle) {
            searchRadiusCircle = L.circle(center, { radius }).addTo(map);
        } else {
            searchRadiusCircle.setLatLng(center);
            searchRadiusCircle.setRadius(radius);
        }
    }

    // Search
    async function searchAroundCenter(isRetry = false) {
        const center = getSearchCenter();
        let radius = parseInt(radiusInput.value, 10) || 500;

        const sortBy = sortBySelect.value;           // distance | time | price
        const minTimeMinutes = parseInt(minTimeSelect.value, 10) || 0;
        const maxRatePerHour = parseFloat(maxRateSelect.value) || 0;

        setStatus(`Searching within ${radius} m…`);

        const params = new URLSearchParams({
            lat: center.lat,
            lng: center.lng,
            radiusMeters: radius,
            sortBy: sortBy,
            minTimeMinutes: String(minTimeMinutes),
            maxRatePerHour: String(maxRatePerHour)
        });

        const url = `/api/search?${params.toString()}`;

        try {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error('HTTP ' + response.status);
            }

            const meters = await response.json();

            // Auto set small radius to 500m if nothing found
            if ((!Array.isArray(meters) || meters.length === 0) &&
                radius < 500 && !isRetry) {

                radius = 500;
                radiusInput.value = 500;
                setStatus('No meters in that radius. Trying 500 m instead…');
                updateRadiusCircle();
                return searchAroundCenter(true);
            }

            markersLayer.clearLayers();

            if (!Array.isArray(meters) || meters.length === 0) {
                setStatus(`No meters found within ${radius} m.`);
                return;
            }

            meters.forEach(m => {
                const marker = L.marker([m.lat, m.lng]).addTo(markersLayer);
                marker.bindPopup(`
                    <strong>${m.name}</strong><br/>
                    Distance: ${m.distanceMeters.toFixed(1)} m<br/>
                    Time limit: ${
                        m.timeLimitMinutes > 0
                            ? (m.timeLimitMinutes >= 60
                                ? (m.timeLimitMinutes / 60) + ' hr'
                                : m.timeLimitMinutes + ' min')
                            : 'n/a'
                    }<br/>
                    Rate: ${
                        m.ratePerHour > 0
                            ? '$' + m.ratePerHour.toFixed(2) + '/hr'
                            : 'n/a'
                    }<br/>
                    Lat: ${m.lat.toFixed(6)}, Lng: ${m.lng.toFixed(6)}
                `);
            });

            setStatus(`Loaded ${meters.length} meter(s) within ${radius} m.`);
        } catch (err) {
            console.error(err);
            setStatus('Error fetching /api/search.');
        }
    }

    // Center from inputs
    function setCenterFromInputs() {
        const latVal = parseFloat(latInput.value);
        const lngVal = parseFloat(lngInput.value);

        if (Number.isNaN(latVal) || Number.isNaN(lngVal)) {
            setStatus('Please enter a valid latitude and longitude.');
            return;
        }

        if (latVal < -90 || latVal > 90 || lngVal < -180 || lngVal > 180) {
            setStatus('Lat must be between -90 and 90, lng between -180 and 180.');
            return;
        }

        const centerLatLng = L.latLng(latVal, lngVal);

        // Move map
        map.setView(centerLatLng, 16);

        // Create or move center marker
        if (!searchCenterMarker) {
            searchCenterMarker = L.marker(centerLatLng, {
                icon: centerIcon,
                draggable: true
            }).addTo(map);

            searchCenterMarker.on('dragend', () => {
                updateRadiusCircle();
            });
        } else {
            searchCenterMarker.setLatLng(centerLatLng);
        }

        // Update radius circle and run a search
        updateRadiusCircle();
        setStatus('Center set from coordinates. Searching for nearby meters…');
        searchAroundCenter();
    }

    // Event listeners
    setCenterButton.addEventListener('click', setCenterFromInputs);

    latInput.addEventListener('keyup', (e) => {
        if (e.key === 'Enter') setCenterFromInputs();
    });
    lngInput.addEventListener('keyup', (e) => {
        if (e.key === 'Enter') setCenterFromInputs();
    });

    map.on('click', (e) => {
        const radius = parseInt(radiusInput.value, 10) || 500;

        if (!searchCenterMarker) {
            searchCenterMarker = L.marker(e.latlng, {
                icon: centerIcon,
                draggable: true
            }).addTo(map);

            searchCenterMarker.on('dragend', () => {
                updateRadiusCircle();
            });
        } else {
            searchCenterMarker.setLatLng(e.latlng);
        }

        updateRadiusCircle();
        searchAroundCenter();
    });

    radiusInput.addEventListener('change', updateRadiusCircle);

    searchButton.addEventListener('click', () => searchAroundCenter());

    // Initial search using default map center
    searchAroundCenter();
});
s