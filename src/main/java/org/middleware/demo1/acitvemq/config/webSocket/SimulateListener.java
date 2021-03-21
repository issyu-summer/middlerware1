package org.middleware.demo1.acitvemq.config.webSocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * 模拟监听器,但group初始化时立刻启动
 * @author summer
 * @date 2021/3/21 11:59
 */
@Slf4j
@Component
public class SimulateListener implements MessageListener {

    public static String messageStr;

    @Override
    public void onMessage(Message message) {
        TextMessage textMsg = (TextMessage) message;
        try {
            String msg=textMsg.getText();
            log.info(msg);
            messageStr=msg;
            log.info("监听到消息:"+msg+",已暂存");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
