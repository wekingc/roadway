package com.weking.core;

import com.weking.core.models.ResponseResult;
import com.weking.core.models.properties.ZookeeperProperty;
import com.weking.core.services.zookeeper.ZookeeperGatewayServiceImpl;
import com.weking.core.services.zookeeper.ZookeeperService;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

/**
 * @author Jim Cen
 * @date 2020/7/16 15:00
 */
public class RoadwayCoreTest {
    //@Test
    void zookeeperTest() throws Exception {
        ZookeeperProperty zookeeperProperty = new ZookeeperProperty();
        zookeeperProperty.setTimeout(5000);
        zookeeperProperty.setNamespace("roadway");
        zookeeperProperty.setHosts("127.0.0.1:2181");
        ZookeeperService zookeeperService = new ZookeeperService(zookeeperProperty);
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework  client = CuratorFrameworkFactory.builder()
                                            .connectString(zookeeperProperty.getHosts())
                                            .sessionTimeoutMs(zookeeperProperty.getTimeout())
                                            .connectionTimeoutMs(zookeeperProperty.getTimeout())
                                            .retryPolicy(retryPolicy)
                                            .namespace(zookeeperProperty.getNamespace())
                                            .build();
        client.start();
        client.getChildren().forPath("/gateway");
        client.close();
    }

}
