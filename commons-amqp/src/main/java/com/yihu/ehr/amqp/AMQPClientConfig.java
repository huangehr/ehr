package com.yihu.ehr.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列客户端配置，接收消息。
 */
@Configuration
public class AMQPClientConfig {
    @Value("${amqp.queue-name}")
    String queueName;

    @Value("${amqp.queue-durable}")
    boolean durable = false;

    @Value("${amqp.exchange-name}")
    String exchange;

    @Bean
    Queue queue() {
        return new Queue(queueName, durable);
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange){
        return new BindingBuilder().bind(queue).to(exchange).with(queueName);
    }

    @Bean
    MessageListenerAdapter listenerAdapter(XReceiver receiver){
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory factory, MessageListenerAdapter listenerAdapter){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);

        return container;
    }
}
