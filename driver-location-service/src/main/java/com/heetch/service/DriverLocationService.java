package com.heetch.service;

import com.heetch.entity.DriverLocation;
import com.heetch.entity.DriverLocationEntity;
import com.heetch.entity.DriverLocationMessageEntity;
import com.heetch.repository.DriverLocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DriverLocationService {

    private static final String ZSET_NAME_PREFIX = "locations_";
    private static final String HASH_ID_PREFIX = "driverLocation:";
    private static final String GEO_SET_KEY_NAME = "geoLocations";

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private DriverLocationRepository driverLocationRepository;

    public List<DriverLocation> getDriverLocationsForLastNMinutes(long driverId, int minutes) {
        long now = Instant.now().getEpochSecond();
        long range = Instant.ofEpochSecond(now).minusSeconds(minutes * 60).getEpochSecond();

        List<String> hashIds = redissonClient.getScoredSortedSet(ZSET_NAME_PREFIX + driverId)
                .valueRange(Instant.ofEpochSecond(range).toEpochMilli(), true,
                        Instant.ofEpochSecond(now).toEpochMilli(), true)
                .stream().map(hashId -> (String) hashId).collect(Collectors.toList());

        List<DriverLocation> driverLocationList = new ArrayList<>();
        for (String hashId : hashIds) {
            Set<Map.Entry<Object, Object>> keyValueEntries = redissonClient.getMap(hashId, new StringCodec()).readAllEntrySet();
            DriverLocation driverLocation = new DriverLocation();
            for (Map.Entry<Object, Object> keyValEntry : keyValueEntries) {
                if (StringUtils.equalsAnyIgnoreCase("updatedAt", (String) keyValEntry.getKey())) {
                    driverLocation.setUpdatedAt(Instant.ofEpochMilli(Long.valueOf((String) keyValEntry.getValue())).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss'Z'")));
                } else if (StringUtils.equalsAnyIgnoreCase("point.x", (String) keyValEntry.getKey())) {
                    driverLocation.setLongitude(Double.valueOf((String) keyValEntry.getValue()));
                } else if (StringUtils.equalsAnyIgnoreCase("point.y", (String) keyValEntry.getKey())) {
                    driverLocation.setLatitude(Double.valueOf((String) keyValEntry.getValue()));
                }
            }

            driverLocationList.add(driverLocation);
        }

        return driverLocationList;
    }

    public void saveDriverLocationMessageEntity(DriverLocationMessageEntity messageEntity) {
        long now = Instant.now().toEpochMilli();

        DriverLocationEntity driverLocationEntity = new DriverLocationEntity();
        driverLocationEntity.setDriverId(messageEntity.getDriverId());
        driverLocationEntity.setPoint(new Point(messageEntity.getLongitude(), messageEntity.getLatitude()));
        driverLocationEntity.setUpdatedAt(new Date(now));
        driverLocationEntity.setId(UUID.randomUUID().toString());

        // save driver location entity
        driverLocationRepository.save(driverLocationEntity);

        // add hash_id into geoset for distance calculations
        redissonClient.getGeo(GEO_SET_KEY_NAME, new StringCodec()).add(messageEntity.getLongitude(), messageEntity.getLatitude(), HASH_ID_PREFIX + driverLocationEntity.getId());

        // save timestamp -> hash_id
        redissonClient.getScoredSortedSet(ZSET_NAME_PREFIX + messageEntity.getDriverId()).add(now, HASH_ID_PREFIX + driverLocationEntity.getId());
    }
}
