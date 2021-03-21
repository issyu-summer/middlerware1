package org.middleware.demo1.acitvemq.config.webSocket;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;

/**
 * @author summer
 * @date 2021/3/16 8:42
 */
@Configuration
@Slf4j
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }

    @Autowired
    private SimulateListener simulateListener;

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Bean
    public DefaultMessageListenerContainer listenerContainer(){
        DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        ConnectionFactory connectionFactory = jmsMessagingTemplate.getConnectionFactory();
        messageListenerContainer.setConnectionFactory(connectionFactory);
        //destination is required,so simulate a topic for springboot running
        messageListenerContainer.setDestination(new ActiveMQTopic("topic01"));
        messageListenerContainer.setMessageListener(new SimulateListener());
        return messageListenerContainer;
    }

}
