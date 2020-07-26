package com.weking.core.services;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.TcpResources;
import java.lang.reflect.Field;
import java.time.Duration;

/**
 * @author Jim Cen
 * @date 2020/7/16 16:23
 */
public class GracefulShutdown implements ApplicationContextAware {
    LoopResources loopResources;
    ApplicationContext applicationContext;
    public GracefulShutdown(ReactorResourceFactory reactorResourceFactory) throws NoSuchFieldException, IllegalAccessException {
        Field field = TcpResources.class.getDeclaredField("defaultLoops");
        field.setAccessible(true);
        loopResources = (LoopResources) field.get(reactorResourceFactory.getLoopResources());
        field.setAccessible(false);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            loopResources.disposeLater().block(Duration.ofSeconds(20));
        }));
    }

    public void shutdown() {
        int exitCode = SpringApplication.exit(applicationContext, () -> 0);
        System.exit(exitCode);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}