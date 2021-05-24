package org.middleware.demo1.acitvemq.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author qqpet24
 * @date 2021/5/24 20:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserListRetVo {
    private List<Long> userList;
}