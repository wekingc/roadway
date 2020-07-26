package com.weking.navigator.controllers;

import com.weking.navigator.annotations.Authorize;
import com.weking.core.models.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Jim Cen
 * @date 2020/7/13 17:15
 */
@RestController
@RequestMapping("/api/roadway/auth")
public class AuthenticateController {
    @PostMapping
    @Authorize
    public Mono<ResponseResult> authenticate() {
        ResponseResult result = new ResponseResult(ResponseResult.OK,"验证成功",null);
        return Mono.just(result);
    }
}
