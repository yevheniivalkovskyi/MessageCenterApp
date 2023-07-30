package com.message.center.app.repository;

import com.message.center.app.domain.ExchangeMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeMessageRepository extends JpaRepository<ExchangeMessage, Long>,
    JpaSpecificationExecutor<ExchangeMessage> {

  List<ExchangeMessage> findBySenderUuid(String senderUuid);

  Optional<ExchangeMessage> findByUuid(String uuid);

  List<ExchangeMessage> findByReceiversContaining(String receiverUuid);

}
