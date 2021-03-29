package com.briansjavablog.kafka.sub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Service
public class MessagePublishService {

    private KafkaTemplate<String, String> kafkaTemplate;
    private String topicName;

    public MessagePublishService(KafkaTemplate<String, String> kafkaTemplate,
                                 @Value("${message.topic.name}") String topicName){

        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void sendMessage(String message) {

        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {

                log.info("Sent Message [{}] Topic [{}] Partition [{}] Offset [{}]",
                            message, result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Error sending message [{}]", message, ex);
            }
        });
    }
}