package com.message.center.app.repository;

import com.message.center.app.domain.AlertKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertKeywordRepository extends JpaRepository<AlertKeyword, Long> {

  Optional<AlertKeyword> findByUuid(String uuid);

  AlertKeyword deleteByUuid(String uuid);

  List<AlertKeyword> findAllByKeywordIn(List<String> nameList);

}
