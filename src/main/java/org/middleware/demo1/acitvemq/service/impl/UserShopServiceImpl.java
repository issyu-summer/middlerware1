package org.middleware.demo1.acitvemq.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.middleware.demo1.acitvemq.entity.po.Record;
import org.middleware.demo1.acitvemq.entity.po.UserShop;
import org.middleware.demo1.acitvemq.mapper.RecordMapper;
import org.middleware.demo1.acitvemq.mapper.UserShopMapper;
import org.middleware.demo1.acitvemq.service.RecordService;
import org.middleware.demo1.acitvemq.service.UserShopService;
import org.springframework.stereotype.Service;

/**
 * @author qqpet24
 * @see <a href=""></a><br/>
 * @since 2021/4/22 8:52
 */
@Service
public class UserShopServiceImpl extends ServiceImpl<UserShopMapper, UserShop> implements UserShopService {
}
