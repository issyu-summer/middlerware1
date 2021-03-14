package org.middleware.demo1.acitvemq.config.content;

import java.util.Arrays;
import java.util.List;

/**
 * 写死的资源
 * @author summer
 * @date 2021/3/14 19:24
 */

public class Content {


    /* ***********************************以下将模拟6个用户*********************************** */
    /**
     * 模拟用户1
     */
    public static User user1
            =new User().setId(1L).setName("jack");
    /**
     * 模拟用户2
     */
    public static User user2
            =new User().setId(2L).setName("peter");

    /**
     * 模拟用户3
     */
    public static User user3
            =new User().setId(3L).setName("park");

    /**
     * 模拟用户4
     */
    public static User user4
            =new User().setId(4L).setName("tony");

    /**
     * 模拟用户5
     */
    public static User user5
            =new User().setId(5L).setName("stark");

    /**
     * 模拟用户6
     */
    public static User user6
            =new User().setId(6L).setName("bruce");

    /* ***********************************以上将模拟6个用户*********************************** */


    /* ***********************************以下将模拟6个用户列表,三个用于群,三个用于私聊的朋友列表*********************************** */


    /**
     * 模拟群用户列表1,group.List<User> list
     */
    public static List<User> list1 =
            Arrays.asList(user1,user2,user3);

    /**
     * 模拟群用户列表2,group.List<User> list
     */
    public static List<User> list2 =
            Arrays.asList(user1,user4,user5);

    /**
     * 模拟群用户列表3,group.List<User> list
     */
    public static List<User> list3 =
            Arrays.asList(user2,user3,user6);

    /**
     * 模拟朋友用户列表1,  user1的朋友
     */
    public static List<User> friendList1 =
            Arrays.asList(user2,user3);

    /**
     * 模拟朋友用户列表2  user2的朋友
     */
    public static List<User> friendList2 =
            Arrays.asList(user1,user4,user5);

    /**
     * 模拟朋友用户列表3  user3的朋友
     */
    public static List<User> friendList3 =
            Arrays.asList(user2,user6);

    /* ***********************************以上将模拟3个用户列表*********************************** */


    /* ***********************************以下将模拟n个消息、请在使用的时候自行添加*********************************** */
    /**
     * 模拟消息1,用户1发送给到群1（msg:你吃饭了吗？）****中文测试****
     */
    public static Msg msg1
            = new Msg()
            .setId(1L).setContent("你们吃饭了吗？").setOrder(1)
            .setType(Type.TEXT).setSenderId(1L);

    /**
     * 模拟消息2,用户1发送给群1（msg:can you speak English ？）****英文测试****
     */
    public static Msg msg2
            = new Msg()
            .setId(1L).setContent("can you speak English ？").setOrder(1)
            .setType(Type.TEXT).setSenderId(1L);

    /**
     * 模拟消息3,用户1发送给用户2（msg:你好）****中文测试****
     */
    public static Msg msg3
            = new Msg()
            .setId(3L).setContent("你好").setOrder(1)
            .setType(Type.TEXT).setSenderId(1L).setReceiverId(2L);

    /**
     * 模拟消息4,用户1发送给用户2（msg:hello!）****英文测试****
     */
    public static Msg msg4
            = new Msg()
            .setId(4L).setContent("hello!").setOrder(2)
            .setType(Type.TEXT).setSenderId(1L).setReceiverId(2L);
    /* ***********************************以上将模拟n个消息、请在使用的时候自行添加*********************************** */


    /* ***********************************以下将模拟n个群消息记录、请在使用的时候自行添加*********************************** */
    /**
     * 群1的模拟消息记录
     */
    public static List<Msg> groupMsg1
            =Arrays.asList(msg1,msg2);

    /**
     * 群2的模拟消息记录,请自行添加
     */
    public static List<Msg> groupMsg2;
    /* ***********************************以上将模拟n个群消息记录、请在使用的时候自行添加*********************************** */


    /* ***********************************以下将模拟2个群*********************************** */
    /**
     * 模拟群1
     */
    public static Group group1
            =new Group()
            .setId(1L).setName("group1")
            .setUserList(list1)
            .setGroupMsg(groupMsg1);
    /**
     * 模拟群2
     */
    public static Group group2
            =new Group()
            .setId(2L).setName("group2")
            .setUserList(list2)
            .setGroupMsg(groupMsg2);
    /* ***********************************以上将模拟2个群*********************************** */







}
