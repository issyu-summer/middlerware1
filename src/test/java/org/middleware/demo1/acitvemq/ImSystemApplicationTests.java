package org.middleware.demo1.acitvemq;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import javax.jms.*;
import java.util.LinkedList;
import java.util.Objects;

@SpringBootTest
class ImSystemApplicationTests {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private Queue queue;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void send() {
        jmsMessagingTemplate.convertAndSend(queue,"jack:小明你在吗？");
        jmsMessagingTemplate.convertAndSend(queue,"jack:周日下午有时间吗？");
        jmsMessagingTemplate.convertAndSend(queue,"jack:我们一起去吃饭啊");
        jmsMessagingTemplate.convertAndSend(queue,"jack:我听菜虚困说,你想叛变革命?");
        jmsMessagingTemplate.convertAndSend(queue,"jack:原来是它不给你革命领导,那没事了");

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

    static int i=1;
    @Test
    @Scheduled(fixedDelay = 5000)
    void Scheduled(){
        jmsMessagingTemplate.convertAndSend(queue,"每隔5s定时投递消息："+i);
    }

    //会一直处于监听状态，有则立即消费。
    //当没有时怎么暂时关闭连接？
    @JmsListener(destination = "${queueName}")
    void receiveByListener(TextMessage msg) throws JMSException {
        if(msg != null) {
            System.out.println(msg.getText());
        }
    }

    /**
     * 异步发送测试
     *
     * 异步发送时,发送者会通过回调函数确认会消息是否成功发送至activeMQ
     * 经过debug,springboot集成activeMq时,默认会走producer.send（T...t,(AsyncCallBack)null）,即回调函数为null,即并不会走回调函数,
     * 如下：public class ActiveMQMessageProduce中的
     * public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
     *         this.send(destination, message, deliveryMode, priority, timeToLive, (AsyncCallback)null);
     *     }
     *
     * eg:小明给小红发了1条消息（在吗？）,小红没看见,一直没回复。此时小明相当于没有收到消息的回调结果，因而小明无法确定小红是否接收了消息
     *
     * 那么如何确定消息发送至activeMq成功（小红接收到了消息）？见下一个测试
     */
    @Test
    void async(){
        //默认异步开启？？？？
        jmsMessagingTemplate.convertAndSend(queue,"测试消息1");
        System.out.println();
    }

    /**
     * 将线程池关闭,将所有配置还原
     * 默认的配置
     */
    @Test
    public void defaultConfig(){


        System.out.println("事务是否开启:"+jmsMessagingTemplate.getJmsTemplate().isSessionTransacted());
        //非持久化消息需要配置explicitQosEnabled该属性为true,如果是false则证明是持久化消息
        System.out.println("是否为持久化消息:"+!jmsMessagingTemplate.getJmsTemplate().isExplicitQosEnabled());
        //同步方式的传输：消息一定会发送到activeMq

        //得出结论：如果是case:**persistence** but **outside of a transaction**持久化但不使用事务，则以同步方式传输
        //因而上一个测试的结果就是springboot集成activeMq,默认是使用同步的发送方式。
    }


    /**
     * 新的问题1:
     * 按照默认的配置,消费者消费activeMq存在消息一定会成功吗？
     */
    @Test
    public void consumer(){

    }

    /**
     * 新的问题2:
     * 如果开启异步？
     * 关键点在：class ActiveMQSession中的send()方法中的if(使用异步发送)
     * 如下：
     * boolean 使用异步发送=(!msg.isPersistent() || this.connection.isUseAsyncSend() || txid != null)==true
     * 三个条件任何一个为真即可，
     * 如果配置？？？
     */
    @Test
    public void asyncConfig(){

    }

}
