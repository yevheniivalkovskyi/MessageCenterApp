package com.message.center.app.controller;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.message.center.app.domain.ExchangeMessageType;
import com.message.center.app.dto.CreateExchangeMessageRequest;
import com.message.center.app.dto.exchange.ExchangeMessageDto;
import com.message.center.app.dto.keyword.AlertKeywordDto;
import com.message.center.app.service.ExchangeMessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ExchangeMessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ExchangeMessageService exchangeMessageService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void testCreateExchangeMessage_ValidRequest_ReturnsOkStatus() throws Exception {
    // Arrange
    CreateExchangeMessageRequest request = new CreateExchangeMessageRequest();
    request.setSenderId("senderId");
    request.setReceiverId("receiverId");
    request.setMessageType(ExchangeMessageType.P2P);
    request.setContent("Test message content");

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.post("/exchange-messages")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void testGetAllExchangeMessages_ReturnsListOfExchangeMessages() throws Exception {
    // Arrange
    List<ExchangeMessageDto> exchangeMessages = List.of(
        new ExchangeMessageDto("uuid1", ExchangeMessageType.P2P, "Test message content 1", LocalDateTime.now(),
            "sender@gmail.com", List.of("receiverId3", "receiverId4"), List.of(new AlertKeywordDto("java"))));
    new ExchangeMessageDto("uuid2", ExchangeMessageType.BROADCAST, "Test message content 2", LocalDateTime.now(),
        "sender2@gmail.com", List.of("receiverId2", "receiverId1"), List.of(new AlertKeywordDto("c++")));
    when(exchangeMessageService.getAll()).thenReturn(exchangeMessages);

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.get("/exchange-messages"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(exchangeMessages.size()));
  }

  @Test
  public void testGetExchangeMessageById_ExistingMessage_ReturnsExchangeMessage() throws Exception {
    // Arrange
    ExchangeMessageDto exchangeMessage = new ExchangeMessageDto("uuid", ExchangeMessageType.P2P,
        "Test message content 1", LocalDateTime.now(),
        "sender@gmail.com", List.of("receiverId3", "receiverId4"), List.of(new AlertKeywordDto("java")));

    when(exchangeMessageService.getById("uuid")).thenReturn(exchangeMessage);

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.get("/exchange-messages/{message-id}", "uuid"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.uuid").value(exchangeMessage.getUuid()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.senderEmail").value(exchangeMessage.getSenderEmail()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.receiverEmails.length()")
                .value(exchangeMessage.getReceiverEmails().size()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(exchangeMessage.getContent()));
  }

  @Test
  public void testGetExchangeMessagesBySender_ExistingSender_ReturnsListOfExchangeMessages() throws Exception {
    // Arrange
    String senderEmail = "sender@gmail.com";
    List<ExchangeMessageDto> exchangeMessages = List.of(
        new ExchangeMessageDto("uuid1", ExchangeMessageType.P2P, "Test message content 1", LocalDateTime.now(),
            senderEmail, List.of("receiverId3", "receiverId4"), List.of(new AlertKeywordDto("java"))));
    new ExchangeMessageDto("uuid2", ExchangeMessageType.BROADCAST, "Test message content 2", LocalDateTime.now(),
        senderEmail, List.of("receiverId2", "receiverId1"), List.of(new AlertKeywordDto("c++")));
    when(exchangeMessageService.getExchangeMessagesBySender(senderEmail)).thenReturn(exchangeMessages);

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.get("/exchange-messages/by-sender/{sender-id}", senderEmail))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(exchangeMessages.size()));
  }

  @Test
  public void testGetExchangeMessagesByReceiver_ExistingReceiver_ReturnsListOfExchangeMessages() throws Exception {
    // Arrange
    String receiverId = "receiverId";
    String senderEmail = "sender@gmail.com";
    List<ExchangeMessageDto> exchangeMessages = List.of(
        new ExchangeMessageDto("uuid1", ExchangeMessageType.P2P, "Test message content 1", LocalDateTime.now(),
            senderEmail, List.of("receiverId3", receiverId), List.of(new AlertKeywordDto("java"))));
    new ExchangeMessageDto("uuid2", ExchangeMessageType.BROADCAST, "Test message content 2", LocalDateTime.now(),
        senderEmail, List.of("receiverId2", receiverId), List.of(new AlertKeywordDto("c++")));
    when(exchangeMessageService.getExchangeMessagesByReceiver(receiverId)).thenReturn(exchangeMessages);

    // Act & Assert
    mockMvc.perform(MockMvcRequestBuilders.get("/exchange-messages/by-receiver/{receiver-id}", receiverId))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(exchangeMessages.size()));
  }
}
