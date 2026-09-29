package com.rushinga.dpdms.alert_service;

import java.beans.BeanProperty;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import java.util.List; 

@Configuration
public class RabbitMQConfig {

@Value("${app.rabbitmq.exchange}")
private String exchange;

@Value("${app.rabbitmq.queue}")
private String queue;

@Value("${app.rabbitmq.routing-key}")
private String routingKey;

@Bean
public TopicExchange exchange() {
return new TopicExchange(exchange);
}

@Bean
public Queue queue() {
return new Queue(queue);
}

@Bean
public Binding binding(Queue queue, TopicExchange exchange) {
return BindingBuilder.bind(queue).to(exchange).with(routingKey);
}

@Bean
public MessageConverter messageConverter() {
    SimpleMessageConverter converter = new SimpleMessageConverter();
    converter.setAllowedListPatterns(List.of("com.rushinga.dpdms.alert_service.*"));
    return converter;
}
}