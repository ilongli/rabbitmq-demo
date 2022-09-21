package com.ilongli.rabbitmq.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
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

            if (message.getMessageProperties().getReceivedDelay() > 0) {
                // 是一个延迟消息，忽略这个错误提示
                return;
            }

            log.debug("{} - {} - {} - {} - {}", message, replyCode, replyText, exchange, routingKey);
        });
    }


    // region # ttl

    @Bean
    public DirectExchange ttlExchange() {
        return new DirectExchange("ttl.direct");
    }

    @Bean
    public Queue ttlQueue() {
        return QueueBuilder.durable("ttl.queue")
                .ttl(10000)
                .deadLetterExchange("dl.direct")
                .deadLetterRoutingKey("dl")
                .build();
    }

    @Bean
    public Binding simpleBinding() {
        return BindingBuilder.bind(ttlQueue()).to(ttlExchange()).with("ttl");
    }

    // endregion


    // region # delayed-queue

/*    @Bean
    public DirectExchange delayedExchange() {
        return ExchangeBuilder
                .directExchange("test.delayed")
                .delayed()
                .durable(true)
                .build();
    }*/

    // endregion


    // region # lazy-queue
    @Bean
    public Queue lazyQueue() {
        return QueueBuilder
                .durable("lazy.queue")
                .lazy()
                .build();
    }

    // endregion


    // region # 仲裁队列

    @Bean
    public Queue quorumQueue() {
        return QueueBuilder
                .durable()  // 持久化
                .quorum()   // 仲裁队列
                .build();
    }

    // endregion

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
