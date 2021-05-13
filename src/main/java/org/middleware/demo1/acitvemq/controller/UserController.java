package org.middleware.demo1.acitvemq.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.middleware.demo1.acitvemq.config.Response;
import org.middleware.demo1.acitvemq.config.util.TokenUtil;
import org.middleware.demo1.acitvemq.entity.po.Token;
import org.middleware.demo1.acitvemq.entity.po.User;
import org.middleware.demo1.acitvemq.entity.vo.StaffRetVo;
import org.middleware.demo1.acitvemq.entity.vo.UserVo;
import org.middleware.demo1.acitvemq.service.TokenService;
import org.middleware.demo1.acitvemq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author summer
 * @see <a href=""></a><br/>
 */
@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;


    /**
     * 登陆
     * @param userVo user info
     */
    @PostMapping("/login")
    public Object login(@RequestBody UserVo userVo){
        User user
                = userService.getOne(new QueryWrapper<User>().eq("username", userVo.getUsername()));
        if(user==null){
            return new Response<>().setCode(403).setMsg("username or password not right");
        }
        if(userVo.getPassword().equals(user.getPassword())){
            String token= TokenUtil.token(user.getUsername(),user.getPassword());
            Token tmp
                    = tokenService.getOne(new QueryWrapper<Token>().eq("username", userVo.getUsername()));
            if(tmp==null) {
                tokenService.save(new Token().setUsername(user.getUsername()).setToken(token));
            }else {
                tokenService.updateById(tmp.setToken(token));
            }
            userService.updateById(user.setOnline(1));
            return new Response<>().setCode(0).setMsg("OK").setData(token);
        }
        return new Response<>().setCode(403).setMsg("username or password not right");
    }

    /**
     * 登出
     * @param username username
     */
    @PostMapping("/logout")
    public Object logout(@RequestParam String username){
        User user
                = userService.getOne(new QueryWrapper<User>().eq("username",username));
        if(user.getType()==1){
            user.setType(2);
        }
        userService.updateById(user.setOnline(0));
        if(tokenService.remove(new QueryWrapper<Token>().eq("username", username))){
            return new Response<>().setCode(0).setMsg("OK");
        }
        return new Response<>().setCode(500).setMsg("server internal error");
    }

    /**
     * 分配一个客服
     * @return staff info
     */
    @PostMapping("/allocate/staff")
    public Object allocate(){
        List<User> list=userService.list(new QueryWrapper<User>().eq("type",2).eq("online",1));
        if(list.isEmpty()){
            User user=userService.getById(0);
            return new Response<>().setCode(0).setMsg("OK").setData(
                    new StaffRetVo().setType(user.getType()).setUserId(user.getId()).setUsername(user.getUsername())
            );
        }
        User user=list.get(0);
        user.setType(1);
        userService.updateById(user);
        return new Response<>().setCode(0).setMsg("OK").setData(
                new StaffRetVo().setType(user.getType()).setUserId(user.getId()).setUsername(user.getUsername())
        );
    }

    /**
     * 恢复客服的身份、以便再次利用
     * @param username staff name
     */
    @PutMapping("restore")
    public Object restore(@RequestParam String username){
        User user
                = userService.getOne(new QueryWrapper<User>().eq("username",username));
        user.setType(2);
        userService.updateById(user.setOnline(0));
        return new Response<>().setCode(0).setMsg("OK");
    }

}
