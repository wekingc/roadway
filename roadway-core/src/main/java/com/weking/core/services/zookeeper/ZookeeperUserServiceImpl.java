package com.weking.core.services.zookeeper;

import com.weking.core.services.interfaces.UserService;
import com.weking.core.models.User;
import org.springframework.web.util.HtmlUtils;
/**
 * @author Jim Cen
 * @date 2020/7/13 14:10
 */
public class ZookeeperUserServiceImpl implements UserService {
    ZookeeperService zookeeperService;

    public ZookeeperUserServiceImpl(ZookeeperService zookeeperService) {
        this.zookeeperService = zookeeperService;
    }

    @Override
    public User getUser(String name) {
        return zookeeperService.read("/user/" + HtmlUtils.htmlEscape(name),User.class);
    }

    @Override
    public boolean addUser(User user) {
        return zookeeperService.write("/user/" + HtmlUtils.htmlEscape(user.getName()),user);
    }

    @Override
    public boolean deleteUser(String name) {
        return zookeeperService.delete("/user/" + HtmlUtils.htmlEscape(name));
    }
}
