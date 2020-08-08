package com.weking.driver.configurations;


import com.weking.core.services.GracefulShutdown;
import com.weking.core.services.interfaces.GatewayService;
import com.weking.driver.DriverRouteLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorResourceFactory;

/**
 * @author Jim Cen
 * @date 2020/7/15 09:14
 */
@Configuration
public class GlobalConfiguration {
    @Value("${server.name}")
    String gatewayName;
    @Autowired
    GatewayService gatewayService;

    @Bean
    public GracefulShutdown gracefulShutdown(ReactorResourceFactory reactorResourceFactory) throws NoSuchFieldException, IllegalAccessException {
        return new GracefulShutdown(reactorResourceFactory);
    }

    @Bean
    public DriverRouteLocator driverRouteLocator(RouteLocatorBuilder builder) {
        return new DriverRouteLocator(gatewayName, gatewayService);
    }
}
