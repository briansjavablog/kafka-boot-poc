package com.briansjavablog.kafka.sub.config;


import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.adapter.RetryingMessageListenerAdapter;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;


@Slf4j
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

  @Value(value = "${kafka.bootstrapAddress}")
  private String bootstrapAddress;

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;


  public ConsumerFactory<String, String> consumerFactory(String groupId) {

    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    return new DefaultKafkaConsumerFactory<>(props);
  }

  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(String groupId) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory(groupId));
    factory.setRetryTemplate(retryTemplate());

    factory.setRecoveryCallback((context -> {

      log.info("Running Recovery handler - retry count [{}] last exception [{}]", context.getRetryCount(), context.getLastThrowable().getMessage());

      ConsumerRecord consumerRecord = (ConsumerRecord) context.getAttribute(RetryingMessageListenerAdapter.CONTEXT_RECORD);
      String message = (String)consumerRecord.value();
      String deadLetterTopic = "messageDeadLetterTopic";

      //here you can do your recovery mechanism where you can put back on to the topic using a Kafka producer
      log.info("Sending message to Dead letter Topic - message [{}] retry count [{}] last exception [{}]", message, context.getRetryCount(), context.getLastThrowable().getMessage());
      sendMessage(message, deadLetterTopic);

      return null;
    }));


    return factory;
  }


  public void sendMessage(String message, String topicName) {

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
        log.error("Error sending message [{}] to topic [{}]", message, topicName, ex);
      }
    });
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> messageGroupKafkaListenerContainerFactory() {
    return kafkaListenerContainerFactory("messageGroup");
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> messageDeadLetterGroupKafkaListenerContainerFactory() {

    return kafkaListenerContainerFactory("messageDeadLetterGroup");
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> logsGroupKafkaListenerContainerFactory() {
    return kafkaListenerContainerFactory("logs");
  }

  private RetryTemplate retryTemplate() {

    RetryTemplate retryTemplate = new RetryTemplate();
    Map<Class<? extends Throwable>, Boolean> exceptionMap = new HashMap<>();
    /* decide whether or not we want to retry these types of exception */
    exceptionMap.put(Exception.class, true);
    exceptionMap.put(RuntimeException.class, true);

    retryTemplate.setRetryPolicy(new SimpleRetryPolicy(3, exceptionMap,true));
    return retryTemplate;
  }

}
