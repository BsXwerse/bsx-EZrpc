package com.bsxjzb;

import com.bsxjzb.annotation.RpcService;
import com.bsxjzb.constant.SysConstant;
import com.bsxjzb.registry.ServiceRegistry;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

public class RpcServer implements ApplicationContextAware {
    private Thread nettyServer;
    private String serverAddress;
    private Map<String, Object> serviceMap = new HashMap<>();
    private ServiceRegistry serviceRegistry;

    public RpcServer(String serverAddress, String registryAddress) {
        this.serverAddress = serverAddress;
        serviceRegistry = new ServiceRegistry(registryAddress);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> service = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(service)) {
            for (Object obj : service.values()) {
                RpcService annotation = obj.getClass().getAnnotation(RpcService.class);
                String interfaceName = annotation.value().getName();
                String version = annotation.version();
                if (StringUtils.hasText(version)) {
                    interfaceName += SysConstant.SERVICE_CONCAT_TOKEN.concat(version);
                }
                serviceMap.put(interfaceName, obj);
            }
        }
    }

    @PostConstruct
    public void start() {
        //zookeeper注册服务，new nettyThread运行
    }

    @PreDestroy
    public void stop() {

    }
}
