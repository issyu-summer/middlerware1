package org.middleware.demo1.acitvemq.config;

import com.auth0.jwt.interfaces.Claim;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.middleware.demo1.acitvemq.config.util.TokenUtil;
import org.middleware.demo1.acitvemq.entity.po.User;
import org.middleware.demo1.acitvemq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author summer
 * @see <a href=""></a><br/>
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("authorization");

        if(token==null){
            Response<Object> forJson
                    = new Response<>().setCode(403).setMsg("forbidden,must have a token");
            data(forJson,response);
            return false;
        }

        Map<String, Claim> verify = TokenUtil.verify(token);
        String username = verify.get("username").asString();
        String password = verify.get("password").asString();

        User user = userService.getOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        if(!password.equals(user.getPassword())){
            Response<Object> forJson
                    = new Response<>().setCode(403).setMsg("username or password not right");
            data(forJson,response);
            return false;
        }
        return true;
    }

    private void data(Response<?> forJson,HttpServletResponse response){
        String data
                = null;
        try {
            data = objectMapper.writeValueAsString(forJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        try {
            assert data != null;
            response.getWriter().write(data.toCharArray());
            response.getWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
