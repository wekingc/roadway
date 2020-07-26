package com.weking.navigator.controllers;

import com.weking.core.annotations.Token;
import com.weking.core.services.interfaces.GatewayService;
import com.weking.core.models.Driver;
import com.weking.core.models.Gateway;
import com.weking.core.models.ResponseResult;
import com.weking.core.models.Route;
import com.weking.core.services.ApiCallerServer;
import com.weking.navigator.annotations.Authorize;
import com.weking.navigator.services.DriverService;
import jdk.internal.org.objectweb.asm.commons.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
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

    @Autowired
    ApiCallerServer apiCallerServer;

    @Autowired
    DriverService driverService;

    @PostMapping("/driver")
    @Token
    public Mono<ResponseResult> registerDriver(@RequestBody Mono<Driver> driver, ServerWebExchange exchange) {
        return driver.map(d -> {
            String address = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            d.setHost(address);
            gatewayService.registerDriver(d);
            return new ResponseResult();
        });
    }

    @PostMapping("/route/{name}")
    @Token
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
            driverService.routeSync(gatewayName).forEach(responseResult -> {
                System.out.println(responseResult);
            });
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
            driverService.routeSync(gatewayName);
            return new ResponseResult();
        });
    }
}
