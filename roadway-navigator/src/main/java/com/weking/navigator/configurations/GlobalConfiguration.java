package com.weking.navigator.configurations;

import com.weking.core.models.User;
import com.weking.core.services.CryptoService;
import com.weking.core.services.GracefulShutdown;
import com.weking.core.services.impl.DefaultAlarmServiceImpl;
import com.weking.core.services.interfaces.AlarmService;
import com.weking.core.services.interfaces.LogService;
import com.weking.core.services.interfaces.UserService;
import com.weking.navigator.exceptions.handlers.GlobalExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

/**
 * @author Jim Cen
 * @date 2020/7/13 14:29
 */
@Configuration
@EnableConfigurationProperties({ServerProperties.class, ResourceProperties.class})
public class GlobalConfiguration {
    @Autowired
    UserService userService;
    @Autowired
    ServerProperties serverProperties;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ResourceProperties resourceProperties;
    @Autowired
    ObjectProvider<List<ViewResolver>> viewResolversProvider;
    @Autowired
    ServerCodecConfigurer serverCodecConfigurer;
    @Autowired
    LogService logService;
    @Autowired
    ErrorAttributes errorAttributes;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalExceptionHandler globalExceptionHandler() {
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(
                                                                    errorAttributes,
                                                                    resourceProperties,
                                                                    serverProperties.getError(),
                                                                    applicationContext,
                                                                    logService);
        exceptionHandler.setViewResolvers(viewResolversProvider.getIfAvailable(Collections::emptyList));
        exceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
        exceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
        return exceptionHandler;
    }

    @Bean
    public CryptoService cryptoService() {
        return new CryptoService();
    }

    @Autowired
    public void defaultUser(CryptoService cryptoService) throws NoSuchAlgorithmException {
        if(userService.getUser(UserService.DEFAULT_USER) == null) {
            User defUser = new User(UserService.DEFAULT_USER,cryptoService.md5Encode(UserService.DEFAULT_PASSWORD));
            userService.addUser(defUser);
        }
    }

    @Bean
    public RouterFunction<ServerResponse> homeRouter(AlarmService alarmService) {
        return RouterFunctions
                .route(RequestPredicates.GET("/"), request ->
                        ServerResponse.permanentRedirect(URI.create("/html/login.html")).build()
                );
    }

    @Bean
    public GracefulShutdown gracefulShutdown(ReactorResourceFactory reactorResourceFactory) throws NoSuchFieldException, IllegalAccessException {
        return new GracefulShutdown(reactorResourceFactory);
    }

    @Bean
    public AlarmService alarmService() {
        return new DefaultAlarmServiceImpl();
    }
}
