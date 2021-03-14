package org.middleware.demo1.acitvemq.controller;

import org.middleware.demo1.acitvemq.config.Response;
import org.middleware.demo1.acitvemq.entity.vo.FriendListRetVo;
import org.middleware.demo1.acitvemq.service.ImSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;

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
     * @param
     * @return
     */
    @GetMapping("/user")
    public Object userInfo(@RequestParam String userName){

        return null;
    }

    @GetMapping("/send")
    public Object send(@RequestParam String msg,
                       @RequestParam String sender,
                       @RequestParam(required = false) String receiver,
                       @RequestParam Integer type,
                       @RequestParam(required = false) String fileName,
                       @RequestParam(required = false) String groupName){
        //true or false
        return null;
    }



    /**
     * 存储转发
     * File的内容
     * @param file
     * @return
     */
    @PostMapping("/fileSave")
    public Object fileSave(@RequestParam File file){
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
