package com.weking.core.services.interfaces;

import com.weking.core.models.Gateway;
import com.weking.core.models.Route;

import java.util.Iterator;
import java.util.List;

/**
 * @author Jim Cen
 * @date 2020/7/14 13:21
 */
public interface GatewayService {
    /**
     * 添加
     * @param gateway Gateway
     * @return 是否成功
     */
    boolean addGateway(Gateway gateway);

    /**
     * 更新
     * @param gateway Gateway
     * @return 是否成功
     */
    boolean updateGateway(Gateway gateway);

    /**
     * 删除
     * @param name 名称
     * @return 是否成功
     */
    boolean deleteGateway(String name);

    /**
     * 获取
     * @param name 名称
     * @return 是否成功
     */
    Gateway getGateway(String name);

    /**
     * 显示所有网关
     * @return 全部的网关
     */
    List<Gateway> listAll();

    /**
     * 添加路由
     * @param gatewayName gateway
     * @param route 路由
     * @return 是否成功
     */
    default boolean addRoute(String gatewayName,Route route) {
        Gateway gateway = getGateway(gatewayName);
        if(!gateway.getRoutes().contains(route)) {
            gateway.getRoutes().add(route);
            return updateGateway(gateway);
        }
        return false;
    }

    /**
     * 删除路由
     * @param gatewayName gateway
     * @param path 路径
     * @return 是否成功
     */
    default boolean deleteRoute(String gatewayName,String path) {
        List<Route> routeList = getGateway(gatewayName).getRoutes();
        Iterator<Route> iterator = routeList.iterator();
        while(iterator.hasNext()) {
            Route route = iterator.next();
            if(path.equalsIgnoreCase(route.getPath())) {
                iterator.remove();
                Gateway gateway = new Gateway(gatewayName,routeList);
                return updateGateway(gateway);
            }
        }
        return true;
    }
}
