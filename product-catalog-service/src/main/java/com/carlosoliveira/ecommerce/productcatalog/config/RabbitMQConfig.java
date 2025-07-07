package com.carlosoliveira.ecommerce.productcatalog.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CART_EVENTS_EXCHANGE = "cart_events_exchange";
    public static final String CART_EVENTS_QUEUE = "cart_events_queue";
    public static final String CART_ROUTING_KEY_PATTERN = "cart.*";

    @Bean
    public Queue productRpcQueue() {
        return new Queue("product_queue", false);
    }

    @Bean
    public Queue cartEventsQueue() {
        return new Queue(CART_EVENTS_QUEUE, true, false, false);
    }

    @Bean
    public TopicExchange cartEventsExchange() {
        return new TopicExchange(CART_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public Binding cartEventsBinding(Queue cartEventsQueue, TopicExchange cartEventsExchange) {
        return BindingBuilder.bind(cartEventsQueue)
                .to(cartEventsExchange)
                .with(CART_ROUTING_KEY_PATTERN);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
