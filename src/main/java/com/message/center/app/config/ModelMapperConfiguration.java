package com.message.center.app.config;


import com.message.center.app.domain.AlertKeyword;
import com.message.center.app.domain.DeviceGroup;
import com.message.center.app.domain.ExchangeMessage;
import com.message.center.app.domain.MessageDevice;
import com.message.center.app.dto.device.MessageDeviceDto;
import com.message.center.app.dto.exchange.ExchangeMessageDto;
import com.message.center.app.dto.group.DeviceGroupDto;
import com.message.center.app.dto.keyword.AlertKeywordDto;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ModelMapperConfiguration {

  private final ModelMapper modelMapper = new ModelMapper();

  @Bean
  public ModelMapper modelMapper() {
    return this.modelMapper;
  }

  @PostConstruct
  private void setCustomModelMappings() {
    setMessageDeviceToMessageDeviceResponse();
    setDeviceGroupToDeviceGroupResponse();
    setExchangeMessageToMessageDto();
    setAlertKeywordToDto();

    modelMapper.getConfiguration()
        .setFieldMatchingEnabled(true)
        .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
  }

  private void setMessageDeviceToMessageDeviceResponse() {
    this.modelMapper().createTypeMap(MessageDevice.class, MessageDeviceDto.class)
        .addMappings(mapper -> mapper.map(MessageDevice::getDeviceName, MessageDeviceDto::setName))
        .addMappings(mapper -> mapper.map(MessageDevice::getEmail, MessageDeviceDto::setEmail))
        .addMappings(mapper -> mapper.map(MessageDevice::getUuid, MessageDeviceDto::setUuid))
        .addMappings(mapper -> mapper.map(MessageDevice::getDeviceType, MessageDeviceDto::setType))
        .addMappings(mapper -> mapper.map(msg -> msg.getDeviceGroup().getName(), MessageDeviceDto::setGroupName));
  }

  private void setDeviceGroupToDeviceGroupResponse() {
    this.modelMapper().createTypeMap(DeviceGroup.class, DeviceGroupDto.class)
        .addMappings(mapper -> mapper.map(DeviceGroup::getUuid, DeviceGroupDto::setUuid))
        .addMappings(mapper -> mapper.map(DeviceGroup::getName, DeviceGroupDto::setName))
        .addMappings(mapper -> mapper.map(DeviceGroup::getDevices, DeviceGroupDto::setDevices));
  }

  private void setExchangeMessageToMessageDto() {
    this.modelMapper().createTypeMap(ExchangeMessage.class, ExchangeMessageDto.class)
        .addMappings(mapper -> {
          mapper.map(ExchangeMessage::getUuid, ExchangeMessageDto::setUuid);
          mapper.map(ExchangeMessage::getContent, ExchangeMessageDto::setContent);
          mapper.map(ExchangeMessage::getMessageType, ExchangeMessageDto::setType);
          mapper.map(ExchangeMessage::getTimestamp, ExchangeMessageDto::setTime);
          mapper.map(ExchangeMessage::getAlertKeywords, ExchangeMessageDto::setKeywords);
        });

  }

  private void setAlertKeywordToDto() {
    this.modelMapper().createTypeMap(AlertKeyword.class, AlertKeywordDto.class)
        .addMappings(mapper -> mapper.map(AlertKeyword::getUuid, AlertKeywordDto::setUuid))
        .addMappings(mapper -> mapper.map(AlertKeyword::getKeyword, AlertKeywordDto::setKeyword));
  }

}
