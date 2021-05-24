package org.middleware.demo1.acitvemq.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.activemq.command.ActiveMQQueue;
import org.middleware.demo1.acitvemq.config.Response;
import org.middleware.demo1.acitvemq.entity.po.Group;
import org.middleware.demo1.acitvemq.entity.po.User;
import org.middleware.demo1.acitvemq.entity.po.UserGroup;
import org.middleware.demo1.acitvemq.entity.vo.GroupRetVo;
import org.middleware.demo1.acitvemq.service.GroupService;
import org.middleware.demo1.acitvemq.service.ImSystemService;
import org.middleware.demo1.acitvemq.service.UserGroupService;
import org.middleware.demo1.acitvemq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.jms.Queue;
import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author summer
 * @date 2021/3/14 19:15
 */
@RestController
@RequestMapping
public class ImSystemController {

    @Autowired
    private ImSystemService service;

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    /**
     * 测试接口
     */
    @GetMapping("/test")
    public Object send5Msg(){
        Queue queue = new ActiveMQQueue("queue");
        jmsMessagingTemplate.convertAndSend(queue,"你在吗？");
        jmsMessagingTemplate.convertAndSend(queue,"周日下午有时间吗？");
        jmsMessagingTemplate.convertAndSend(queue,"我们一起去吃饭啊");
        jmsMessagingTemplate.convertAndSend(queue,"我听菜虚困说,你想叛变革命?");
        jmsMessagingTemplate.convertAndSend(queue,"原来是它不给你革命领导,那没事了");
        return new Response<>();
    }

    /**
     * @author qqpet24
     * 获取当前所有shop列表
     */
    @GetMapping("shop/all")
    public Object getAllShop(){
        try{
            return new Response<>().setCode(0).setMsg("OK").setData(service.getAllShop());
        }catch (Exception e){
            return new Response<>().setCode(500).setMsg(e.getMessage());
        }
    }

    /**
     * @author qqpet24
     * 增加用户对应的shop,类似于QQ加好友，加的是店家,userId和shopUserId联合唯一索引,userId和shopUserId必须存在,否则报400错,如果添加不唯一记录报500错
     */
    @GetMapping("user/addshop")
    public Object addShop(@RequestParam Long userId,
                          @RequestParam Long shopUserId){
        try{
            if(service.addShop(userId,shopUserId)){
                return new Response<>().setCode(0).setMsg("OK");
            }else {
                return new Response<>().setCode(400).setMsg("userId或者shopUserId中一个为空或者userId对应user不为普通用户类型(代号3)或者shopUserId对应user不为普通商家类型(代号4)");
            }
        }catch (Exception e){
            return new Response<>().setCode(500).setMsg(e.getMessage());
        }
    }

    /**
     * @author qqpet24
     * 查找用户对应shop,对于传入userId没有进行校验有效性
     */
    @GetMapping("user/getshop")
    public Object getShop(@RequestParam Long userId){
        try{
            return new Response<>().setCode(0).setMsg("OK").setData(service.getUserShop(userId));
        }catch (Exception e){
            return new Response<>().setCode(500).setMsg(e.getMessage());
        }
    }

    /**
     * @author qqpet24
     * 查找shop对应的User
     */
    @GetMapping("shop/getuser")
    public Object getUser(@RequestParam Long shopId){
        try{
            return new Response<>().setCode(0).setMsg("OK").setData(service.getShopUser(shopId));
        }catch (Exception e){
            return new Response<>().setCode(500).setMsg(e.getMessage());
        }
    }
    /**
     * @author qqpet24
     * 成立group,随机分配一个可分配的admin客服，如果没有，返回503，如果有建立群,并且拉user、shopUser、admin三个人进群
     * userId和shopId必须有效且用户类型正确，否则报400
     * admin客服被分配后,除非调用/user/restore接口,否则不能再次分配
     */
    @GetMapping("group/found")
    public Object foundGroup(@RequestParam Long userId,
                             @RequestParam Long shopId,
                             @RequestParam String groupName){
        try{
            return service.foundGroup(userId,shopId,groupName);
        }catch (Exception e){
            return new Response<>().setCode(500).setMsg(e.getMessage());
        }
    }

    /**
     * @author qqpet24
     * 查找用户对应的group,userId代表任意类型user,但是必须存在,否则报400
     */
    @GetMapping("group/search")
    public Object searchGroup(@RequestParam Long userId){
        try{
            return service.searchUserGroup(userId);
        }catch (Exception e){
            return new Response<>().setCode(500).setMsg(e.getMessage());
        }
    }

    @Autowired
    private UserService userService;
    /**
     * @author qqpet24
     * 查找group对应的members,groupId必须存在,否则报400
     */
    @GetMapping("group/members")
    public Object getGroupMember(@RequestParam Long groupId){
        try{
            List<UserGroup> group = userGroupService.list(new QueryWrapper<UserGroup>().eq("group_id",groupId));
            List<Integer> collect = group.stream().mapToInt(e -> Math.toIntExact(e.getUserId())).boxed().collect(Collectors.toList());
            return new Response<>().setCode(0).setMsg("OK").setData(userService.list(new QueryWrapper<User>().in("id", collect)));
        }catch (Exception e){
            return new Response<>().setCode(500).setMsg(e.getMessage());
        }
    }

    @GetMapping("/user")
    public Object getUser(@RequestParam Long userId){
        List<UserGroup> userGroup = userGroupService.list(new QueryWrapper<UserGroup>().eq("user_id", userId));
        if(userGroup==null){
            return new Response<>().setCode(500).setMsg("server internal error");
        }
        List<Integer> collect1 = userGroup.stream().mapToInt(e -> Math.toIntExact(e.getGroupId())).boxed().collect(Collectors.toList());
        List<UserGroup> groups= userGroupService.list(new QueryWrapper<UserGroup>().in("group_id", collect1));
        List<Long> collect = groups.stream().mapToLong(e -> Math.toIntExact(e.getGroupId())).boxed().collect(Collectors.toList());
        List<GroupRetVo> list = new LinkedList<>();
        for (Long id:collect) {
            GroupRetVo groupRetVo = new GroupRetVo().setGroupId(id).setUsers(userService.list(new QueryWrapper<User>().in("id",
                    userGroupService.list(new QueryWrapper<UserGroup>().eq("group_id", id))
                            .stream().mapToInt(e -> Math.toIntExact(e.getUserId())).boxed().collect(Collectors.toList()))));
            list.add(groupRetVo);
        }
        return new Response<>().setCode(0).setMsg("OK").setData(list);
    }
    @Autowired
    private GroupService groupService;
    @Autowired
    private UserGroupService userGroupService;
    @GetMapping("group")
    public Object group(@RequestParam Long userId,@RequestParam Long shopId){
        Group group = new Group().setGroupName("test-group");
        boolean save = groupService.save(group);
        Long groupId=group.getId();
        UserGroup user = new UserGroup().setUserId(userId).setGroupId(groupId);
        UserGroup shop = new UserGroup().setUserId(shopId).setGroupId(groupId);
        if(save){
            userGroupService.save(user);
            userGroupService.save(shop);
        }
        return new Response<>().setCode(0).setMsg("OK").setData(group);
    }


    /**
     * 发送聊天接口
     * @modify qqpet24
     * @param msg 消息，如果聊天内容类型是0代表普通聊天记录，如果是1代表文件记录
     * @param senderId 发送者ID,必须在user表中中存在
     * @param receiverId 被选择者ID(这个可能是群聊也可能是一对一聊,如果type为0代表userId如果type为1代表groupId)，但是receiverId没有校验是否真的存在
     * @param type 聊天类型,如果是0,就是一对一聊天,如果是1就是群聊
     * @param contentType 聊天内容类型,如果是0代表普通文字聊天记录，1代表文件
     * @return code0 400 500
     */
    @GetMapping("/send")
    public Object send(@RequestParam String msg,
                       @RequestParam Long senderId,
                       @RequestParam Long receiverId,
                       @RequestParam Integer type,
                       @RequestParam Integer contentType){
        try{
            if(type == 0 && (contentType == 0 || contentType == 1 )){
                return new Response<>().setCode(0).setMsg("OK").setData(service.sendToSomebody(msg, senderId, receiverId, contentType));
            }else if(type == 1 && (contentType == 0 || contentType == 1 )){
                return new Response<>().setCode(0).setMsg("OK").setData(service.sendToGroup(msg, senderId,receiverId, contentType));
            }else{
                return new Response<>().setCode(400).setMsg("type和contentType必须是0或者1");
            }
        }catch (Exception e){
            return new Response<>().setCode(500).setMsg(e.getMessage());
        }
    }

    /**
     * 查找聊天记录接口
     * @author qqpet24
     * @param nums 聊天记录条数限制,默认10条
     * @param orders 聊天记录顺序,如果orders为1,nums为3,意思是从聊天记录中的第二条数据开始最多返回3条聊天记录,默认从0开始
     * @param senderId 发送人ID，但是senderId没有校验是否真的存在
     * @param receiverId 被选择者ID(这个可能是群聊也可能是一对一聊,如果type为0代表userId如果type为1代表groupId)，但是receiverId没有校验是否真的存在
     * @param type 聊天类型,如果是0,就是一对一聊天,如果是1就是群聊
     * @param contentType 可选，筛选聊天内容类型,如果是0代表普通文字聊天记录，1代表文件,不写符合条件全部返回
     * @return Record
     */
    @GetMapping("/record")
    public Object record(@RequestParam(required = false,defaultValue = "10") Integer nums,
                         @RequestParam(required = false,defaultValue = "0") Integer orders,
                         @RequestParam Long senderId,
                         @RequestParam Long receiverId,
                         @RequestParam Integer type,
                         @RequestParam(required = false)  Integer contentType){
        try{
            if((type!=0 && type!=1) || (contentType!=null && contentType != 0 && contentType != 1)){
                return new Response<>().setCode(400).setData("请求的contentType和type必须为0或者1中的一个整数");
            }
            return new Response<>().setCode(0).setData("成功").setData(service.getRecord(nums,orders,type,contentType,senderId,receiverId));
        }catch (Exception e){
            return new Response<>().setCode(500).setData(e.getMessage());
        }
    }

    /**
     * 存储转发
     * File的内容
     * @param file
     * @return
     */
    @PostMapping("/fileSave")
    public Object fileSave(HttpServletRequest request,
                           @RequestParam(value = "file", required = true) MultipartFile file){
        boolean result = false;

        try {
            if(file == null) {
                return new Response<>().setCode(404).setMsg("文件不存在").setData(false);
            }

            result = service.uploadFile(file);
        } catch (Exception e) {
            return new Response<>().setCode(400).setMsg("内部错误");
        }

        //true of false
        return new Response<>().setCode(0).setData("成功").setData(result);
    }

    /**
     * 下载,输入对应文件名,获取相对文件路径,如输入a.txt获取到/a.txt。
     * @author qqpet24
     * @param fileName
     * @return
     */
    @GetMapping("/download")
    public Object download(@RequestParam String fileName){
        String result = service.getFile(fileName);
        if(result == null){
            return new Response<>().setCode(404).setData("查不到该文件");
        }else{
            return new Response<>().setCode(0).setData("成功").setData(result);
        }
    }

    //发送文件和存储转发的流程：A调用/fileSave--->/send----->B收到了消息------->/downLoad
}
