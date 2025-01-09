package com.poc.consumer;

import java.util.List;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerRoute extends RouteBuilder {

  private final String fromRoute;
  private final int aggregationThreshold;
  private final long aggregationTimeoutMs;
  private final String outputFilePath;

  public KafkaConsumerRoute(
      @Value("${app.configuration.source-topic}") String sourceTopic,
      @Value("${app.configuration.aggregation-threshold}") int aggregationThreshold,
      @Value("${app.configuration.aggregation-timeout-ms}") int aggregationTimeoutMs,
      @Value("${app.configuration.output-file-path}") String outputFilePath) {
    this.fromRoute = "kafka:" + sourceTopic;
    this.aggregationThreshold = aggregationThreshold;
    this.aggregationTimeoutMs = aggregationTimeoutMs;
    this.outputFilePath = outputFilePath;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void configure() {
    onException(Exception.class)
        .log("Exception occurred: ${exception.message}")
        .handled(true)
        .to("log:error");

    from(fromRoute)
        .log("Received message for id ${headers.id}: ${body}")
        .aggregate(header("id"), new MessageAggregationStrategy())
        .completionSize(aggregationThreshold)
        .completionTimeout(aggregationTimeoutMs)
        .log("Batch for id ${headers.id} is ready, writing to file")
        .process(exchange -> {
          var messages = exchange.getIn().getBody(List.class);
          var combinedMessages = String.join("\n", messages)+"\n";
          exchange.getIn().setBody(combinedMessages);
        })
        .to("file:"+outputFilePath+"?fileName=messages-${headers.id}.txt&fileExist=Append")
        .log("Id ${headers.id} batch saved.");
  }
}
