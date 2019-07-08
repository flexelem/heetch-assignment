package com.heetch.repository;

import com.heetch.entity.DriverLocationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverLocationRepository extends CrudRepository<DriverLocationEntity, Long> {
}
