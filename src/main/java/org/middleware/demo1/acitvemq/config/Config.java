package org.middleware.demo1.acitvemq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.jms.Queue;
import javax.jms.TextMessage;
import java.util.Objects;

/**
 * @author summer
 * @date 2021/3/14 19:13
 */
@Configuration
@EnableJms
@Slf4j
public class Config {

    /**
     * queueName
     */
    @Value("${queueName}")
    private String queueName;

    /**
     * json工具
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 消息队列
     * @return bean:queue
     */
    @Bean
    public Queue queue(){
        return new ActiveMQQueue(queueName);
    }

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private Queue queue;

    @Bean
    public WebSocketConfigurer webSocketConfigurer(){
     return registry -> registry.addHandler(
             new TextWebSocketHandler(){
                 //msg是从activeMq中获取的未被消费的消息
                 @Override
                 public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                     super.afterConnectionEstablished(session);
                     log.info("和客户端建立连接");
                     //如果有activeMq有未消费的消息
                     TextMessage textMessage
                             = jmsMessagingTemplate.receiveAndConvert(queue, TextMessage.class);

                     if(!Objects.isNull(textMessage)) {
                         log.info("监听到消息");
                         String msg = textMessage.getText();
                         session.sendMessage(
                                 new org.springframework.web.socket.TextMessage(msg));
                         log.info("通过webSocket传输至客户端完成");
                         //如果在一段时间内没有监听到消息.那么就关闭这个连接
                     }
                 }

                 @Override
                 public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                     super.afterConnectionClosed(session, status);
                     log.info("和客户端断开连接");
                 }
             });
    }
}
