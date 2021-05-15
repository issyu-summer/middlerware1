package org.middleware.demo1.acitvemq.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.middleware.demo1.acitvemq.entity.po.Group;
import org.middleware.demo1.acitvemq.entity.po.Record;
import org.middleware.demo1.acitvemq.entity.po.UserGroup;
import org.springframework.stereotype.Component;

/**
 * @author qqpet24
 * @see <a href=""></a><br/>
 * @since 2021/4/22 8:51
 */
@Component
public interface UserGroupMapper extends BaseMapper<UserGroup> {
}