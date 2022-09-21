package com.ilongli.rabbitmq.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@SpringBootApplication
@RestController
@Slf4j
public class RabbitmqPublisherApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqPublisherApplication.class, args);
    }

    @Resource
    private RabbitTemplate rabbitTemplate;

    @GetMapping("test")
    public String test() {
        String routingKey = "simple";
        String message = "谁TM买小米啊";

        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());

        correlationData.getFuture().addCallback(confirm -> {
            if (confirm.isAck()) {
                log.debug("消息成功投递到交换机！消息ID：{}", correlationData.getId());
            } else {
                log.error("消息投递到交换机失败！消息ID：{}", correlationData.getId());
            }
        }, throwable -> {
            log.error("消息发送失败！", throwable);
        });


        rabbitTemplate.convertAndSend("test.topic", routingKey, message, correlationData);
        return "test-ok";
    }

    @GetMapping("test-ttl")
    public String testTtl() {

        String routingKey = "ttl";
        String message = "谁TM买小米啊(大概8点50分发)";
        Message m = MessageBuilder
                .withBody(message.getBytes(StandardCharsets.UTF_8))
                .setExpiration("5000")
                .build();

        rabbitTemplate.convertAndSend("ttl.direct", routingKey, m);
        return "test-ok";
    }

    @GetMapping("test-delayed")
    public String testDelayed() {

        String routingKey = "delay";
        String message = "谁TM买小米啊(延迟5s后发)";
        Message m = MessageBuilder
                .withBody(message.getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .setHeader("x-delay", "5000")
                .build();

        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());

        rabbitTemplate.convertAndSend("test.delayed", routingKey, m, correlationData);

        return "test-ok";
    }
}
