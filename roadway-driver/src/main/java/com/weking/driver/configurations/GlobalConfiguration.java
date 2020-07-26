package com.weking.driver.configurations;


import com.weking.core.filters.TokenFilter;
import com.weking.core.services.interfaces.GatewayService;
import com.weking.core.models.Driver;
import com.weking.core.models.Navigator;
import com.weking.core.models.ResponseResult;
import com.weking.core.services.ApiCallerServer;
import com.weking.core.services.CryptoService;
import com.weking.core.services.GracefulShutdown;
import com.weking.core.services.RouteService;
import com.weking.driver.models.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

import java.util.Optional;

/**
 * @author Jim Cen
 * @date 2020/7/15 09:14
 */
@Configuration
public class GlobalConfiguration {
    @Bean
    public CryptoService cryptoService() {
        return new CryptoService();
    }

    @Bean
    @ConfigurationProperties(prefix = "navigator")
    public Navigator navigator() {
        return new Navigator();
    }

    @Bean
    public Driver driver(Server server) {
        Driver driver = new Driver(null,0,null);
        driver.setGatewayName(server.getDriverName());
        driver.setPort(Optional.ofNullable(server.getPort())
                               .orElse(GatewayService.DEFAULT_HOST_PORT));
        return driver;
    }


    @Bean
    public TokenFilter tokenFilter(CryptoService cryptoService, RequestMappingHandlerMapping requestMappingHandlerMapping){
        return new TokenFilter(cryptoService,requestMappingHandlerMapping);
    }

    @Bean
    public ApiCallerServer apiCallerServer(CryptoService cryptoService) {
        return new ApiCallerServer(cryptoService);
    }

    @Bean
    public GracefulShutdown gracefulShutdown(ReactorResourceFactory reactorResourceFactory) throws NoSuchFieldException, IllegalAccessException {
        return new GracefulShutdown(reactorResourceFactory);
    }

    @Autowired
    public void registry(ApiCallerServer apiCallerServer, Navigator navigator, Driver driver) throws Exception {
       ResponseResult result = apiCallerServer.call("http://" + navigator.getAddress() +
                                 GatewayService.HOST_PORT_SEPARATOR +navigator.getPort() +
                                 "/api/roadway/gateway/driver",driver);
        assert result != null;
        if(result.getCode() != ResponseResult.OK) {
           throw new RuntimeException("注册失败");
       }
    }

    @Bean
    public RouteService routeService() {
        return new RouteService();
    }
}
