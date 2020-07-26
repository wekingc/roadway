package com.weking.core.services.zookeeper;

import com.weking.core.services.interfaces.GatewayService;
import com.weking.core.models.Gateway;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Jim Cen
 * @date 2020/7/14 16:39
 */
public class ZookeeperGatewayServiceImpl implements GatewayService {

    ZookeeperService zookeeperService;

    public ZookeeperGatewayServiceImpl(ZookeeperService zookeeperService) {
        this.zookeeperService = zookeeperService;
    }

    @Override
    public boolean addGateway(Gateway gateway) {
        return zookeeperService.write("/gateway/" + HtmlUtils.htmlEscape(gateway.getName()),gateway);
    }

    @Override
    public boolean updateGateway(Gateway gateway) {
        return zookeeperService.write("/gateway/" + HtmlUtils.htmlEscape(gateway.getName()),gateway);
    }

    @Override
    public boolean deleteGateway(String name) {
        return zookeeperService.delete("/gateway/" + HtmlUtils.htmlEscape(name));
    }

    @Override
    public Gateway getGateway(String name) {
        return zookeeperService.read("/gateway/" + HtmlUtils.htmlEscape(name), Gateway.class);
    }

    @Override
    public List<Gateway> listAll() {
        return zookeeperService.list("/gateway")
                               .stream()
                               .map(name ->  {
                                   return getGateway(name);
                               })
                               .collect(Collectors.toList());
    }

}
