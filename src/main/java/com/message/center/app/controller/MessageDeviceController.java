package com.message.center.app.controller;

import com.message.center.app.dto.device.AssignMessageDeviceRequest;
import com.message.center.app.dto.device.CreateMessageDeviceRequest;
import com.message.center.app.dto.device.MessageDeviceDto;
import com.message.center.app.service.MessageDeviceService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/devices")
public class MessageDeviceController {

  private final MessageDeviceService messageDeviceService;

  @Autowired
  public MessageDeviceController(MessageDeviceService messageDeviceService) {
    this.messageDeviceService = messageDeviceService;
  }

  @GetMapping
  public ResponseEntity<List<MessageDeviceDto>> getAllDevices() {
    return ResponseEntity.ok(messageDeviceService.getAll());
  }

  @GetMapping(path = "/{device-id}")
  public ResponseEntity<MessageDeviceDto> getDeviceById(@PathVariable("device-id") String deviceId) {
    return ResponseEntity.ok(messageDeviceService.getById(deviceId));
  }

  @PostMapping
  public ResponseEntity<MessageDeviceDto> createDevice(@Validated @RequestBody CreateMessageDeviceRequest request) {
    return ResponseEntity.ok(messageDeviceService.create(request));
  }

  @PutMapping("/{device-id}")
  public ResponseEntity<MessageDeviceDto> assignMessageDevice(
      @Validated @RequestBody AssignMessageDeviceRequest request,
      @PathVariable("device-id") @NotBlank @Size(min = 36, max = 36) String deviceId) {
    return ResponseEntity.ok(messageDeviceService.assignDevice(request, deviceId));
  }

  @DeleteMapping("/{device-id}")
  public ResponseEntity<HttpStatus> deleteGroupById(@PathVariable("device-id") String deviceId) {
    return ResponseEntity.status(messageDeviceService.delete(deviceId)).build();
  }
}
