package entity;

/**
 * A geographic location resolved from a user-provided address.
 * This is part of the core entity layer.
 */
public class Location {
    private final String displayName;
    private final double latitude;
    private final double longitude;

    public Location(String displayName, double latitude, double longitude) {
        this.displayName = displayName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
