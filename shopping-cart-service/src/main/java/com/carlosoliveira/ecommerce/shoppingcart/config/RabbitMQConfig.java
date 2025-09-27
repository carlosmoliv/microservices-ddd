package com.carlosoliveira.ecommerce.shoppingcart.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class RabbitMQConfig {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);
    public static final String CART_EVENTS_EXCHANGE = "cart_events_exchange";
    public static final String CART_ROUTING_KEY_PATTERN = "cart.*";


    @Bean
    public TopicExchange cartEventsExchange() {
        return new TopicExchange(CART_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setExchange(CART_EVENTS_EXCHANGE);
        template.setMessageConverter(messageConverter());
        template.setMandatory(true);
        return template;
    }
}
