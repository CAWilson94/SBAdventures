package redditflux.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GeographicCoordinates {

    private final double latitude;
    private final double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @JsonCreator
    public GeographicCoordinates(@JsonProperty("latitude") double latitude,@JsonProperty("longitude") double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
