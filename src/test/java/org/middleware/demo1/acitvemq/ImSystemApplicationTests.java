package org.middleware.demo1.acitvemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import java.util.LinkedList;
import java.util.Objects;

@SpringBootTest
class ImSystemApplicationTests {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private Queue queue;

    @Test
    void send() {
        jmsMessagingTemplate.convertAndSend(queue,"测试消息1");
        jmsMessagingTemplate.convertAndSend(queue,"测试消息2");
        jmsMessagingTemplate.convertAndSend(queue,"测试消息3");
    }

    @Test
    void receiveAll(){

        //jmsMessageTemplate设置超时时间
        Objects.requireNonNull(jmsMessagingTemplate.getJmsTemplate()).setReceiveTimeout(5000);

        java.util.Queue<String> queue1=new LinkedList<>();
        String msg= jmsMessagingTemplate.receiveAndConvert(queue, String.class);
        while (Objects.nonNull(msg)){
            queue1.offer(msg);
            //如果不设置超时，当队列中没有消息时，会一直重复获取消息，导致阻塞
            msg=jmsMessagingTemplate.receiveAndConvert(queue, String.class);
        }
        for(String s:queue1){
            System.out.println(s);
        }

    }

}
