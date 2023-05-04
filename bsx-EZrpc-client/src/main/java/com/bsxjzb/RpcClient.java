package com.bsxjzb;

import com.bsxjzb.annotation.RpcAutowired;
import com.bsxjzb.manager.ServiceNodeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import com.bsxjzb.proxy.ServiceProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;


public class RpcClient implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private ServiceNodeManager serviceNodeManager;
    private static CountDownLatch latch = new CountDownLatch(1);

    public RpcClient(String registryAddress) {
        serviceNodeManager = new ServiceNodeManager(registryAddress);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                RpcAutowired annotation = field.getAnnotation(RpcAutowired.class);
                if (Objects.nonNull(annotation)) {
                    String version = annotation.version();
                    field.setAccessible(true);
                    try {
                        field.set(bean, createProxy(field.getType(), version));
                    } catch (IllegalAccessException e) {
                        logger.error("rpc field autowired failed : {} \nerror : \n{}", field.getType(), e);
                    }
                }
            }
        }
        latch.countDown();
    }

    public static CountDownLatch getReady() {
        return latch;
    } 

    @SuppressWarnings("unchecked")
    private <T> T createProxy(Class<T> interfaces, String version) {
        return (T) Proxy.newProxyInstance(interfaces.getClassLoader(),
                new Class<?>[]{interfaces},
                new ServiceProxy(version, serviceNodeManager));
    }


}
