package com.poc.consumer;

import java.util.ArrayList;
import java.util.List;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

public class MessageAggregationStrategy implements AggregationStrategy {

  @Override
  @SuppressWarnings("unchecked")
  public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
    if (oldExchange == null) {
      oldExchange = newExchange.copy();
      oldExchange.getIn().setBody(new ArrayList<String>());
    }
    var message = newExchange.getIn().getBody(String.class);
    var messages = oldExchange.getIn().getBody(List.class);
    messages.add(message);
    oldExchange.getIn().setBody(messages);
    return oldExchange;
  }
}

