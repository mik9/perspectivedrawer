package ua.pl.mik.perspectivedrawer.sample;

public enum Place {
    KYIV(R.string.kyiv, 50.450100, 30.523400),
    LONDON(R.string.london, 51.507351, -0.127758),
    NY(R.string.ny, 40.712784, -74.005941),
    SEOUL(R.string.seoul, 37.566535, 126.977969),
    NJ(R.string.nj, 40.058324, -74.405661),
    SF(R.string.sf, 37.774929, -122.419416),
    PARIS(R.string.paris, 48.856614, 2.352222),
    AMSTERDAM(R.string.amsterdam, 52.370216, 4.895168),
    ROME(R.string.rome, 41.872389, 12.480180),
    PRAHA(R.string.praha, 50.075538, 14.437800),
    MADRID(R.string.madrid, 40.416775, -3.703790),
    MUNCHEN(R.string.munchen, 48.135125, 11.581981),
    VENEZIA(R.string.venezia, 45.440847, 12.315515);
    private int textId;
    private double lat;
    private double lng;

    Place(int textId, double lat, double lng) {
        this.textId = textId;
        this.lat = lat;
        this.lng = lng;
    }

    public int getTextId() {
        return textId;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
