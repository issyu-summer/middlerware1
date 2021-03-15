package org.middleware.demo1.acitvemq.service;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.middleware.demo1.acitvemq.config.content.Msg;
import org.middleware.demo1.acitvemq.config.content.Type;
import org.middleware.demo1.acitvemq.entity.vo.FileVo;
import org.middleware.demo1.acitvemq.entity.vo.RecordListRetVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.jms.*;
import java.io.BufferedOutputStream;
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

    public boolean sendToSomebody(String msg, Long senderId, Long receiverId, Integer type, String fileName) throws JMSException {
        if(senderId == null || receiverId == null || msg == null){
            return false;
        }

        try {
            jmsMessagingTemplate.convertAndSend(new ActiveMQQueue(String.valueOf(receiverId)), msg);

            writeLog(senderId, receiverId, null, type, msg);
        } catch (Exception e) {
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

    public boolean uplaodFile(MultipartFile file) {

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
        }else{
            Long id = (long) msgGroupRecordList.size();
            Integer orderId = msgGroupRecordsOrderMap.get(groupId);
            if(orderId == null){
                orderId = 0;
                msgGroupRecordsOrderMap.put(receiverId,orderId);
            }else{
                orderId += 1;
                msgGroupRecordsOrderMap.put(receiverId,orderId);
            }
            msgGroupRecordList.add(new Msg().setId(id).setType(Type.getEnum(type)).setContent(msg).setReceiverId(groupId).setSenderId(senderId).setOrder(orderId));
        }
    }

    public RecordListRetVo getRecord(Integer nums, Integer orders, Long receiverId, Long groupId){
        if(msgReceiverRecordList == null){
            msgReceiverRecordList = new LinkedList<>();
        }
        if(msgGroupRecordList == null){
            msgGroupRecordList = new LinkedList<>();
        }
        RecordListRetVo recordListRetVo = new RecordListRetVo();
        recordListRetVo.setMsgs(new LinkedList<>());

        if(receiverId != null){
            recordListRetVo.setReceiverId(receiverId).setNums(nums).setOrders(orders);

            int tmpOrder = 0;
            for(Msg tmpMsg : msgReceiverRecordList){
                if(tmpMsg.getReceiverId().equals(receiverId)){
                    if(tmpOrder >= orders && recordListRetVo.getMsgs().size() < nums ){
                        recordListRetVo.getMsgs().add(tmpMsg);
                    }
                    tmpOrder++;
                }
            }
            return recordListRetVo;
        }else{
            recordListRetVo.setGroupId(groupId).setNums(nums).setOrders(orders);

            int tmpOrder = 0;
            for(Msg tmpMsg : msgGroupRecordList){
                if(tmpMsg.getReceiverId().equals(groupId)){
                    if(tmpOrder >= orders && recordListRetVo.getMsgs().size() < nums ){
                        recordListRetVo.getMsgs().add(tmpMsg);
                    }
                    tmpOrder++;
                }
            }
            return recordListRetVo;
        }
    }
}
