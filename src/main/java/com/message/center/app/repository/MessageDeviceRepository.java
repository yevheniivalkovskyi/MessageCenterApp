package com.message.center.app.repository;

import com.message.center.app.domain.MessageDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageDeviceRepository extends JpaRepository<MessageDevice, Long>,
    JpaSpecificationExecutor<MessageDevice> {

  Optional<MessageDevice> findByUuid(String uuid);

  boolean deleteByUuid(String uuid);
}
