package com.message.center.app.service;

import com.message.center.app.dto.keyword.AlertKeywordDto;
import com.message.center.app.dto.keyword.CreateAlertKeywordRequest;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface AlertKeywordService {

  List<AlertKeywordDto> getAll();

  AlertKeywordDto create(CreateAlertKeywordRequest requestDto);

  HttpStatus deleteById(String uuid);
}
