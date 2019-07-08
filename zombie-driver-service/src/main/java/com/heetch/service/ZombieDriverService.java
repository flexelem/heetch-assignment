package com.heetch.service;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.GeoUnit;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ZombieDriverService {
    private static final String ZSET_NAME_PREFIX = "locations_";
    private static final String GEO_SET_KEY_NAME = "geoLocations";

    @Autowired
    private RedissonClient redissonClient;

    public double calculateTotalDistanceCovered(long driverId, int minutes) {
        long now = Instant.now().getEpochSecond();
        long range = Instant.ofEpochSecond(now).minusSeconds(minutes * 60).getEpochSecond();

        List<String> hashIds = redissonClient.getScoredSortedSet(ZSET_NAME_PREFIX + driverId)
                .valueRange(Instant.ofEpochSecond(range).toEpochMilli(), true,
                        Instant.ofEpochSecond(now).toEpochMilli(), true)
                .stream().map(hashId -> (String) hashId).collect(Collectors.toList());

        double totalDistanceCovered = 0;
        if (CollectionUtils.isEmpty(hashIds) || hashIds.size() == 1) {
            return 0;
        }

        Iterator<String> iter = hashIds.iterator();
        String currentHash = iter.next();
        while (iter.hasNext()) {
            String nextHash = iter.next();

            Double dist = redissonClient.getGeo(GEO_SET_KEY_NAME, new StringCodec()).dist(nextHash, currentHash, GeoUnit.METERS);
            totalDistanceCovered += dist;

            currentHash = nextHash;
        }

        return totalDistanceCovered;
    }
}
