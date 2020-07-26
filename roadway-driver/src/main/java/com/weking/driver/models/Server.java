package com.weking.driver.models;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Jim Cen
 * @date 2020/7/15 09:17
 */
@Data
@Component
public class Server {
    @Value("${server.port:#{null}}")
    private int port;
    @Value("${server.name:gateway}")
    private String driverName;
}
