package org.middleware.demo1.acitvemq.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgVo implements Serializable {
    private String content;

    private Long senderId;

}
