package com.briansjavablog.kafka.sub.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MessageSubscribeService {

    // retry
    // recover
    // error handler

    @Getter
    private List<String> messages = new ArrayList<>();

    @Getter
    private List<String> deadLetterMessages = new ArrayList<>();

    @Getter
    private List<String> logs = new ArrayList<>();

    @KafkaListener(topics = "${message.topic.name}",
                   groupId = "messageGroup",
                   containerFactory = "messageGroupKafkaListenerContainerFactory")
    public void listenMessages(String message,  @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition, @Header(KafkaHeaders.GROUP_ID) String groupId) throws Exception {

        log.info("Received Message (message topic) [{}] in group [{}] partition [{}]", message, groupId, partition);

        /* trigger an error here so that we can test Retries */
        if(message.contains("error")){
            log.error("Message contained error...throwing exception...");
            throw new Exception("Oops....an exception has occurred");
        }

        messages.add(message);
    }

    @KafkaListener(topics = "messageDeadLetterTopic",
                    groupId = "messageDeadLetterGroup",
                    containerFactory = "messageDeadLetterGroupKafkaListenerContainerFactory")
    public void listenMessagesDeadLetterTopic(String message,  @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition, @Header(KafkaHeaders.GROUP_ID) String groupId) throws Exception {

        log.info("Received Message (message DLT) [{}] in group [{}] partition [{}]", message, groupId, partition);

        deadLetterMessages.add(message);
    }

    @KafkaListener(topics = "logs",
            groupId = "logsGroup",
            containerFactory = "logsGroupKafkaListenerContainerFactory")
    public void listenLogsGroup(String message) {

        log.info("Received Message [{}] in group [logsGroup]", message);
        logs.add(message);
    }



}
