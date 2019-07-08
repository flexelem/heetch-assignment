package com.heetch.utils;

import com.heetch.entity.DriverLocationMessageEntity;
import com.heetch.entity.DriverLocationRequestEntity;
import com.heetch.exception.InvalidDriverIdException;
import com.heetch.exception.InvalidLocationException;

public class Validator {

    public static void validateLocationMessageAndDriverId(DriverLocationRequestEntity request, Long driverId) {
        if (request == null) {
            throw new InvalidLocationException("Message can not be null");
        } else if (request.getLatitude() == null || request.getLatitude() < -90.0 || request.getLatitude() > 90.0 ) {
            throw new InvalidLocationException("message.latitude can not be null and should be between -90.0 and 90.0");
        } else if (request.getLongitude() == null || request.getLongitude() < -180.0 || request.getLongitude() > 180.0) {
            throw new InvalidLocationException("message.longitude can not be null and should be between -180.0 and 180.0");
        }

        validateDriverId(driverId);
    }

    public static void validateDriverId(Long id) {
        if (id < 0L) {
            throw new InvalidDriverIdException("Invalid driverId");
        }
    }
}
