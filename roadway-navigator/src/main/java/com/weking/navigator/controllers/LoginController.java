package com.weking.navigator.controllers;

import com.weking.core.services.interfaces.UserService;
import com.weking.core.models.ResponseResult;
import com.weking.core.models.User;
import com.weking.navigator.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Jim Cen
 * @date 2020/7/13 15:21
 */
@RestController
@RequestMapping("/api/roadway/login")
public class LoginController {
    @Autowired
    UserService userService;

    @Autowired
    JwtService jwtService;

    @PostMapping
    public Mono<ResponseResult> login(@RequestBody Mono<User> user) {
        return user.map(u ->{
            User us = userService.getUser(u.getName());
            ResponseResult result = new ResponseResult();
            if(us == null) {
                result.setCode(ResponseResult.FAILED);
                result.setMessage("账号不存在");
            }
            else if(!us.getPassword().equals(u.getPassword())) {
                result.setCode(ResponseResult.FAILED);
                result.setMessage("密码错误");
            }
            else {
                String token = jwtService.sign(u.getName());
                result.setCode(ResponseResult.OK);
                result.setMessage("登录成功");
                result.setData(token);
            }
            return result;
        });
    }
}