package com.briansjavablog.kafka.sub.controller;

import com.briansjavablog.kafka.sub.MessagePublishService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class PublishController {

  private MessagePublishService messagePublishService;

  @GetMapping("/api/publish/{message}")
  public void publishMessage(@PathVariable("message") String message) {

    log.info("Message submitted [{}]", message);
    messagePublishService.sendMessage(message);
  }

}