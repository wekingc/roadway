package com.weking.driver.configurations;

import com.weking.core.models.properties.ZookeeperProperty;
import com.weking.core.services.interfaces.GatewayService;
import com.weking.core.services.interfaces.UserService;
import com.weking.core.services.zookeeper.ZookeeperGatewayServiceImpl;
import com.weking.core.services.zookeeper.ZookeeperService;
import com.weking.core.services.zookeeper.ZookeeperUserServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jim Cen
 * @date 2020/7/14 10:18
 */
@Configuration
@ConditionalOnProperty(prefix = "zookeeper",value = "hosts")
public class ZookeeperConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "zookeeper")
    public ZookeeperProperty zookeeperProperty() {
        return new ZookeeperProperty();
    }

    @Bean
    public ZookeeperService zookeeperService(ZookeeperProperty zookeeperProperty) {
        return new ZookeeperService(zookeeperProperty);
    }

    @Bean
    public GatewayService gatewayService(ZookeeperService zookeeperService) {
        return new ZookeeperGatewayServiceImpl(zookeeperService);
    }
}
