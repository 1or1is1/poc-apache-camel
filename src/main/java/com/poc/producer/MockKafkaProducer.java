package com.poc.producer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.stereotype.Component;

@Component
public class MockKafkaProducer {

  private static final Logger log = LoggerFactory.getLogger(MockKafkaProducer.class);

  private final Producer<String, String> producer;
  private final AtomicInteger counter = new AtomicInteger();
  private final String topic;
  private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

  public MockKafkaProducer(
      DefaultKafkaProducerFactory<String, String> defaultKafkaProducerFactory,
      @Value("${app.configuration.source-topic}") String sourceTopic) {
    this.producer = defaultKafkaProducerFactory.createProducer();
    this.topic = sourceTopic;
  }

  public void produceMessages() {
    executor.scheduleAtFixedRate(() -> {
      try {
        counter.updateAndGet(value -> value % 2);
        var message = "Message %f".formatted(Math.random());
        var record = new ProducerRecord<String, String>(topic, message);
        record.headers().add("id", String.valueOf(counter.get()).getBytes());
        log.info("Sending message to kafka : {}", message);
        producer.send(record, (metadata, exception) -> {
          if (exception != null) {
            log.error("Some error occurred.", exception);
          }
        });

        counter.getAndIncrement();
      } catch (Exception e) {
        shutdown();
      }
    }, 2000, 2000, TimeUnit.MILLISECONDS);
  }

  public void shutdown() {
    executor.shutdown();
    try {
      if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
    }
  }

}
