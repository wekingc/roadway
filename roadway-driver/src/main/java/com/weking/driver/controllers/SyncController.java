package com.weking.driver.controllers;

import com.weking.core.annotations.Token;
import com.weking.core.models.ResponseResult;
import com.weking.core.services.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jim Cen
 * @date 2020/7/20 09:56
 */
@RestController
@RequestMapping("/api/roadway/driver/sync")
public class SyncController {
    @Autowired
    RouteService routeService;

    @PostMapping
    @Token
    public ResponseResult sync() {
        routeService.refresh();
        return new ResponseResult();
    }
}
