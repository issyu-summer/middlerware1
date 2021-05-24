package org.middleware.demo1.acitvemq.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.middleware.demo1.acitvemq.config.Response;
import org.middleware.demo1.acitvemq.config.content.Msg;
import org.middleware.demo1.acitvemq.config.content.Type;
import org.middleware.demo1.acitvemq.entity.po.*;
import org.middleware.demo1.acitvemq.entity.po.Record;
import org.middleware.demo1.acitvemq.entity.vo.*;
import org.middleware.demo1.acitvemq.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.jms.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author summer
 * @date 2021/3/14 19:15
 */
@Service
public class ImSystemService {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserShopService userShopService;

    @Autowired
    private UserShopMapper userShopMapper;

    @Autowired
    private RecordService recordService;

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private  UserGroupService userGroupService;

    @Autowired
    private UserGroupMapper userGroupMapper;

    private HashMap<String,ActiveMQQueue> queueMap;
    private HashMap<String,ActiveMQTopic> topicMap;

    public boolean sendToSomebody(String msg, Long senderId, Long receiverId, Integer contentType){
        try {
            //1给2发,3给2发,队列名是否可以相同。
            //同时只进行一个通信；初步在消息中加入senderId
            String json=objectMapper.writeValueAsString( new MsgVo(msg,senderId));
            jmsMessagingTemplate.convertAndSend(new ActiveMQQueue("queue01"),json);
            writeLog(senderId,receiverId,0,contentType,msg);//魔法值0代表私聊
        } catch (Exception e) {
            e.printStackTrace();
            //发消息到activeMq一定会成功,怎么能知道接收者是否消费了activeMq上的消息？
            //不知道
            writeLog(senderId,receiverId,null,contentType,"FAILTOSENDSOMEBODY:");//魔法值null代表发送失败
            return false;
        }

        return true;
    }

    public boolean sendToGroup(String msg, Long senderId, Long receiverId, Integer contentType){
        try {
            String json=objectMapper.writeValueAsString( new MsgVo(msg,senderId));

            jmsMessagingTemplate.convertAndSend(new ActiveMQTopic("topic01"), json);
            writeLog(senderId,receiverId,1,contentType,msg);//魔法值1代表群聊
        } catch (Exception e) {
            writeLog(senderId,receiverId,null,contentType,"FAILTOSENDTOGROUP:");//魔法值null代表发送失败
            return false;
        }
        return true;
    }

    public void writeLog(Long senderId, Long receiverId, Integer type, Integer contentType,String msg){
        recordService.save(new Record().setSenderId(senderId).setReceiverId(receiverId).setType(type).setContentType(contentType)
                .setContent(msg).setDateTime(LocalDateTime.of(LocalDate.now(), LocalTime.now())));
    }

    public boolean sendFile(Long senderId, Long receiverId, String fileName, byte[] file) {

        try {

            FileVo vo = new FileVo(fileName, file, senderId);
            jmsMessagingTemplate.convertAndSend(new ActiveMQQueue("queue01"), vo);

            String log = "SEND_FILE:"+fileName;
            writeLog(senderId, receiverId, null, 1, log);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean uploadFile(MultipartFile file) {

        try {
            File folder = new File("src/main/resources/static/");
            if(!folder.exists()){
                folder.mkdir();
            }

            BufferedOutputStream bos = null;
            String path="src/main/resources/static/" + file.getOriginalFilename();
            bos = new BufferedOutputStream(new FileOutputStream(path));
            bos.write(file.getBytes());
            bos.close();

            String uri="http://localhost:9000/"+file.getOriginalFilename();
            String fileName=file.getOriginalFilename();

            fileService.save(
                    new org.middleware.demo1.acitvemq.entity.po.File()
                    .setPath(path).setUri(uri).setFileName(fileName));
            QueryWrapper<File> queryWrapper=new QueryWrapper<>();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public RecordListRetVo getRecord(Integer nums, Integer orders, Integer type,Integer contentType,Long senderId,Long receiverId){
        QueryWrapper<Record> queryWrapper= new QueryWrapper<>();
        queryWrapper.eq("sender_id",senderId);
        queryWrapper.eq("receiver_id",receiverId);
        queryWrapper.eq("type",type);
        if(contentType!=null){
            queryWrapper.eq("content_type",contentType);
        }
        if(nums != null && orders == null){
            queryWrapper.last("limit "+nums.toString());
        }else if(nums == null && orders != null){
            queryWrapper.last("limit "+orders.toString()+","+"-1");
        }else if(nums !=null && orders != null){
            queryWrapper.last("limit "+orders.toString()+","+nums.toString());
        }

        List<Record> records = recordMapper.selectList(queryWrapper);
        RecordListRetVo recordListRetVo = new RecordListRetVo();
        recordListRetVo.setMsgs(new LinkedList<>()).setOrders(orders).setNums(nums).setReceiverId(receiverId).setSenderId(senderId)
        .setContentType(contentType).setType(type);

        Integer tmpOrder = 0;
        for(Record record : records){
            recordListRetVo.getMsgs().add(new Msg().setOrder(tmpOrder++).setSenderId(record.getSenderId()).setReceiverId(record.getReceiverId())
            .setContent(record.getContent()).setType(record.getType()).setContentType(record.getContentType()).setDateTime(record.getDateTime().toString()).setId(record.getId()));
        }
        return recordListRetVo;
    }

    public String getFile(String fileName){
        QueryWrapper<org.middleware.demo1.acitvemq.entity.po.File> queryWrapper= new QueryWrapper<>();
        queryWrapper.eq("file_name",fileName);
        List<org.middleware.demo1.acitvemq.entity.po.File> files = fileMapper.selectList(queryWrapper);
        for(org.middleware.demo1.acitvemq.entity.po.File file : files){
            if(file.getFileName()!=null){
                return "/"+file.getFileName();
            }
        }
        return null;
    }

    private ActiveMQQueue getQueue(String queueName) {
        if(queueMap == null){
            queueMap = new HashMap<>();
        }

        ActiveMQQueue queue;
        if(queueMap.get(queueName) == null){
            queue = new ActiveMQQueue(queueName);
            queueMap.put(queueName, queue);
        } else {
            queue = queueMap.get(queueName);
        }

        return queue;
    }

    private ActiveMQTopic getTopic(String topicName) {
        if(topicMap == null) {
            topicMap = new HashMap<>();
        }

        ActiveMQTopic topic;
        if(topicMap.get(topicName) == null){
            topic = new ActiveMQTopic(String.valueOf(topicName));
            topicMap.put(topicName, topic);
        } else {
            topic = topicMap.get(topicName);
        }

        return topic;
    }

    public boolean addShop(Long userId,Long shopUserId){
        User user = userService.getById(userId);
        User shop = userService.getById(shopUserId);
        if(user==null || shop==null || user.getType()!=3 || shop.getType()!=4 ){
            return false;
        }
        userShopService.save(new UserShop().setShopUserId(shopUserId).setUserId(userId));
        return true;
    }

    public ShopListRetVo getAllShop(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type",4);
        List<User> users = userService.list(queryWrapper);
        ShopListRetVo shopListRetVo = new ShopListRetVo();
        shopListRetVo.setShopUserIdList(users);
        return shopListRetVo;
    }

    public Object getUserShop(Long userId){
        QueryWrapper<UserShop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<UserShop> userShops = userShopMapper.selectList(queryWrapper);
        List<Integer> collect = userShops.stream().mapToInt(e -> Math.toIntExact(e.getShopUserId())).boxed().collect(Collectors.toList());
        List<User> list = userService.list(new QueryWrapper<User>().in("id", collect));
        return list;
    }


    public UserListRetVo getShopUser(Long shopId){
        QueryWrapper<UserShop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shop_id",shopId);
        List<UserShop> userShops = userShopMapper.selectList(queryWrapper);
        UserListRetVo userListRetVo = new UserListRetVo();
        userListRetVo.setUserList(new LinkedList<>());
        for(UserShop userShop : userShops){
            userListRetVo.getUserList().add(userShop.getUserId());
        }
        return userListRetVo;
    }

    @Transactional
    public Object foundGroup(Long userId,Long shopId,String groupName){
        User user = userService.getById(userId);
        User shop = userService.getById(shopId);
        if(user == null || shop == null || user.getType()!=3 || shop.getType()!=4){
            return new Response<>().setCode(400).setMsg("未找到用户或者user对应类型不为普通用户,或者shopUser类型不为商家");
        }
        List<User> adminList=userService.list(new QueryWrapper<User>().eq("type",2).eq("online",1));
        if(adminList.isEmpty()){
            return new Response<>().setCode(503).setMsg("已经没有可分配的客服");
        }
        User admin=adminList.get(0);
        admin.setType(1);
        userService.updateById(admin);

        groupService.save(new Group().setGroupName(groupName));
        Group group = groupService.getOne(new QueryWrapper<Group>().eq("group_name",groupName));
        userGroupService.save(new UserGroup().setGroupId(group.getId()).setUserId(userId));
        userGroupService.save(new UserGroup().setGroupId(group.getId()).setUserId(shopId));
        userGroupService.save(new UserGroup().setGroupId(group.getId()).setUserId(admin.getId()));

        return new Response<>().setCode(0).setMsg("OK").setData(new FoundGroupRetVo().setAdminId(admin.getId())
        .setGroupId(group.getId()).setGroupName(groupName).setShopUserId(shopId).setUserId(userId));
    }

    public Object searchUserGroup(Long userId){
        if(userService.getById(userId) == null){
            return new Response<>().setCode(400).setMsg("用户不存在");
        }
        List<UserGroup> userGroupList = userGroupMapper.selectList(new QueryWrapper<UserGroup>().eq("user_id",userId));
        UserGroupRetVo userGroupRetVo = new UserGroupRetVo().setUserId(userId).setGroupList(new LinkedList<>());
        for(UserGroup userGroup : userGroupList){
            String groupName = groupService.getById(userGroup.getGroupId()).getGroupName();
            GroupVo groupVo = new GroupVo();
            groupVo.setGroupId(userGroup.getGroupId());
            groupVo.setGroupName(groupName);
            userGroupRetVo.getGroupList().add(groupVo);
        }
        return new Response<>().setCode(0).setMsg("OK").setData(userGroupRetVo);
    }

    public Object getGroupMember(Long groupId){
        if(groupService.getById(groupId) == null){
            return new Response<>().setCode(400).setMsg("群不存在");
        }
        List<UserGroup> userGroupList = userGroupMapper.selectList(new QueryWrapper<UserGroup>().eq("group_id",groupId));
        GroupUserRetVo groupUserRetVo = new GroupUserRetVo().setGroupId(groupId).setUsersInfo(new LinkedList<>());
        for(UserGroup userGroup : userGroupList){
            groupUserRetVo.getUsersInfo().add(new UserBasicInfoVo().setUserId(userGroup.getUserId())
            .setUserName(userService.getById(userGroup.getId()).getUsername()));
        }
        return new Response<>().setCode(0).setMsg("OK").setData(groupUserRetVo);
    }
}
