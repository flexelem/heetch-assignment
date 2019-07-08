package com.heetch.controller;

import com.heetch.entity.ZombieDriverResponseEntity;
import com.heetch.exception.InvalidDriverIdException;
import com.heetch.service.ZombieDriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/drivers")
public class ZombieDriverController {

    @Autowired
    private ZombieDriverService zombieDriverService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<ZombieDriverResponseEntity> isZombieDriver(@PathVariable("id") Long driverId) {

        if (driverId < 0) {
            throw new InvalidDriverIdException("Invalid driver id: {}" + driverId);
        }

        double dist = zombieDriverService.calculateTotalDistanceCovered(driverId, 5);
        if (dist > 500.0) {
            return ResponseEntity.status(HttpStatus.OK).body(ZombieDriverResponseEntity.builder().driverId(driverId).zombie(false).build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(ZombieDriverResponseEntity.builder().driverId(driverId).zombie(true).build());
    }
}
