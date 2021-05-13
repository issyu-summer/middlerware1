package org.middleware.demo1.acitvemq.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.middleware.demo1.acitvemq.entity.po.User;
import org.middleware.demo1.acitvemq.mapper.UserMapper;
import org.middleware.demo1.acitvemq.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author summer
 * @see <a href=""></a><br/>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {
}
