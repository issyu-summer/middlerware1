package org.middleware.demo1.acitvemq.service;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.middleware.demo1.acitvemq.config.content.Type;
import org.springframework.stereotype.Service;

import javax.jms.*;

/**
 * @author summer
 * @date 2021/3/14 19:15
 */
@Service
public class ImSystemService {

    public boolean sendToSomebody(String msg, Long senderId, Long receiverId, Integer type, String fileName) throws JMSException {
        if(senderId == null || receiverId == null || msg == null){
            return false;
        }

        try {
            if(fileName != null){
                //todo 发送文件
            }

            ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("tcp://101.37.20.199:61616");
            Connection connection = activeMQConnectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(receiverId.toString());
            MessageProducer producer = session.createProducer(destination);

            TextMessage message = session.createTextMessage(msg);
            producer.send(message);

            writeLog(senderId, receiverId, null, type, msg);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean sendToGroup(String msg, Long senderId, Long groupId, Integer type, String fileName){

        if(senderId == null || groupId == null || msg == null){
            return false;
        }

        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, "tcp://101.37.20.199:61616");
            TopicConnection connection = factory.createTopicConnection();
            TopicSession pubSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            TopicSession subSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = subSession.createTopic("GROUP_"+groupId);
            TopicPublisher publisher = pubSession.createPublisher(topic);
            connection.start();

            TextMessage message = pubSession.createTextMessage(msg);
            publisher.send(message);

            writeLog(senderId, null, groupId, type, msg);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean sendFile(Long senderId, Long receiverId, String fileName, byte[] file) {

        try {
            ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("tcp://101.37.20.199:61616");
            Connection connection = activeMQConnectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(receiverId.toString());
            MessageProducer producer = session.createProducer(destination);

            StreamMessage message = session.createStreamMessage();
            message.setStringProperty("fileName",fileName);
            message.writeBytes(file);
            producer.send(message);

            String log = "SEND_FILE:"+fileName;
            writeLog(senderId, receiverId, null, 1, log);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public void writeLog(Long senderId, Long receiverId, Long groupId, Integer type, String msg){

    }

}
