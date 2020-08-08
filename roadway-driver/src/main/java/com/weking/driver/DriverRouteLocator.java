package com.weking.driver;

import com.weking.core.filters.PreUrlTransformFilter;
import com.weking.core.predicates.UrlMatchPredicate;
import com.weking.core.services.interfaces.GatewayService;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import reactor.core.publisher.Flux;

import java.net.URI;

/**
 * @author Jim Cen
 */
public class DriverRouteLocator implements RouteLocator {
    final String DRIVER_URL_TRANSFORM_ROUTE = "DRIVER_URL_TRANSFORM_ROUTE";
    final String LOCAL_HOST = "http://127.0.0.1";

    final String gatewayName;
    final GatewayService gatewayService;

    public DriverRouteLocator(String gatewayName, GatewayService gatewayService) {
        this.gatewayName = gatewayName;
        this.gatewayService = gatewayService;
    }

    @Override
    public Flux<Route> getRoutes() {
        System.out.println(gatewayService.getGateway(gatewayName));
        return Flux.just(Route.async().id(DRIVER_URL_TRANSFORM_ROUTE).uri(URI.create(LOCAL_HOST))
                         .asyncPredicate(new UrlMatchPredicate(gatewayName,gatewayService))
                         .filters(new PreUrlTransformFilter())
                         .build());
    }
}
