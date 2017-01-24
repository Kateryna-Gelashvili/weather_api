package org.k.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {
    @JsonProperty("Key")
    private String locationKey;

    public String getLocationKey() {
        return locationKey;
    }
}
