package org.middleware.demo1.acitvemq.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.middleware.demo1.acitvemq.entity.po.Group;
import org.middleware.demo1.acitvemq.entity.po.Record;
import org.middleware.demo1.acitvemq.mapper.GroupMapper;
import org.middleware.demo1.acitvemq.mapper.RecordMapper;
import org.middleware.demo1.acitvemq.service.GroupService;
import org.middleware.demo1.acitvemq.service.RecordService;
import org.springframework.stereotype.Service;

/**
 * @author qqpet24
 * @see <a href=""></a><br/>
 * @since 2021/4/22 8:52
 */
@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {
}