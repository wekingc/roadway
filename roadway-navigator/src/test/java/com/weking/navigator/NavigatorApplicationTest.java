package com.weking.navigator;

import com.weking.core.models.ResponseResult;
import com.weking.core.services.ApiCallerServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

/**
 * @author Jim Cen
 * @date 2020/7/16 15:00
 */
@SpringBootTest
public class NavigatorApplicationTest {
    @Autowired
    ApiCallerServer apiCallerServer;

    @Test
    void contextLoads()  {
        ResponseResult result = apiCallerServer.call("http://127.0.0.1:8090/api/roadway/gateway/route/g1"
                ,new HashMap<>(2));
        System.out.println(result);
    }
}
