package com.weking.driver.controllers;

import com.weking.core.annotations.Token;
import com.weking.core.models.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Jim Cen
 * @date 2020/7/20 13:53
 */
@RestController
@RequestMapping("/api/roadway/driver/heartbeat")
public class HeartbeatController {
    @PostMapping
    @Token
    public ResponseResult heartbeat() {
        return new ResponseResult();
    }
}
