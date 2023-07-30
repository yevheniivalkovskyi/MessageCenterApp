package com.message.center.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "message_device")
public class MessageDevice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String deviceName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DeviceType deviceType;

  @Email(message = "Please provide a valid device email address")
  @NotBlank
  private String email;

  @Column(name = "guid")
  @NotBlank
  @Size(max = 36)
  private String uuid = UUID.randomUUID().toString();

  @ManyToOne
  @JoinColumn(name = "device_group_id")
  private DeviceGroup deviceGroup;

}
