package com.weking.core.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weking.core.annotations.Token;
import com.weking.core.models.Ticket;
import com.weking.core.services.CryptoService;
import lombok.SneakyThrows;
import org.springframework.core.Ordered;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.condition.PatternsRequestCondition;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Jim Cen
 * @date 2020/7/15 13:53
 */
public class TokenFilter implements WebFilter, Ordered {

    private final long ALLOW_MAX_TIME_MILLIS =3000L;

    private CryptoService cryptoService;
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    public TokenFilter(CryptoService cryptoService,RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.cryptoService = cryptoService;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    @SneakyThrows
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Map<RequestMappingInfo, HandlerMethod> methods  =  requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : methods.entrySet()) {
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();
            if (method.getMethod().isAnnotationPresent(Token.class)) {
                PatternsRequestCondition p = info.getPatternsCondition();
                for (PathPattern url : p.getPatterns()) {
                    if(url.toString().equalsIgnoreCase(exchange.getRequest().getPath().toString())) {
                        String strTicket = exchange.getRequest().getHeaders().getFirst("ticket");
                        ObjectMapper objectMapper = new ObjectMapper();
                        Ticket ticket = objectMapper.readValue(strTicket,Ticket.class);
                        long timestamp = ticket.getTimestamp();
                        if (Math.abs(timestamp - System.currentTimeMillis()) > ALLOW_MAX_TIME_MILLIS) {
                            throw new Exception("timestamp已经失效");
                        }
                        String token = cryptoService.generateToken(ticket.getTimestamp());
                        if(token.equals(ticket.getToken())) {
                            return chain.filter(exchange);
                        }
                        else {
                            throw new Exception("ticket有问题");
                        }
                    }
                }
            }
        }
        return chain.filter(exchange);
    }
}
