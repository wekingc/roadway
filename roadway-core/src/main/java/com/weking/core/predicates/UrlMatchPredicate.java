package com.weking.core.predicates;

import com.weking.core.models.Route;
import com.weking.core.models.properties.Constants;
import com.weking.core.services.interfaces.GatewayService;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Jim Cen
 * @date 2020/7/17 15:29
 */
public class UrlMatchPredicate implements AsyncPredicate<ServerWebExchange> {
    private final String gatewayName;
    private final GatewayService gatewayService;
    public UrlMatchPredicate(String gatewayName, GatewayService gatewayService) {
        this.gatewayName = gatewayName;
        this.gatewayService = gatewayService;
    }

    @Override
    public Publisher<Boolean> apply(ServerWebExchange exchange) {
        return Mono.just(exchange).map(e -> {
            System.out.println("------------------");
            String path = exchange.getRequest().getPath().value().toLowerCase() + "/";
            List<Route> routeList = gatewayService.getGateway(gatewayName).getRoutes();
            System.out.println(path);
            for (Route route: routeList) {
                String pathPattern = route.getPath().toLowerCase() + "/";
                System.out.println(pathPattern);
                if(path.startsWith((pathPattern))) {
                    exchange.getAttributes().put(Constants.ROADWAY_URL_TRANSFORM_ROUTE,route);
                    return true;
                }
            }
            System.out.println("------------------");
            return false;
        });
    }
}
