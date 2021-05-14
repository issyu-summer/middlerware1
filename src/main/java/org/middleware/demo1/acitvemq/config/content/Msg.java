package org.middleware.demo1.acitvemq.config.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author summer
 * @date 2021/3/14 19:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Msg {

    private Long id;
    /**
     * 所有消息中的消息序号
     */
    private Integer order;

    /**
     * 消息记录的内容
     */
    private String content;

    /**
     * 0-私聊 1群聊
     */
    private Integer type;

    /**
     * 0-text  1-file
     */
    private Integer contentType;

    /**
     * 发送者
     */
    private Long senderId;

    /**
     * 接收者
     */
    private Long receiverId;

    /**
     * 消息发送的时间
     */
    private String dateTime;

}
