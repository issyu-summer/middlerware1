package org.middleware.demo1.acitvemq.config.webSocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

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
    private Queue queue;

//    @Bean
//    public WebSocketConfigurer webSocketConfigurer(){
//        return registry -> registry.addHandler(
//                new TextWebSocketHandler(){
//                    //msg是从activeMq中获取的未被消费的消息
//                    @Override
//                    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//                        super.afterConnectionEstablished(session);
//                        log.info("和客户端建立连接");
//                        //如果有activeMq有未消费的消息
//                        TextMessage textMessage
//                                = jmsMessagingTemplate.receiveAndConvert(queue, TextMessage.class);
//
//
//                        if(!Objects.isNull(textMessage)) {
//                            log.info("监听到消息");
//                            String msg = textMessage.getText();
//                            session.sendMessage(
//                                    new org.springframework.web.socket.TextMessage(msg));
//                            log.info("通过webSocket传输至客户端完成");
//                            //如果在一段时间内没有监听到消息.那么就关闭这个连接
//                        }
//                    }
//
//                    @Override
//                    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//                        super.afterConnectionClosed(session, status);
//                        log.info("和客户端断开连接");
//                    }
//                });
//    }
}
