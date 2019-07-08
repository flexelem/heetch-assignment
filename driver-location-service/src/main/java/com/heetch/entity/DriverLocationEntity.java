package com.heetch.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.GeoIndexed;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Date;

@RedisHash("driverLocation")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DriverLocationEntity {

    @Id
    private String id;

    @Indexed
    private Long driverId;

    @GeoIndexed
    private Point point;

    private Date updatedAt;
}
