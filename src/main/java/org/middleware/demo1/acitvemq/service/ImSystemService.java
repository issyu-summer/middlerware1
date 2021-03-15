package org.middleware.demo1.acitvemq.service;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.middleware.demo1.acitvemq.config.content.Type;
import org.middleware.demo1.acitvemq.entity.vo.FileVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.jms.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author summer
 * @date 2021/3/14 19:15
 */
@Service
public class ImSystemService {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    public boolean sendToSomebody(String msg, Long senderId, Long receiverId, Integer type, String fileName) throws JMSException {
        if(senderId == null || receiverId == null || msg == null){
            return false;
        }

        try {
            //1给2发,3给2发,队列名是否可以相同。
            jmsMessagingTemplate.convertAndSend(new ActiveMQQueue(String.valueOf(receiverId)), msg);

            writeLog(senderId, receiverId, null, type, msg);
        } catch (Exception e) {
            //发消息到activeMq一定会成功,怎么能知道接收者是否消费了activeMq上的消息？
            writeLog(senderId, receiverId, null, type, "FAILTOSEND");
            return false;
        }

        return true;
    }

    public boolean sendToGroup(String msg, Long senderId, Long groupId, Integer type, String fileName){

        if(senderId == null || groupId == null || msg == null){
            return false;
        }

        try {
            jmsMessagingTemplate.convertAndSend(new ActiveMQTopic(String.valueOf(groupId)), msg);

            writeLog(senderId, null, groupId, type, msg);
        } catch (Exception e) {
            writeLog(senderId, null, groupId, type, "FAILETOSEND");
            return false;
        }

        return true;
    }

    public boolean sendFile(Long senderId, Long receiverId, String fileName, byte[] file) {

        try {
            FileVo vo = new FileVo(fileName, file);
            jmsMessagingTemplate.convertAndSend(new ActiveMQQueue(String.valueOf(receiverId)), vo);

            String log = "SEND_FILE:"+fileName;
            writeLog(senderId, receiverId, null, 1, log);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean uploadFile(MultipartFile file) {

        try {
            BufferedOutputStream bos = null;
            bos = new BufferedOutputStream(new FileOutputStream("src/main/resources/static/" + file.getOriginalFilename()));
            bos.write(file.getBytes());
            bos.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void writeLog(Long senderId, Long receiverId, Long groupId, Integer type, String msg){

    }

}
