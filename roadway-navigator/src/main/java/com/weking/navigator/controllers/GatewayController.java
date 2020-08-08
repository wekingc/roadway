package com.weking.navigator.controllers;

import com.weking.core.services.interfaces.GatewayService;
import com.weking.core.models.Gateway;
import com.weking.core.models.ResponseResult;
import com.weking.core.models.Route;
import com.weking.navigator.annotations.Authorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Jim Cen
 * @date 2020/7/14 17:10
 */
@RestController
@RequestMapping("/api/roadway/gateway")
public class GatewayController {
    @Autowired
    GatewayService gatewayService;

    @PostMapping("/route/{name}")
    public ResponseResult getRoutes(@PathVariable String name) {
        List<Route> routes = gatewayService.getGateway(name).getRoutes();
        return new ResponseResult(ResponseResult.OK,null,routes);
    }

    @PostMapping("/list")
    @Authorize
    public ResponseResult list() {
        List<Gateway> gateways = gatewayService.listAll();
        return  new ResponseResult(ResponseResult.OK,null,gateways);
    }

    @PostMapping("/route/add")
    @Authorize
    public Mono<ResponseResult> addRoute(ServerWebExchange exchange) {
       return exchange.getFormData().map(formData -> {
            String gatewayName = formData.getFirst("gateway");
            String path = formData.getFirst("path");
            String url = formData.getFirst("url");
            Route route = new Route(path,url);
            gatewayService.addRoute(gatewayName,route);
            return new ResponseResult();
        });
    }

    @PostMapping("/route/delete")
    @Authorize
    public Mono<ResponseResult> deleteRoute(ServerWebExchange exchange) {
        return exchange.getFormData().map(formData -> {
            String gatewayName = formData.getFirst("gateway");
            String path = formData.getFirst("path");
            gatewayService.deleteRoute(gatewayName,path);
            return new ResponseResult();
        });
    }
}
