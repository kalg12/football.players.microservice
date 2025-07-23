package com.football.players.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE_NAME       = "football.exchange";
    public static final String QUEUE_PLAYER_CREATED = "player.created.queue";
    public static final String ROUTING_KEY_CREATED  = "player.created";

    @Bean Exchange footballExchange() {
      return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
    }

    @Bean Queue playerCreatedQueue() {
      return QueueBuilder.durable(QUEUE_PLAYER_CREATED).build();
    }

    @Bean Binding binding(Queue q, Exchange ex) {
      return BindingBuilder.bind(q).to(ex).with(ROUTING_KEY_CREATED).noargs();
    }

    /**
     * Configurar RabbitTemplate para usar Jackson2JsonMessageConverter
     * para serializar objetos Java a JSON
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }
}