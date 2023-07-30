package com.message.center.app.repository;

import com.message.center.app.domain.DeviceGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceGroupRepository extends JpaRepository<DeviceGroup, Long>,
    JpaSpecificationExecutor<DeviceGroup> {

  Optional<DeviceGroup> findByUuid(String uuid);

  DeviceGroup deleteByUuid(String uuid);

}
