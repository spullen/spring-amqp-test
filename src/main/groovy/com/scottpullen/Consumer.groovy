package com.scottpullen

import com.rabbitmq.client.Channel
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired

public class Consumer implements ChannelAwareMessageListener {

    @Autowired
    RabbitTemplate rabbitTemplate

    @Autowired
    TopicExchange exchange

    @Override
    public void onMessage(Message message, Channel channel) {
        println(exchange.name)
        println("Received: ${message.body}")
        Object j = rabbitTemplate.convertSendAndReceive(exchange.name, "test.rpc", "Test Data")
        println(j.toString())
    }
}
