package com.weking.core.services.interfaces;


import com.weking.core.models.User;

/**
 * @author Jim Cen
 * @date 2020/7/10 16:24
 */
public interface UserService {
    String DEFAULT_USER = "admin";
    String DEFAULT_PASSWORD = "123456";
    /**
     * 获取用户
     * @param name 姓名
     * @return 用户
     */
    User getUser(String name);

    /**
     * 添加用户
     * @param user 用户
     * @return 是否成功
     */
    boolean addUser(User user);

    /**
     * 添加用户
     * @param name 姓名
     * @return 是否成功
     */
    boolean deleteUser(String name);
}
