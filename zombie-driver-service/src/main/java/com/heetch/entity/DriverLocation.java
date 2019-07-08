package com.heetch.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverLocation {
    private String updatedAt;
    private Double latitude;
    private Double longitude;
}
