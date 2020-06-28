package redditflux.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SunriseSunset {

    private final String sunrise;
    private final String sunset;

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    @JsonCreator
    public SunriseSunset(@JsonProperty("sunrise") final String sunrise, @JsonProperty("sunset") final String sunset){
        this.sunrise = sunrise;
        this.sunset = sunset;
    }
}
