package org.middleware.demo1.acitvemq.config.webSocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * ws://localhost:8080/websocket/{username}/{identityName}
 * identityName的约定格式如下：
 * 队列:queue01,queue02,queue03,...(私聊建立队列queue)
 * 主题:topic01,topic02,topic02,...(群聊建立主题,如果不想和群名相同,可以在群中另外增加一个字段)
 *
 * 此处不能直接自动注入,会导致空指针异常：
 * 原因如下
 * spring管理的都是单例（singleton）和 websocket （多对象）相冲突。
 *
 * 需要了解一个事实：websocket 是多对象的，每个用户的聊天客户端对应 java 后台的一个 websocket 对象，前后台一对一（多对多）实时连接，
 * 所以 websocket 不可能像 servlet 一样做成单例的，让所有聊天用户连接到一个 websocket对象，这样无法保存所有用户的实时连接信息。
 * 可能 spring 开发者考虑到这个问题，没有让 spring 创建管理 websocket ，而是由 java 原来的机制管理websocket ，所以用户聊天时创建的
 * websocket 连接对象不是 spring 创建的，spring 也不会为不是他创建的对象进行依赖注入，所以如果不用static关键字，每个 websocket 对象的 service 都是 null。
 *
 *从springboot上下文中取出
 *
 * @author summer
 * @date 2021/3/15 21:56
 */
@Component
@ServerEndpoint("/websocket/{username}/{identityName}")
@Slf4j
public class WebSocketServer {


    /**
     * json工具
     */
    private ObjectMapper objectMapper
            =ApplicationContextUtil.getApplicationContext().getBean("objectMapper",ObjectMapper.class);

    /**
     * jsm消息队列模板
     */
    private JmsMessagingTemplate jmsMessagingTemplate=
            ApplicationContextUtil.getApplicationContext().getBean("jmsMessagingTemplate",JmsMessagingTemplate.class);

    /**
     * 会话缓存池
     * key的约定格式:
     *    queue:username
     */
    private static final Map<String,Session> SESSION_CACHE =new HashMap<>();

    /**
     * 当webSocket连接打开时,获取所有未被消费的消息,按照格式发送到前端
     * @param session server和client之间的绘画
     * @param username client's user
     */
    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("username") String username,
            @PathParam("identityName") String identityName) {
        String cacheMapKey=identityName+":"+username;
        SESSION_CACHE.put(cacheMapKey,Objects.requireNonNull(session));
        String initJson="初始化所有消息";
        session.getAsyncRemote().sendText(initJson);
        if(isQueue(identityName)){
            log.info("queueName:"+identityName+" user:"+username+"与服务器建立连接成功");
        }else {
            log.info("topicName:"+identityName+" user:"+username+"与服务器建立连接成功");
        }
    }

    /**
     * 必须传输一个String msg作为载荷,否则会报no payload的异常
     * @param receiverName 接收者
     * @param username username
     * @param identityName identityName
     */
    @OnMessage
    public void onMsg(String receiverName,@PathParam("username") String username,@PathParam("identityName") String identityName){
        log.info("从activeMq接收所有未被消费的消息");
        Queue<String> msgQueue;
        if(isQueue(identityName)){
            msgQueue=this.receiveAllMsgFromQueue(identityName);
        }else {
            msgQueue=this.receiveAllMsgFromTopic(identityName);
        }
        //int size = msgQueue.size();
        //每条消息都是一个json,一共需要发size次
//        List<String> jsonList=msgList.stream().map(e-> {
//            try {
//                return objectMapper.writeValueAsString(e);
//            } catch (JsonProcessingException jsonProcessingException) {
//                jsonProcessingException.printStackTrace();
//            }
//            return null;
//        }).collect(Collectors.toList());
        String json=this.convert(msgQueue,username);
        //是jack想发送给peter,不是发送给他自己。
        String cacheMapKey=identityName+":"+receiverName;
        Session session = SESSION_CACHE.get(cacheMapKey);
        if(json!=null){
            session.getAsyncRemote().sendText(json);
            log.info("发送消息至客户端:"+json);
        }
    }

    @OnClose
    public void onClose(@PathParam("username") String username,
                        @PathParam("identityName") String identityName){
        String cacheMapKey=identityName+":"+username;
        SESSION_CACHE.remove(cacheMapKey);
        if(isQueue(identityName)){
            log.info("queueName:"+identityName+" user:"+username+"与服务器断开连接成功");
        }else {
            log.info("topicName:"+identityName+" user:"+username+"与服务器断开连接成功");
        }
    }

    /**
     * Queue to json
     * @return 接收到消息的json
     */
    private String convert(Queue<String> msgQueue, String username){
        AtomicInteger order = new AtomicInteger();
        List<Msg> msgList = new ArrayList<>();
        if(!msgQueue.isEmpty()){
            msgList =msgQueue.stream().map(e-> new Msg(username, e, order.getAndIncrement())).collect(Collectors.toList());
        }
        try {
            return objectMapper.writeValueAsString(msgList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 从activeMq队列中接收所有消息
     */
    private Queue<String> receiveAllMsgFromQueue(String queueName){
        Objects.requireNonNull(jmsMessagingTemplate.getJmsTemplate()).setReceiveTimeout(5000);

        Queue<String> queue1=new LinkedList<>();
        String msg= jmsMessagingTemplate.receiveAndConvert(new ActiveMQQueue(queueName), String.class);
        while (Objects.nonNull(msg)){
            queue1.offer(msg);
            //如果不设置超时，当队列中没有消息时，会一直重复获取消息，导致阻塞
            msg=jmsMessagingTemplate.receiveAndConvert(new ActiveMQQueue(queueName), String.class);
        }
        return queue1;
    }

    /**
     * 从activeMq主题中接收所有消息
     */
    private Queue<String> receiveAllMsgFromTopic(String topicName){
        Objects.requireNonNull(jmsMessagingTemplate.getJmsTemplate()).setReceiveTimeout(5000);

        Queue<String> queue1=new LinkedList<>();
        String msg= jmsMessagingTemplate.receiveAndConvert(new ActiveMQTopic(topicName), String.class);
        while (Objects.nonNull(msg)){
            queue1.offer(msg);
            //如果不设置超时，当队列中没有消息时，会一直重复获取消息，导致阻塞
            msg=jmsMessagingTemplate.receiveAndConvert(new ActiveMQQueue(topicName), String.class);
        }
        return queue1;
    }
    /**
     * 服务器主懂推送到client
     */
    private void sendMessage(String msg,Session session) throws IOException {
        session.getBasicRemote().sendText(msg);
    }

    /**
     * 判断identityName是队列名还是主体名称
     * @param identityName 标识名称
     * @return queue:true topic:false
     */
    private boolean isQueue(String identityName){
        return identityName.contains("queue");
    }

    @AllArgsConstructor
    @Data
    @Accessors
    @NoArgsConstructor
    static class  Msg{
        private String username;
        private String msg;
        private Integer order;
    }
}
