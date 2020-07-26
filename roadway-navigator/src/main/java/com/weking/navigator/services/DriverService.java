package com.weking.navigator.services;

import com.weking.core.models.Gateway;
import com.weking.core.models.ResponseResult;
import com.weking.core.services.interfaces.AlarmService;
import com.weking.core.services.interfaces.GatewayService;
import com.weking.core.services.ApiCallerServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jim Cen
 * @date 2020/7/20 14:17
 */
@Service
public class DriverService {
    @Autowired
    ApiCallerServer apiCallerServer;

    @Autowired
    GatewayService gatewayService;

    @Autowired
    AlarmService alarmService;

    public List<ResponseResult> routeSync(String gatewayName) {
       List<Gateway> gateways = gatewayService.listAll();
       List<ResponseResult> results = new ArrayList<>();
       for(Gateway gateway:gateways) {
           if(gatewayName.equals(gateway.getName())) {
                List<String> hosts = gateway.getHosts();
                for(String host:hosts) {
                   ResponseResult result = apiCallerServer.call(apiCallerServer.PROTOCOL + host +"/api/roadway/driver/sync",
                                                                new HashMap<>(2));
                   results.add(result);
                }
           }
       }
       return results;
    }

    @Scheduled(fixedRate = 10000)
    public void heartBeatCheck() {
        gatewayService.listAll().forEach(gateway -> {
            List<String> hosts = gateway.getHosts();
            Iterator<String> iterator = hosts.iterator();
            boolean changed = false;
            while (iterator.hasNext()) {
                String host = iterator.next();
                try {
                    ClientResponse response = apiCallerServer.request(apiCallerServer.PROTOCOL + host +"/api/roadway/driver/heartbeat",
                            new HashMap<>(2)).block();
                    assert response != null;
                    if(!response.statusCode().is2xxSuccessful()) {
                        iterator.remove();
                        changed = true;
                    }
                }  catch (Exception ex) {
                    ex.printStackTrace();
                    iterator.remove();
                    changed = true;
                }
                if(changed) {
                    alarmService.alarm("gateway is offline",
                                       "<table>" +
                                               "<tr><td>Gateway Name</td>" +
                                               "<td>" + gateway.getName() + "</td>" +
                                               "</tr>" +
                                               "<tr><td>Host</td>" +
                                               "<td>" + host + "</td>" +
                                               "</tr>",true);
                }
            }
            if(changed) {
                gatewayService.updateGateway(gateway);
            }
        });
    }
}
