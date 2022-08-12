package com.ilongli.rabbitmq.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * @author ilongli
 * @date 2022/8/12 17:21
 */
@Slf4j
@Configuration
public class RabbitMQConfig implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.debug("{} - {} - {} - {} - {}", message, replyCode, replyText, exchange, routingKey);
        });
    }

/*   @Bean
   public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
       RabbitTemplate rabbitTemplate = new RabbitTemplate();
       rabbitTemplate.setConnectionFactory(connectionFactory);
       rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.debug("{} - {} - {} - {} - {}", message, replyCode, replyText, exchange, routingKey);
       });
       return rabbitTemplate;
   }*/

}
