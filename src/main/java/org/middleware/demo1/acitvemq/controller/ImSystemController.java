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
import java.io.File;
import java.io.IOException;
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
        return new Response<>().setCode(0).setMsg("success").setData(friendList);
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
        boolean result = false;

        if(receiverId != null) {
            result = service.sendToSomebody(msg, senderId, receiverId, type, fileName);
        }

        else if(groupId != null) {
            result = service.sendToGroup(msg, senderId, groupId, type, fileName);
        }

        //true or false
        return result;
    }



    /**
     * 存储转发
     * File的内容
     * @param file
     * @return
     */
    @PostMapping("/fileSave")
    public Object fileSave(HttpServletRequest request,
                           @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        //todo 上传文件至文件服务器（进度条？）

        //true of false
        return null;
    }

    /**
     * 下载
     * @param fileName
     * @return
     */
    @PostMapping("/downLoad")
    public Object downLoad(@RequestParam String fileName){
        //String fileName+url
        return null;
    }

    //发送文件和存储转发的流程：A调用/fileSave--->/send----->B收到了消息------->/downLoad

    @PostMapping("/watchdogs")
    public Object watchDog(){
        return null;
    }


    @GetMapping("/record")
    public Object record(@RequestParam(required = false) Integer nums,
                         @RequestParam String sender,
                         @RequestParam(required = false) String receiver,
                         @RequestParam(required = false) String groupName){
        //名字,内容,order
        return null;
    }


}
