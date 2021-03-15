package org.middleware.demo1.acitvemq.controller;

import org.middleware.demo1.acitvemq.config.Response;
import org.middleware.demo1.acitvemq.config.content.Content;
import org.middleware.demo1.acitvemq.config.content.Msg;
import org.middleware.demo1.acitvemq.entity.vo.FriendListRetVo;
import org.middleware.demo1.acitvemq.service.ImSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.jms.JMSException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.middleware.demo1.acitvemq.config.content.Content.*;

/**
 * @author summer
 * @date 2021/3/14 19:15
 */
@RestController
@RequestMapping
public class ImSystemController {

    @Autowired
    private ImSystemService service;

    /**
     * webSocket
     *
     * @return
     */
    @GetMapping("/receive")
    public Object receive(@RequestParam String msg,
                          @RequestParam String sender,
                          @RequestParam(required = false) String receiver,
                          @RequestParam Integer type,
                          @RequestParam(required = false) String fileName,
                          @RequestParam(required = false) String groupName){
        //最近十条
        return null;
    }

    /**
     * @param userId  发起聊天的人
     * @return userList
     */
    @GetMapping("/userList")
    public Object getUserList(@RequestParam("userId")  Long userId){
        FriendListRetVo friendList=new FriendListRetVo();
        if(userId==1L){
            friendList.setUserId(user1.getId()).setFriends(friendList1).setUsername(user1.getName());
        }
        if(userId==2L){
            friendList.setUserId(user2.getId()).setFriends(friendList2).setUsername(user2.getName());
        }
        if(userId==3L){
            friendList.setUserId(user3.getId()).setFriends(friendList3).setUsername(user3.getName());
        }
        return new Response<>().setCode(0).setMsg("Ok").setData(friendList);
    }



    /**
     *
     * @param senderId 发送者ID
     * @param userId 被选择者ID
     * @return [聊天记录]
     */
    @GetMapping("/user")
    public Object userInfo(@RequestParam Long senderId, @RequestParam Long userId){
        List<Msg> chatLog = new ArrayList<>();

        if(senderId.equals(1L) && userId.equals(2L)){
            chatLog.add(Content.msg3);
            chatLog.add(Content.msg4);
        }

        return new Response<>().setCode(0).setMsg("OK").setData(chatLog);
    }

    @GetMapping("/send")
    public Object send(@RequestParam String msg,
                       @RequestParam Long senderId,
                       @RequestParam(required = false) Long receiverId,
                       @RequestParam Integer type,
                       @RequestParam(required = false) String fileName,
                       @RequestParam(required = false) Long groupId) throws JMSException {
        boolean result;

        if(receiverId != null) {
            result = service.sendToSomebody(msg, senderId, receiverId, type, fileName);
        }

        else if(groupId != null) {
            result = service.sendToGroup(msg, senderId, groupId, type, fileName);
        }
        else {
            return new Response<>().setCode(400).setMsg("接收方为空");
        }

        //true or false
        return new Response<>().setCode(0).setMsg("OK").setData(result);
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

        //todo （进度条？）

        //true of false
        return new Response<>().setCode(0).setData("成功").setData(result);
    }

    /**
     * 下载,输入对应文件名,获取相对文件路径,如输入a.txt获取到/a.txt。必须先调用fileSave接口成功,文件才会被记录
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

    /**
     * @author qqpet24
     * watchDog不是一个API
     */
//    @PostMapping("/watchdogs")
//    public Object watchDog(){
//        return null;
//    }


    /**
     * @author qqpet24
     * @param nums 聊天记录条数限制,默认10条
     * @param orders 聊天记录顺序,如果orders为1,nums为3,意思是从聊天记录中的第二条数据开始最多返回3条聊天记录,默认从0开始
     * @param receiverId 接收人ID,意味着是一对一聊天
     * @param groupId 群ID,意味着是群聊,groupId和receiverId有且仅有一个
     * @return
     */

    @GetMapping("/record")
    public Object record(@RequestParam(required = false,defaultValue = "10") Integer nums,
                         @RequestParam(required = false,defaultValue = "0") Integer orders,
                         @RequestParam(required = false) Long receiverId,
                         @RequestParam(required = false) Long groupId){
        try{
            if((receiverId == null && groupId == null ) || (receiverId != null && groupId != null)){
                return new Response<>().setCode(400).setData("请求的receiverId和groupId必须有且仅有一个");
            }
            return new Response<>().setCode(0).setData("成功").setData(service.getRecord(nums,orders,receiverId,groupId));
        }catch (Exception e){
            return new Response<>().setCode(500).setData(e.getMessage());
        }
    }


}
