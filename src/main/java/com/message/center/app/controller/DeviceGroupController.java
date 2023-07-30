package com.message.center.app.controller;


import com.message.center.app.dto.group.CreateDeviceGroupRequest;
import com.message.center.app.dto.group.DeviceGroupDto;
import com.message.center.app.service.DeviceGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class DeviceGroupController {

  private final DeviceGroupService deviceGroupService;

  @Autowired
  public DeviceGroupController(DeviceGroupService deviceGroupService) {
    this.deviceGroupService = deviceGroupService;
  }

  @GetMapping
  public ResponseEntity<List<DeviceGroupDto>> getAllGroups() {
    return ResponseEntity.ok(deviceGroupService.getAll());
  }

  @GetMapping(path = "/{group-id}")
  public ResponseEntity<DeviceGroupDto> getGroupById(@PathVariable("group-id") String groupId) {
    return ResponseEntity.ok(deviceGroupService.getById(groupId));
  }

  @PostMapping
  public ResponseEntity<DeviceGroupDto> createGroup(@Validated @RequestBody CreateDeviceGroupRequest request) {
    return ResponseEntity.ok(deviceGroupService.create(request));
  }

  @DeleteMapping("/{group-id}")
  public ResponseEntity<HttpStatus> deleteGroupById(@PathVariable("group-id") String groupId) {
    return ResponseEntity.status(deviceGroupService.deleteById(groupId)).build();
  }

}
