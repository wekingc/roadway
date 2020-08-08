package com.weking.core.filters;

import com.weking.core.models.Route;
import com.weking.core.models.properties.Constants;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

/**
 * @author Jim Cen
 * @date 2020/7/17 15:27
 */
public class PreUrlTransformFilter implements GatewayFilter, Ordered {
    private final int ROADWAY_PRE_URL_TRANSFORM_ORDER = 10000;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = (Route) exchange.getAttributes().get(Constants.ROADWAY_URL_TRANSFORM_ROUTE);
        ServerHttpRequest req = exchange.getRequest();
        addOriginalRequestUrl(exchange, req.getURI());
        String path = req.getURI().getRawPath();
        URI uri = URI.create(route.getDest());
        String[] routePathPieces = route.getPath().split("/");
        String[] pathPieces = path.split("/");
        String newPath = uri.getRawPath() + "/" + StringUtils.join(pathPieces,"/",routePathPieces.length,pathPieces.length);
        ServerHttpRequest newRequest  = req.mutate().path(newPath).uri(uri).build();
        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, newRequest.getURI());
        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    @Override
    public int getOrder() {
        return ROADWAY_PRE_URL_TRANSFORM_ORDER;
    }
}
