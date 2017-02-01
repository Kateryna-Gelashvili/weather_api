package org.k.service;

import org.k.data.Location;
import org.k.data.Weather;
import org.k.exception.UnknownException;
import org.k.exception.WeatherServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class AccuWeatherApiService {
    private static final String GET_LOCATION_URL = "http://dataservice.accuweather.com/locations/v1/";
    private static final String SEARCH = "/search";
    private static final String GET_WEATHER_URL = "http://dataservice.accuweather.com/currentconditions/v1/";
    private final String apiKey;
    private final RestTemplate restTemplate;

    @Autowired
    public AccuWeatherApiService(@Value("${api.key}") String apiKey, RestTemplate restTemplate) {
        this.apiKey = apiKey;
        this.restTemplate = restTemplate;
    }

    public Optional<Location> getLocation(String countryCode, String city) throws IOException {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(GET_LOCATION_URL + countryCode + SEARCH)
                .queryParam("apikey", apiKey)
                .queryParam("q", city);

        ResponseEntity<List<Location>> response = restTemplate.exchange(
                uriBuilder.build().encode().toUri(),
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Location>>() {
                });

        HttpStatus status = response.getStatusCode();
        checkResponseStatus(status);

        List<Location> locations = response.getBody();

        return locations.stream().findFirst();
    }

    public Optional<Weather> getWeather(String locationKey) throws IOException {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(GET_WEATHER_URL + locationKey)
                .queryParam("apikey", apiKey);

        ResponseEntity<List<Weather>> response = restTemplate.exchange(
                uriBuilder.build().encode().toUri(),
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Weather>>() {
                });

        HttpStatus status = response.getStatusCode();
        checkResponseStatus(status);

        List<Weather> weathers = response.getBody();

        return weathers.stream().findFirst();
    }

    private void checkResponseStatus(HttpStatus status) {
        if (!HttpStatus.OK.equals(status)) {
            if (HttpStatus.BAD_REQUEST.equals(status)) {
                throw new WeatherServiceException("Bad request");
            } else {
                throw new UnknownException("Request failed with status " + status);
            }
        }
    }
}
