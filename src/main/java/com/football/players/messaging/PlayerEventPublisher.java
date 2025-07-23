package com.football.players.messaging;

import com.football.players.config.RabbitConfig;
import com.football.players.dto.PlayerDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publica el evento player.created en RabbitMQ.
 */
@Component
public class PlayerEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PlayerEventPublisher.class);
    private final RabbitTemplate rabbit;

    public PlayerEventPublisher(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    public void publishPlayerCreated(PlayerDto dto) {
        try {
            log.info("Enviando evento player.created para jugador: {}", dto.name());
            rabbit.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY_CREATED,
                dto
            );
            log.info("Evento enviado exitosamente a RabbitMQ");
        } catch (Exception e) {
            log.error("Error al enviar evento a RabbitMQ: ", e);
        }
    }
}