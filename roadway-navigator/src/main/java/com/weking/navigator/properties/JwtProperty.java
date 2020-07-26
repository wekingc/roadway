package com.weking.navigator.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Jim Cen
 * JWT的配置
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperty {
    private long expire;
    private String secret ;
    private String issuer;
    private String audience;
}
