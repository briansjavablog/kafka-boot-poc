package com.briansjavablog.kafka.sub.controller;

import com.briansjavablog.kafka.sub.service.MessageSubscribeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class SubscribeController {

  private MessageSubscribeService messageSubscribeService;

  @GetMapping("/api/messages")
  public ResponseEntity<List<String>> getMessages() {

    return ResponseEntity.ok(messageSubscribeService.getMessages());
  }

  @GetMapping("/api/dlt-messages")
  public ResponseEntity<List<String>> getDeadLetterTopicMessages() {

    return ResponseEntity.ok(messageSubscribeService.getDeadLetterMessages());
  }

  @GetMapping("/api/logs")
  public ResponseEntity<List<String>> getLogMessages() {

    return ResponseEntity.ok(messageSubscribeService.getLogs());
  }

}
