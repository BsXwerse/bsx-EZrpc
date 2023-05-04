package com.bsxjzb.proxy;

import com.bsxjzb.constant.SysConstant;
import com.bsxjzb.protocol.RpcRequest;
import com.bsxjzb.handler.RpcClientHandler;
import com.bsxjzb.handler.RpcFuture;
import io.netty.channel.Channel;
import com.bsxjzb.manager.ServiceNodeManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

public class ServiceProxy implements InvocationHandler {
    private String version;
    private ServiceNodeManager serviceNodeManager;

    public ServiceProxy(String version, ServiceNodeManager serviceNodeManager) {
        this.version = version;
        this.serviceNodeManager = serviceNodeManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            switch (name) {
                case "equals":
                    return proxy == args[0];
                case "hashCode":
                    return System.identityHashCode(proxy);
                case "toString":
                    return proxy.getClass().getName() + "@" +
                            Integer.toHexString(System.identityHashCode(proxy)) +
                            ", with InvocationHandler " + this;
                default:
                    throw new IllegalStateException(String.valueOf(method));
            }
        }

        RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString());
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setVersion(version);
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setParameterValues(args);

        String serviceKey = method.getDeclaringClass().getName().concat(SysConstant.SERVICE_CONCAT_TOKEN)
                .concat(version);
        Channel connection = serviceNodeManager.getConnection(serviceKey);
        RpcFuture rpcFuture = connection.pipeline().get(RpcClientHandler.class).sendRequest(rpcRequest);
        return rpcFuture.get();
    }
}
