package org.k.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.k.data.Location;
import org.k.data.Weather;
import org.k.exception.UnknownException;
import org.k.exception.WeatherServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class AccuWeatherApiService {
    private static final String GET_LOCATION_URL = "http://dataservice.accuweather.com/locations/v1/";
    private static final String SEARCH = "/search";
    private static final String GET_WEATHER_URL = "http://dataservice.accuweather.com/currentconditions/v1/";
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public AccuWeatherApiService(@Value("${api.key}") String apiKey,
                                 ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }

    public Optional<Location> getLocation(String countryCode, String city) throws IOException {
        String url = GET_LOCATION_URL + countryCode + SEARCH;
        String charset = StandardCharsets.UTF_8.name();
        String query = String.format("apikey=%s&q=%s",
                URLEncoder.encode(apiKey, charset),
                URLEncoder.encode(city, charset));

        URLConnection connection = getUrlConnection(url, query);
        StringBuilder response = getResponse(connection);

        List<Location> locations = objectMapper.readValue(response.toString(),
                new TypeReference<List<Location>>() {
        });

        if (!locations.isEmpty()) {
            return Optional.of(locations.get(0));
        } else {
            return Optional.empty();
        }

    }

    public Optional<Weather> getWeather(String locationKey) throws IOException {
        String url = GET_WEATHER_URL + locationKey;
        String charset = StandardCharsets.UTF_8.name();
        String query = String.format("apikey=%s", URLEncoder.encode(apiKey, charset));

        URLConnection connection = getUrlConnection(url, query);
        StringBuilder response = getResponse(connection);

        List<Weather> weathers = objectMapper.readValue(response.toString(),
                new TypeReference<List<Weather>>() {
        });

        return weathers.stream().findFirst();
    }

    private StringBuilder getResponse(URLConnection connection) throws IOException {
        StringBuilder response = new StringBuilder();
        try (InputStream in = connection.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in))) {
            int status = ((HttpURLConnection) connection).getResponseCode();
            if (HttpStatus.OK.value() == status) {
                String inputLine;
                while ((inputLine = bufferedReader.readLine()) != null) {
                    response.append(inputLine);
                }
            } else if (HttpStatus.BAD_REQUEST.value() == status) {
                throw new WeatherServiceException("Bad request");
            } else {
                throw new UnknownException("Request failed with status " + status);
            }
        }
        return response;
    }

    private URLConnection getUrlConnection(String url, String query) throws IOException {
        String charset = StandardCharsets.UTF_8.name();
        URLConnection connection = new URL(url + "?" + query).openConnection();
        connection.setRequestProperty("Accept-Charset", charset);
        return connection;
    }
}
