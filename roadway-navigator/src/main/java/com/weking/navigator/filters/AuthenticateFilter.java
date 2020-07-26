package com.weking.navigator.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weking.navigator.annotations.Authorize;
import com.weking.core.models.ResponseResult;
import com.weking.navigator.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.condition.PatternsRequestCondition;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

/**
 * @author Jim Cen
 * @date 2020/7/14 10:57
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class AuthenticateFilter implements WebFilter {
    @Autowired
    RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Map<RequestMappingInfo, HandlerMethod> methods  =  requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : methods.entrySet()) {
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();
            if(method.getMethod().isAnnotationPresent(Authorize.class)) {
                PatternsRequestCondition p = info.getPatternsCondition();
                for (PathPattern url : p.getPatterns()) {
                    if( exchange.getRequest().getMethod() == HttpMethod.POST &&
                            url.toString().equalsIgnoreCase(exchange.getRequest().getPath().toString())) {

                        String token = Optional.ofNullable(exchange.getRequest()
                                .getHeaders()
                                .getFirst("Authorization"))
                                .orElse("")
                                .replace("Bearer ","");
                        if(!jwtService.authenticate(token)){
                            exchange.getResponse().setStatusCode(HttpStatus.OK);
                            exchange.getResponse().getHeaders().set("Content-type", "application/json;charset=UTF-8");
                            ResponseResult result = new ResponseResult();
                            result.setCode(ResponseResult.UNAUTHORIZED);
                            result.setMessage("JWT token不正确");
                            ObjectMapper mapper=new ObjectMapper();
                            DataBuffer body = null;
                            try {
                                body = exchange.getResponse()
                                        .bufferFactory()
                                        .wrap(mapper.writeValueAsString(result).getBytes(StandardCharsets.UTF_8));
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                            return exchange.getResponse().writeWith(Mono.just(body));
                        }
                    }
                }
            }
        }
        return chain.filter(exchange);
    }
}
