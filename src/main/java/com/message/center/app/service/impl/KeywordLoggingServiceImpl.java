package com.message.center.app.service.impl;

import com.message.center.app.domain.AlertKeyword;
import com.message.center.app.domain.ExchangeMessage;
import com.message.center.app.repository.AlertKeywordRepository;
import com.message.center.app.service.KeywordLoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class KeywordLoggingServiceImpl implements KeywordLoggingService {

  private final AlertKeywordRepository alertKeywordRepository;

  @Autowired
  public KeywordLoggingServiceImpl(AlertKeywordRepository alertKeywordRepository) {
    this.alertKeywordRepository = alertKeywordRepository;
  }

  @Override
  public void logExchangeMessage(ExchangeMessage exchangeMessage) {
    List<String> nameList = extractKeywords(exchangeMessage);
    if (!CollectionUtils.isEmpty(nameList)) {
      List<AlertKeyword> keywordsExtractedFromMessage = alertKeywordRepository.findAllByKeywordIn(nameList);
      exchangeMessage.setAlertKeywords(keywordsExtractedFromMessage);
    }
  }

  public List<String> extractKeywords(ExchangeMessage exchangeMessage) {
    String content = exchangeMessage.getContent();
    List<AlertKeyword> allKeywords = alertKeywordRepository.findAll();

    String regexPattern = allKeywords.stream()
        .map(keyword -> "\\b" + Pattern.quote(keyword.getKeyword()) + "\\b")
        .collect(Collectors.joining("|"));

    Pattern pattern = Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(content);

    List<String> matchedKeywords = new ArrayList<>();
    while (matcher.find()) {
      matchedKeywords.add(matcher.group());
    }
    return matchedKeywords;
  }
}
