package com.ilongli.rabbitmq.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
}
