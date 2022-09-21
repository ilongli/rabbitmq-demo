package com.ilongli.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author ilongli
 * @date 2022/8/15 19:37
 */
@Component
@Slf4j
public class MyRabbitListeners {

    @RabbitListener(queues = "simple.queue")
    public void test(String msg) {
        log.info("simple.queue消息: {}", msg);
        System.out.println(1 / 0);
        log.info("消费者消息处理成功");
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "error.queue", durable = "true"),
            exchange = @Exchange(
                    value = "error.direct",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.DIRECT
            ),
            key = "error"
    ))
    public void error (String msg) {
        log.error("接收到错误消息: {}", msg);
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "dl.queue", durable = "true"),
            exchange = @Exchange(name = "dl.direct"),
            key = "dl"
    ))
    public void dlQueue(String msg) {
        log.info("dl.queue延迟消息: {}", msg);
    }


    /**
     * 延迟队列（插件实现）
     * @param msg
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "delayed.queue", durable = "true"),
            exchange = @Exchange(name = "test.delayed", delayed = "true"),
            key = "delay"
    ))
    public void delayedQueue(String msg) {
        log.info("延迟队列消息: {}", msg);
    }


    /**
     * 惰性队列
     * @param msg
     */
    @RabbitListener(queuesToDeclare = @Queue(
            name = "lazy.queue",
            durable = "true",
            arguments = @Argument(name = "x-queue-mode", value = "lazy")
    ))
    public void lazyQueue(String msg) {
        log.info("惰性队列消息: {}", msg);
    }

}
