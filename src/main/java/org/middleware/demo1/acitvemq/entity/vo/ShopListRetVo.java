package org.middleware.demo1.acitvemq.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.middleware.demo1.acitvemq.config.content.Msg;

import java.util.List;

/**
 * @author qqpet24
 * @date 2021/3/15 20:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ShopListRetVo {
    private List<Long> shopUserIdList;
}
