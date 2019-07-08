package com.heetch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DriverLocationRequestEntity {
    private Double latitude;
    private Double longitude;
}
