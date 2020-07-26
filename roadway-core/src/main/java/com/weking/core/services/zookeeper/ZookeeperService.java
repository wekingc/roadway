package com.weking.core.services.zookeeper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weking.core.models.properties.ZookeeperProperty;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.InitializingBean;

import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 * @author Jim Cen
 * @date 2020/7/14 12:06
 */
public class ZookeeperService implements InitializingBean {
    ZookeeperProperty zookeeperProperty;

    private final int BASE_SLEEP_TIME_MS = 1000;
    private final int MAX_RETRIES = 3;

    public ZookeeperService(ZookeeperProperty zookeeperProperty) {
        this.zookeeperProperty = zookeeperProperty;
    }

    private CuratorFramework getClient() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES);
        return CuratorFrameworkFactory.builder()
                .connectString(zookeeperProperty.getHosts())
                .sessionTimeoutMs(zookeeperProperty.getTimeout())
                .connectionTimeoutMs(zookeeperProperty.getTimeout())
                .retryPolicy(retryPolicy)
                .namespace(zookeeperProperty.getNamespace())
                .build();
    }

    public boolean delete(String path) {
        try (CuratorFramework client = getClient()) {
            client.delete().forPath(path);
            return  true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean create(String path) {
        try (CuratorFramework client = getClient()) {
            client.start();
            Stat stat = client.checkExists().forPath(path);
            if(stat == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(path,new byte[]{0});
            }
            return  true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean write(String path,Object value) {
        try (CuratorFramework client = getClient()) {
            client.start();
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(value);
            Stat stat = client.checkExists().forPath(path);
            if(stat == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(path, json.getBytes(StandardCharsets.UTF_8));
            }
            else {
                client.setData().forPath(path, json.getBytes(StandardCharsets.UTF_8));
            }
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    public <T> T read(String path,Class<T> type){
        try (CuratorFramework client = getClient()) {
            ObjectMapper objectMapper = new ObjectMapper();
            client.start();
            String value = new String(client.getData().forPath(path), StandardCharsets.UTF_8);
            return objectMapper.readValue(value, type);
        }
        catch (Exception ex) {
            return null;
        }
    }

    public List<String> list(String path) {
        try (CuratorFramework client = getClient()) {
            client.start();
            return client.getChildren().forPath(path);
        }
        catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void afterPropertiesSet() {
        create("/user");
        create("/gateway");
    }
}