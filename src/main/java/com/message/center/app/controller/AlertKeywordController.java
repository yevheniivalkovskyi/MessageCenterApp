package com.message.center.app.controller;


import com.message.center.app.dto.keyword.AlertKeywordDto;
import com.message.center.app.dto.keyword.CreateAlertKeywordRequest;
import com.message.center.app.service.AlertKeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/keywords")
public class AlertKeywordController {

  private final AlertKeywordService alertKeywordService;

  @Autowired
  public AlertKeywordController(AlertKeywordService alertKeywordRepository) {
    this.alertKeywordService = alertKeywordRepository;
  }

  @GetMapping
  public ResponseEntity<List<AlertKeywordDto>> getAllAlertKeywords() {
    List<AlertKeywordDto> alertKeywords = alertKeywordService.getAll();
    return new ResponseEntity<>(alertKeywords, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<AlertKeywordDto> addAlertKeyword(@RequestBody CreateAlertKeywordRequest request) {
    AlertKeywordDto newAlertKeyword = alertKeywordService.create(request);
    return new ResponseEntity<>(newAlertKeyword, HttpStatus.CREATED);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<HttpStatus> deleteAlertKeyword(@PathVariable String id) {
    return ResponseEntity.status(alertKeywordService.deleteById(id)).build();
  }
}
