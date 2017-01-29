package org.k.service;

import org.k.data.Weather;
import org.k.exception.UnknownException;
import org.k.exception.WeatherServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class WeatherService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final KafkaProducerService kafkaProducerService;
    private final AccuWeatherApiService accuWeatherApiService;
    private final Set<String> topics;

    @Autowired
    private WeatherService(KafkaProducerService kafkaProducerService,
                           AccuWeatherApiService accuWeatherApiService) {
        this.kafkaProducerService = kafkaProducerService;
        this.accuWeatherApiService = accuWeatherApiService;
        topics = ConcurrentHashMap.newKeySet();
    }

    @Scheduled(fixedDelayString = "${messages.delay:60000}")
    protected void sendWeatherMessages() {
        logger.info("Started new execution...");
        if (!topics.isEmpty()) {
            logger.info("Topics: {}", topics);
            kafkaProducerService.sendWeatherMessages(getMapOfMessages());
        } else {
            logger.info("No topics found...");
        }
    }

    public void addNewTopicToMessageProcess(String topic) throws IOException {
        checkWeatherResponseForNewTopic(topic);
        topics.add(topic);
    }

    private void checkWeatherResponseForNewTopic(String topic) throws IOException {
        Optional<Weather> weatherOptional = accuWeatherApiService.getWeather(topic);
        if (!weatherOptional.isPresent()) {
            throw new WeatherServiceException("Can not get weather for requested parameters: " +
                    topic);
        }
    }

    private Map<String, Weather> getMapOfMessages() {
        Map<String, Weather> messages = new HashMap<String, Weather>();
        for (String topic : topics) {
            try {
                Optional<Weather> weatherOptional = accuWeatherApiService.getWeather(topic);
                if (!weatherOptional.isPresent()) {
                    continue;
                }

                messages.put(topic, weatherOptional.get());
            } catch (IOException e) {
                throw new UnknownException("IOExceptional while getting weather for " + topic);
            }
        }
        return messages;
    }
}
