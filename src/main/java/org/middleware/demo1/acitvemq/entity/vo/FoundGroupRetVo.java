package org.middleware.demo1.acitvemq.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author qqpet24
 * @date 2021/5/14 20:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class FoundGroupRetVo {
    private Long userId;

    private Long shopUserId;

    private Long adminId;

    private Long groupId;

    private String groupName;
}
