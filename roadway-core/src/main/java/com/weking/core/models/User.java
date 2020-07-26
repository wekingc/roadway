package com.weking.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Jim Cen
 * @date 2020/7/10 16:21
 */
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class User {
    String name;
    String password;
}
