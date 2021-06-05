package org.middleware.demo1.acitvemq.config.webSocket;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.middleware.demo1.acitvemq.entity.po.User;
import org.middleware.demo1.acitvemq.entity.po.UserGroup;
import org.middleware.demo1.acitvemq.service.UserGroupService;
import org.middleware.demo1.acitvemq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.*;
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
     * msgListenerContainer
     */
    private DefaultMessageListenerContainer msgListenerContainer
            =ApplicationContextUtil.getApplicationContext().getBean("listenerContainer",DefaultMessageListenerContainer.class);
    /**
     * jsm消息队列模板
     */
    private JmsMessagingTemplate jmsMessagingTemplate=
            ApplicationContextUtil.getApplicationContext().getBean("jmsMessagingTemplate",JmsMessagingTemplate.class);

    private UserGroupService userGroupService =
            ApplicationContextUtil.getApplicationContext().getBean(UserGroupService.class);

    private UserService userService=
            ApplicationContextUtil.getApplicationContext().getBean(UserService.class);

    /**
     * 会话缓存池
     * key的约定格式:
     *    queue:username
     */
    private static final Map<String,Session> SESSION_CACHE =new HashMap<>();

    /**
     * 会话数量标志
     */
    private static int sessionNum=0;

    /**
     * topic即群聊消息,jack发给peter和park,则jack需要发sessionNum-1次，
     * 即msgNum%sessionNum==0
     */
    private static int msgNum=0;

    /**
     *处理客服的标识
     */
    private static final String STAFF_IDENTIFY="staff_allocate";

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
        if(!isQueue(identityName)) {
            this.initListenerContainer(identityName);
        }
        String cacheMapKey=identityName+":"+username;
        SESSION_CACHE.put(cacheMapKey,Objects.requireNonNull(session));
        String initJson;
        if(isQueue(identityName)){
            log.info("queueName:"+identityName+" user:"+username+"与服务器建立连接成功");
            sessionNum++;
            log.info("sessionNum:"+sessionNum);
            initJson=this.convert(this.receiveAllMsgFromQueue(identityName),username);
        }else {
            log.info("topicName:"+identityName+" user:"+username+"与服务器建立连接成功");
            sessionNum++;
            log.info("sessionNum:"+sessionNum);
            //此处
            initJson=this.convert(this.receiveAllMsgFromQueue(identityName),username);
        }
        log.info("消息窗口初始化中:"+initJson);
        //如果时群聊,之前的聊天记录不会存在
        if(isQueue(identityName)) {
            session.getAsyncRemote().sendText(initJson);
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
        System.out.println(receiverName);
        String[] split = receiverName.split(",");
        //进入处理客服分配的逻辑
        if(split.length==2){
            String staffID=split[1];
            String cacheMapKey=identityName+":"+staffID;
            Session session;
            if (SESSION_CACHE.containsKey(cacheMapKey)) {
                session=SESSION_CACHE.get(cacheMapKey);                //
                session.getAsyncRemote().sendText("{\"groupId\":-1}");
            }
        }
        if(split.length==3){
            if (split[0].equals(STAFF_IDENTIFY)){
                String staffID=split[1];
                /* *********以下********* */
                Long groupId = Long.valueOf(split[2]);
                List<UserGroup> userGroups = userGroupService.list(new QueryWrapper<UserGroup>().eq("group_id", groupId));
                List<Long> ids = userGroups.stream().mapToLong(UserGroup::getUserId).boxed().collect(Collectors.toList());
                List<User> list = userService.list(new QueryWrapper<User>().in("id", ids).eq("type", 4));
//                Long id = list.get(0).getId();
                /* *********以上********* */
                String cacheMapKey=identityName+":"+staffID;
                Session session;
                if (SESSION_CACHE.containsKey(cacheMapKey)) {
                    session=SESSION_CACHE.get(cacheMapKey);
                    session.getAsyncRemote().sendText("{\"groupId\":"+split[2]+"}");
                }
                cacheMapKey=identityName+":"+4;
                if(SESSION_CACHE.containsKey(cacheMapKey)){
                    session=SESSION_CACHE.get(cacheMapKey);
                    session.getAsyncRemote().sendText("{\"groupId\":"+split[2]+"}");
                }
            }
            //进入聊天的逻辑
        }else {
            String cacheMapKey=identityName+":"+receiverName;
            Session session;
            if(SESSION_CACHE.containsKey(cacheMapKey)) {
                session= SESSION_CACHE.get(cacheMapKey);
                //当接收者有会话连接时才做事
                log.info("从activeMq接收所有未被消费的消息");
                //没有接收者时,只有/send生效
                String json;
                if(isQueue(identityName)) {
                    if (supportTopic()) {
                        changeJmsPattern();
                    }
                    json = this.receiveOneMsgFromQueue(identityName);
                }else {
                    if (!supportTopic()) {
                        changeJmsPattern();
                    }
                    //无法从此处获取消息
                    json = this.receiveOneMsgFromTopic();
                }
                if(json!=null){
                    session.getAsyncRemote().sendText(json);
                    msgNum++;
                    if(msgNum>=(sessionNum-1)) {
                        SimulateListener.messageStr = null;
                        msgNum=0;
                    }
                    log.info("发送消息至客户端:"+json);
                }
            }
        }
    }


    /**
     * 处理客服分配
     */
    private void staffAllocate(){

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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        if(!msgQueue.isEmpty()){
            msgQueue.forEach(s -> stringBuilder.append(s).append(","));
        }
        if(stringBuilder.charAt(stringBuilder.length()-1)==',') {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    /**
     * 执行msgListenerContainer的初始化
     */
    private void initListenerContainer(String topicName){
        this.msgListenerContainer.setDestination(new ActiveMQTopic(topicName));
    }

    /**
     * 从activeMQ队列中接收一条消息
     */
    private String receiveOneMsgFromQueue(String queueName){
        Objects.requireNonNull(jmsMessagingTemplate.getJmsTemplate()).setReceiveTimeout(5000);
        return jmsMessagingTemplate.receiveAndConvert(new ActiveMQQueue(queueName), String.class);
    }

    /**
     * 从activeMQ主题中接收一条消息
     */
    private String receiveOneMsgFromTopic(){
        String msg=SimulateListener.messageStr;
        return msg;
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
     * 判断identityName是队列名还是主体名称
     * @param identityName 标识名称
     * @return queue:true topic:false
     */
    private boolean isQueue(String identityName){
        return identityName.contains("queue");
    }

    /**
     * 判断jms模式
     * @return true or false
     */
    private boolean supportTopic(){
        JmsTemplate jmsTemplate = jmsMessagingTemplate.getJmsTemplate();
        return Objects.requireNonNull(jmsTemplate).isPubSubNoLocal();
    }

    /**
     * 切换jms模式
     */
    private void changeJmsPattern(){
        JmsTemplate jmsTemplate = jmsMessagingTemplate.getJmsTemplate();
        Objects.requireNonNull(jmsTemplate).setPubSubNoLocal(true);
    }

    @AllArgsConstructor
    @Data
    @Accessors
    @NoArgsConstructor
    static class  Msg{
        private String username;
        private String content;
        private Integer order;
    }
}
