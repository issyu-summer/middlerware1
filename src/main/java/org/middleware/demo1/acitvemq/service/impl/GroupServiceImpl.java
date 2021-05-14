package org.middleware.demo1.acitvemq.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.middleware.demo1.acitvemq.entity.po.File;
import org.middleware.demo1.acitvemq.mapper.FileMapper;
import org.middleware.demo1.acitvemq.mapper.GroupMapper;
import org.middleware.demo1.acitvemq.service.FileService;
import org.middleware.demo1.acitvemq.service.GroupService;
import org.springframework.stereotype.Service;
import org.middleware.demo1.acitvemq.entity.po.Group;
/**
 * @author qqpet24
 * @see <a href=""></a><br/>
 * @since 2021/4/22 8:52
 */
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {
}
