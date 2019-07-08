package com.heetch.controller;

import com.heetch.entity.DriverLocationMessageEntity;
import com.heetch.entity.DriverLocationRequestEntity;
import com.heetch.utils.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/drivers")
@Slf4j
public class GatewayController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${application.nsq.host}")
    private String host;

    @Value("${application.nsq.port}")
    private int port;

    @Value("${application.nsq.topic}")
    private String topic;

    @Value("${application.nsq.channel}")
    private String channel;

    @Value("${application.zombie-driver-service.host}")
    private String zombieDriverHost;

    @Value("${application.zombie-driver-service.port}")
    private String zombieDriverPort;

    @RequestMapping(value = "/{id}/locations", method = RequestMethod.PATCH)
    public ResponseEntity<String> addDriversLocation(@PathVariable("id") Long id,
                                                     @RequestBody DriverLocationRequestEntity requestEntity) {
        Validator.validateLocationMessageAndDriverId(requestEntity, id);

        // send message to nsq
        ResponseEntity<String> response = restTemplate.exchange(
                "http://" + host + ":" + port + "/pub?topic=" + topic + "&channel=" + channel,
                HttpMethod.POST,
                new HttpEntity<>(DriverLocationMessageEntity.builder()
                        .driverId(id)
                        .latitude(requestEntity.getLatitude())
                        .longitude(requestEntity.getLongitude())
                        .build()),
                String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> getDriver(@PathVariable("id") Long id) {
        Validator.validateDriverId(id);

        // send message to nsq
        ResponseEntity<String> response = restTemplate.exchange(
                "http://" + zombieDriverHost + ":" + zombieDriverPort + "/drivers/" + id,
                HttpMethod.GET,
                null,
                String.class);

        return response;
    }
}
