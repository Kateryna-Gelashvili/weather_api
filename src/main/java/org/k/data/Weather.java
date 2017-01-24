package org.k.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather {
    @JsonProperty("LocalObservationDateTime")
    private String localObservationDateTime;

    @JsonProperty("EpochTime")
    private String epochTime;

    @JsonProperty("WeatherText")
    private String weatherText;

    private String temperatureC;
    private String temperatureF;

    public String getLocalObservationDateTime() {
        return localObservationDateTime;
    }

    public void setLocalObservationDateTime(String localObservationDateTime) {
        this.localObservationDateTime = localObservationDateTime;
    }

    public String getEpochTime() {
        return epochTime;
    }

    public void setEpochTime(String epochTime) {
        this.epochTime = epochTime;
    }

    public String getWeatherText() {
        return weatherText;
    }

    public void setWeatherText(String weatherText) {
        this.weatherText = weatherText;
    }

    public String getTemperatureC() {
        return temperatureC;
    }

    public void setTemperatureC(String temperatureC) {
        this.temperatureC = temperatureC;
    }

    public String getTemperatureF() {
        return temperatureF;
    }

    public void setTemperatureF(String temperatureF) {
        this.temperatureF = temperatureF;
    }

    @JsonProperty("Temperature")
    private void getTemperatureC(Map<String, Map<String, String>> temperature) {
        this.temperatureC = temperature.get("Metric").get("Value");
        this.temperatureF = temperature.get("Imperial").get("Value");
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
