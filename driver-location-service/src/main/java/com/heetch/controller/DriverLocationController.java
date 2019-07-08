package com.heetch.controller;

import com.heetch.entity.DriverLocation;
import com.heetch.service.DriverLocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/drivers")
@Slf4j
public class DriverLocationController {

    @Autowired
    private DriverLocationService driverLocationService;

    @RequestMapping(value = "/{id}/locations", method = RequestMethod.GET)
    public ResponseEntity<List<DriverLocation>> getDriver(@RequestParam("minutes") int minutes,
                                                          @PathVariable("id") Long driverId) {
        // TODO: do param validations

        return ResponseEntity.status(HttpStatus.OK).body(driverLocationService.getDriverLocationsForLastNMinutes(driverId, minutes));
    }
}
