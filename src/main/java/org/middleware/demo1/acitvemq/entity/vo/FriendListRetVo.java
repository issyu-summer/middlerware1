package org.middleware.demo1.acitvemq.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.middleware.demo1.acitvemq.config.content.User;

import java.util.List;

/**
 * @author summer
 * @date 2021/3/14 20:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class FriendListRetVo {

    /**
     * 标识这个人是谁
     */
    private Long userId;

    private String username;

    /**
     * 标识他的朋友列表
     */
    private List<User> friends;
}
