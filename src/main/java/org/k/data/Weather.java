package org.k.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather {
    private final String localObservationDateTime;
    private final String epochTime;
    private final String weatherText;
    private final String temperatureC;
    private final String temperatureF;

    @JsonCreator
    public Weather(@JsonProperty("LocalObservationDateTime") String localObservationDateTime,
                   @JsonProperty("EpochTime") String epochTime,
                   @JsonProperty("WeatherText") String weatherText,
                   @JsonProperty("Temperature") Map<String, Map<String, String>> temperature) {
        this.localObservationDateTime = localObservationDateTime;
        this.epochTime = epochTime;
        this.weatherText = weatherText;
        this.temperatureC = temperature.get("Metric").get("Value");
        this.temperatureF = temperature.get("Imperial").get("Value");
    }

    public String getLocalObservationDateTime() {
        return localObservationDateTime;
    }

    public String getEpochTime() {
        return epochTime;
    }

    public String getWeatherText() {
        return weatherText;
    }

    public String getTemperatureF() {
        return temperatureF;
    }

    private String getTemperatureC() {
        return temperatureC;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "localObservationDateTime='" + localObservationDateTime + '\'' +
                ", epochTime='" + epochTime + '\'' +
                ", weatherText='" + weatherText + '\'' +
                ", temperatureC='" + temperatureC + '\'' +
                ", temperatureF='" + temperatureF + '\'' +
                '}';
    }
}
