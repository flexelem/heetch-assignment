package com.heetch.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DriverLocationMessageEntity {
    private Long driverId;
    private Double latitude;
    private Double longitude;
}
