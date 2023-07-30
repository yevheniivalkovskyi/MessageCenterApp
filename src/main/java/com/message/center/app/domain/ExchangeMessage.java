package com.message.center.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "exchange_message")
public class ExchangeMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "guid")
  @NotBlank
  @Size(max = 36)
  private String uuid = UUID.randomUUID().toString();

  @Column(name = "content")
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private ExchangeMessageType messageType;

  @Column(name = "timestamp")
  @NotNull
  private LocalDateTime timestamp;

  @ManyToOne
  private MessageDevice sender;

  @ManyToMany
  @JoinTable(
      name = "message_receivers",
      joinColumns = @JoinColumn(name = "message_id"),
      inverseJoinColumns = @JoinColumn(name = "receiver_id")
  )
  private List<MessageDevice> receivers;

  @ManyToMany
  @JoinTable(
      name = "message_keywords",
      joinColumns = @JoinColumn(name = "message_id"),
      inverseJoinColumns = @JoinColumn(name = "keyword_id")
  )
  private List<AlertKeyword> alertKeywords;
}
