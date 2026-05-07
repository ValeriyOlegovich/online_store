package ru.v_and_a.rabbitMQ.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // события отмена заказа
    public static final String ORDER_CANCELLED_EVENT_QUEUE = "order.cancelled.event.queue";
    public static final String ORDER_EXCHANGE = "order.exchange"; // можно использовать один exchange для всех событий заказа
    public static final String ORDER_CANCELLED_ROUTING_KEY = "order.order.cancelled";

    @Bean
    public Queue paymentRequestQueue() {
        return QueueBuilder.durable(ORDER_CANCELLED_EVENT_QUEUE).build();
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Binding paymentBinding() {
        return BindingBuilder
                .bind(paymentRequestQueue())
                .to(paymentExchange())
                .with(ORDER_CANCELLED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
