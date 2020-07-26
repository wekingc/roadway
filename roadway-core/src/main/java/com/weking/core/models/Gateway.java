package com.weking.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Jim Cen
 * @date 2020/7/14 16:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Gateway {
    String name;
    List<String> hosts;
    List<Route> routes;
}
