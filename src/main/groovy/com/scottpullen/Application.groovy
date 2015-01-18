package com.scottpullen

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableAutoConfiguration
public class Application {

    @Autowired
    AnnotationConfigApplicationContext context

    @Bean
    CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost")
        connectionFactory.username = "guest"
        connectionFactory.password = "guest"
        return connectionFactory
    }

    @Bean
    RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory())
    }

    @Bean
    RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory())
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange("sp.topic.exchange2")
    }

    @Bean
    Queue queue() {
        return new Queue("my.queue", true)
    }

    @Bean
    Queue rpcQueue() {
        return new Queue("my.rpc.queue", true)
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with("my.routing.key")
    }

    @Bean
    Binding rpcBinding() {
        return BindingBuilder.bind(rpcQueue()).to(exchange()).with("test.rpc")
    }

    @Bean
    Consumer consumer() {
        return new Consumer()
    }

    @Bean
    RPCConsumer rpcConsumer() {
        return new RPCConsumer()
    }

    @Bean
    SimpleMessageListenerContainer container() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer()
        container.connectionFactory = connectionFactory()
        container.messageListener = consumer()
        container.queueNames = "my.queue"
        container.concurrentConsumers = 10
        return container
    }

    @Bean
    SimpleMessageListenerContainer rpcContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.connectionFactory = connectionFactory()
        container.messageListener = new MessageListenerAdapter(rpcConsumer())
        container.queueNames = "my.rpc.queue"
        return container
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args)
    }
}
