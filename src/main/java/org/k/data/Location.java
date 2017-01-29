package org.k.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {
    private final String locationKey;

    @JsonCreator
    public Location(@JsonProperty("Key") String locationKey) {
        this.locationKey = locationKey;
    }

    public String getLocationKey() {
        return locationKey;
    }
}
