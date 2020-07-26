package com.weking.core.models.properties;

import lombok.Data;

/**
 * @author Jim Cen
 * @date 2020/7/14 11:59
 * Zookeeper配置
 */
@Data
public class ZookeeperProperty {
    private String hosts;
    private String namespace;
    private int timeout;
}