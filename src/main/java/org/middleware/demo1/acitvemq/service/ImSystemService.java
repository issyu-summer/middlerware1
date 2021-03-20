package org.middleware.demo1.acitvemq.service;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.middleware.demo1.acitvemq.config.content.Msg;
import org.middleware.demo1.acitvemq.config.content.Type;
import org.middleware.demo1.acitvemq.entity.vo.FileVo;
import org.middleware.demo1.acitvemq.entity.vo.MsgVo;
import org.middleware.demo1.acitvemq.entity.vo.RecordListRetVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.jms.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author summer
 * @date 2021/3/14 19:15
 */
@Service
public class ImSystemService {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    private List<Msg> msgReceiverRecordList;
    private HashMap<Long,Integer> msgReceiverRecordsOrderMap;
    private List<Msg> msgGroupRecordList;
    private HashMap<Long,Integer> msgGroupRecordsOrderMap;
    private HashMap<String,String> fileSaveMap;
    private HashMap<Long,ActiveMQQueue> queueMap;
    private HashMap<Long,ActiveMQTopic> topicMap;

    public boolean sendToSomebody(String msg, Long senderId, Long receiverId, Integer type, String fileName) throws JMSException {
        if(senderId == null || receiverId == null || msg == null){
            return false;
        }

        try {
            //1给2发,3给2发,队列名是否可以相同。
            //同时只进行一个通信；初步在消息中加入senderId
            jmsMessagingTemplate.convertAndSend(getQueue(receiverId), new MsgVo(msg,senderId));

            writeLog(senderId, receiverId, null, type, msg);
        } catch (Exception e) {
            e.printStackTrace();
            //发消息到activeMq一定会成功,怎么能知道接收者是否消费了activeMq上的消息？
            //不知道
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

            jmsMessagingTemplate.convertAndSend(getTopic(groupId), new MsgVo(msg,senderId));

            writeLog(senderId, null, groupId, type, msg);
        } catch (Exception e) {
            writeLog(senderId, null, groupId, type, "FAILETOSEND");
            return false;
        }

        return true;
    }

    public boolean sendFile(Long senderId, Long receiverId, String fileName, byte[] file) {

        try {

            FileVo vo = new FileVo(fileName, file, senderId);
            jmsMessagingTemplate.convertAndSend(getQueue(receiverId), vo);

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
            bos = new BufferedOutputStream(new FileOutputStream("src/main/resources/static/" + file.getOriginalFilename()));
            bos.write(file.getBytes());
            bos.close();

            if(fileSaveMap == null){
                fileSaveMap = new HashMap<>();
            }
            fileSaveMap.put(file.getOriginalFilename(),"http://localhost:9000/"+file.getOriginalFilename());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void writeLog(Long senderId, Long receiverId, Long groupId, Integer type, String msg){
        if(msgReceiverRecordList == null){
            msgReceiverRecordList = new LinkedList<>();
        }
        if(msgReceiverRecordsOrderMap == null){
            msgReceiverRecordsOrderMap = new HashMap<>();
        }
        if(msgGroupRecordList == null){
            msgGroupRecordList = new LinkedList<>();
        }
        if(msgGroupRecordsOrderMap == null){
            msgGroupRecordsOrderMap = new HashMap<>();
        }

        if(receiverId !=null){
            Long id = (long) msgReceiverRecordList.size();
            Integer orderId = msgReceiverRecordsOrderMap.get(receiverId);
            if(orderId == null){
                orderId = 0;
                msgReceiverRecordsOrderMap.put(receiverId,orderId);
            }else{
                orderId += 1;
                msgReceiverRecordsOrderMap.put(receiverId,orderId);
            }
            msgReceiverRecordList.add(new Msg().setId(id).setType(Type.getEnum(type)).setContent(msg).setReceiverId(receiverId).setSenderId(senderId).setOrder(orderId));
        } else{
            Long id = (long) msgGroupRecordList.size();
            Integer orderId = msgGroupRecordsOrderMap.get(groupId);
            if(orderId == null){
                orderId = 0;
                msgGroupRecordsOrderMap.put(groupId,orderId);
            }else{
                orderId += 1;
                msgGroupRecordsOrderMap.put(groupId,orderId);
            }
            msgGroupRecordList.add(new Msg().setId(id).setType(Type.getEnum(type)).setContent(msg).setSenderId(senderId).setGruopId(groupId).setOrder(orderId));
        }
    }

    public RecordListRetVo getRecord(Integer nums, Integer orders, Long senderId,Long receiverId, Long groupId){
        if(msgReceiverRecordList == null){
            msgReceiverRecordList = new LinkedList<>();
        }
        if(msgGroupRecordList == null){
            msgGroupRecordList = new LinkedList<>();
        }
        RecordListRetVo recordListRetVo = new RecordListRetVo();
        recordListRetVo.setMsgs(new LinkedList<>());

        if(receiverId != null){
            recordListRetVo.setReceiverId(receiverId).setSenderId(senderId).setNums(nums).setOrders(orders);

            int tmpOrder = 0;
            for(Msg tmpMsg : msgReceiverRecordList){
                if(tmpMsg.getSenderId().equals(senderId)){
                    if(tmpMsg.getReceiverId().equals(receiverId)){
                        if(tmpOrder >= orders && recordListRetVo.getMsgs().size() < nums ){
                            recordListRetVo.getMsgs().add(tmpMsg);
                        }
                        tmpOrder++;
                    }
                }
            }
            return recordListRetVo;
        }else{
            recordListRetVo.setGroupId(groupId).setSenderId(senderId).setNums(nums).setOrders(orders);

            int tmpOrder = 0;
            for(Msg tmpMsg : msgGroupRecordList){
                if(tmpMsg.getGruopId() != null && tmpMsg.getGruopId().equals(groupId)) {
                    if(tmpOrder >= orders && recordListRetVo.getMsgs().size() < nums ){
                        recordListRetVo.getMsgs().add(tmpMsg);
                    }
                    tmpOrder++;
                }
//                if(tmpMsg.getSenderId().equals(senderId)){
//                    if(tmpMsg.getReceiverId().equals(groupId)){
//                        if(tmpOrder >= orders && recordListRetVo.getMsgs().size() < nums ){
//                            recordListRetVo.getMsgs().add(tmpMsg);
//                        }
//                        tmpOrder++;
//                    }
//                }
            }
            return recordListRetVo;
        }
    }

    public String getFile(String fileName){
        if(fileSaveMap == null){
            return null;
        }else{
            return fileSaveMap.get(fileName);
        }
    }

    private ActiveMQQueue getQueue(Long receiverId) {
        if(queueMap == null){
            queueMap = new HashMap<>();
        }

        ActiveMQQueue queue;
        if(queueMap.get(receiverId) == null){
            queue = new ActiveMQQueue(String.valueOf(receiverId));
            queueMap.put(receiverId, queue);
        } else {
            queue = queueMap.get(receiverId);
        }

        return queue;
    }

    private ActiveMQTopic getTopic(Long groupId) {
        if(topicMap == null) {
            topicMap = new HashMap<>();
        }

        ActiveMQTopic topic;
        if(topicMap.get(groupId) == null){
            topic = new ActiveMQTopic(String.valueOf(groupId));
            topicMap.put(groupId, topic);
        } else {
            topic = topicMap.get(groupId);
        }

        return topic;
    }
}
