package com.briansjavablog.kafka.sub.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MessageSubscribeService {

    @Getter
    private List<String> messages = new ArrayList<>();

    @KafkaListener(topics = "${message.topic.name}",
                   groupId = "testGroup",
                   containerFactory = "testGroupKafkaListenerContainerFactory")
    public void listenTestGroup(String message) throws Exception {

        log.info("Received Message [{}] in group [testGroupX]", message);

        /* trigger an error here so that we can test Retries */
        if(message.contains("error")){
            log.error("Oops....an error has occurred");
            throw new Exception("Oops....an error has occurred");
        }

        messages.add(message);
    }

    @KafkaListener(topics = "logs",
            groupId = "logsGroup",
            containerFactory = "logsGroupKafkaListenerContainerFactory")
    public void listenLogsGroup(String message) {

        log.info("Received Message [{}] in group [logsGroup]", message);
        messages.add(message);
    }
}