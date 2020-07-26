package com.weking.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Jim Cen
 * @date 2020/7/15 09:00
 */
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Driver {
    String host;
    int port;
    String gatewayName;
}
