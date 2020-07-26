package com.weking.core.predicates;

import com.weking.core.models.Route;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Jim Cen
 * @date 2020/7/17 15:29
 */
public class UrlMatchPredicate implements AsyncPredicate<ServerWebExchange> {
    private Route route;
    public UrlMatchPredicate(Route route) {
        this.route = route;
    }

    @Override
    public Publisher<Boolean> apply(ServerWebExchange exchange) {
        return Mono.just(exchange).map(e -> {
            String path = exchange.getRequest().getPath().value().toLowerCase() + "/";
            String pathPattern = route.getPath().toLowerCase() + "/";
            if(path.startsWith((pathPattern))) {
                return true;
            }
            else {
                return false;
            }
        });
    }
}
