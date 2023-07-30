package com.message.center.app.service.impl;

import com.message.center.app.domain.AlertKeyword;
import com.message.center.app.dto.keyword.AlertKeywordDto;
import com.message.center.app.dto.keyword.CreateAlertKeywordRequest;
import com.message.center.app.repository.AlertKeywordRepository;
import com.message.center.app.service.AlertKeywordService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertKeywordServiceImpl implements AlertKeywordService {

  private final AlertKeywordRepository alertKeywordRepository;
  private final ModelMapper modelMapper;

  @Autowired
  public AlertKeywordServiceImpl(AlertKeywordRepository alertKeywordRepository, ModelMapper modelMapper) {
    this.alertKeywordRepository = alertKeywordRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  public AlertKeywordDto create(CreateAlertKeywordRequest request) {
    AlertKeyword alertKeyword = new AlertKeyword();
    alertKeyword.setKeyword(request.getName());
    AlertKeyword savedKeyword = alertKeywordRepository.save(alertKeyword);
    return convertToDto(savedKeyword);
  }


  private AlertKeywordDto convertToDto(AlertKeyword alertKeyword) {
    return modelMapper.map(alertKeyword, AlertKeywordDto.class);
  }

  @Override
  public List<AlertKeywordDto> getAll() {
    List<AlertKeyword> alertKeywords = alertKeywordRepository.findAll();
    return alertKeywords.stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
  }

  @Override
  public HttpStatus deleteById(String uuid) {
    AlertKeyword alertKeyword = alertKeywordRepository.deleteByUuid(uuid);
    return alertKeyword.getUuid().equalsIgnoreCase(uuid) ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND;
  }
}
