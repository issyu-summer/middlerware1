package org.middleware.demo1.acitvemq.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.middleware.demo1.acitvemq.entity.po.Token;
import org.middleware.demo1.acitvemq.mapper.TokenMapper;
import org.middleware.demo1.acitvemq.service.TokenService;
import org.springframework.stereotype.Service;

/**
 * @author summer
 * @see <a href=""></a><br/>
 */
@Service
public class TokenServiceImpl extends ServiceImpl<TokenMapper, Token> implements TokenService {
}
