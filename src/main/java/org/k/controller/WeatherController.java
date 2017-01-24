package org.k.controller;

import org.k.data.Location;
import org.k.dto.ErrorMessageDto;
import org.k.dto.TopicDto;
import org.k.service.AccuWeatherApiService;
import org.k.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Optional;

@Controller
public class WeatherController {

    private final WeatherService weatherService;
    private final AccuWeatherApiService accuWeatherApiService;

    @Autowired
    public WeatherController(WeatherService weatherService,
                             AccuWeatherApiService accuWeatherApiService) {
        this.weatherService = weatherService;
        this.accuWeatherApiService = accuWeatherApiService;
    }

    @GetMapping(value = "/topic")
    public ResponseEntity<?> getTopic(@RequestParam("countryCode") String countryCode,
                                      @RequestParam("city") String city) throws IOException {
        Optional<Location> locationOptional = accuWeatherApiService.getLocation(countryCode, city);

        if (!locationOptional.isPresent()) {
            return new ResponseEntity<Object>(
                    new ErrorMessageDto(
                            "Can not find location of " + countryCode + " " + city),
                    HttpStatus.NOT_FOUND);
        }

        String topic = locationOptional.get().getLocationKey();
        weatherService.addNewTopicToMessageProcess(topic);

        return new ResponseEntity<Object>(new TopicDto(topic), HttpStatus.OK);
    }
}
