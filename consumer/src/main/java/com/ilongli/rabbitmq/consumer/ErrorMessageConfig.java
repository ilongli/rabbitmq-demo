package com.ilongli.rabbitmq.consumer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ilongli
 * @date 2022/8/15 20:00
 */
@Configuration
public class ErrorMessageConfig {

    @Bean
    public MessageRecoverer republishMessageRecover(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", "error");
    }

}
