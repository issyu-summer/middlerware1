package org.middleware.demo1.acitvemq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsMessagingTemplate;

import javax.jms.Queue;

/**
 * @author summer
 * @date 2021/3/14 19:13
 */
@Configuration
@EnableJms
@Slf4j
public class Config {

    /**
     * json工具
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 该消息队列的模板,默认时同步发送的方式
     */
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

}
