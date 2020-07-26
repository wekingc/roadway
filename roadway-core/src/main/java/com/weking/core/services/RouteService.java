package com.weking.core.services;

import com.weking.core.filters.PreUrlTransformFilter;
import com.weking.core.models.ResponseResult;
import com.weking.core.services.interfaces.GatewayService;
import com.weking.core.models.Driver;
import com.weking.core.models.Navigator;
import com.weking.core.predicates.UrlMatchPredicate;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.gateway.route.CachingRouteLocator;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


/**
 * @author Jim Cen
 * @date 2020/7/17 15:13
 */
public class RouteService implements RouteLocator, InitializingBean, ApplicationContextAware {
    private RouteLocator routeLocator;
    private ApplicationContext applicationContext;
    private Driver driver;
    private Navigator navigator;
    private ApiCallerServer apiCallerServer;
    private CryptoService cryptoService;

    private final Map<String, Route> routes = Collections.synchronizedMap(new LinkedHashMap());
    private final Lock SYNC_LOCK = new ReentrantLock();

    @SneakyThrows
    public String getId(String path) {
        return cryptoService.md5Encode(path);
    }

    public void refresh() {
        routes.clear();
        getRoutesFromNavigator().forEach(route -> routes.put(getId(route.getPath()),
                                Route.async().id(getId(route.getPath())).uri(route.getDest())
                                             .asyncPredicate(new UrlMatchPredicate(route))
                                             .filter(new PreUrlTransformFilter(route))
                                             .build()));
        SYNC_LOCK.lock();
        try {
            ((CachingRouteLocator)routeLocator).refresh();
        }
        finally {
            SYNC_LOCK.unlock();
        }
    }

    public List<com.weking.core.models.Route> getRoutesFromNavigator() {
        ResponseResult result = apiCallerServer.call(apiCallerServer.PROTOCOL + navigator.getAddress() +
                                                        GatewayService.HOST_PORT_SEPARATOR +navigator.getPort() +
                                                        "/api/roadway/gateway/route/" +
                                                         driver.getGatewayName()
                                                        ,new HashMap<>(2));
        return  ((List<Map<String,String>>)result.getData()).stream()
                                        .map(hashMap -> new com.weking.core.models.Route(hashMap.get("path"),hashMap.get("dest")))
                                        .collect(Collectors.toList());
    }

    @Override
    public Flux<Route> getRoutes() {
        return Flux.fromIterable(routes.values());
    }

    @Override
    public void afterPropertiesSet() {
        routeLocator = applicationContext.getBean(RouteLocator.class);
        driver = applicationContext.getBean(Driver.class);
        navigator = applicationContext.getBean(Navigator.class);
        apiCallerServer = applicationContext.getBean(ApiCallerServer.class);
        cryptoService = applicationContext.getBean(CryptoService.class);
        refresh();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
