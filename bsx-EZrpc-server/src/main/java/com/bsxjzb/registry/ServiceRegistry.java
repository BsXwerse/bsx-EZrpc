package com.bsxjzb.registry;

import com.bsxjzb.constant.SysConstant;
import com.bsxjzb.service.RpcServerNodeInfo;
import com.bsxjzb.service.RpcServiceInfo;
import com.bsxjzb.util.JsonUtil;
import com.bsxjzb.util.CuratorClient;
import org.apache.curator.framework.state.ConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

    private CuratorClient curatorClient;
    private List<String> pathList;

    public ServiceRegistry(String registryAddress) {
        curatorClient = new CuratorClient(registryAddress);
    }

    public void registry(String serverAddress, Map<String, Object> serviceMap) {
        pathList = new ArrayList<>();
        List<RpcServiceInfo> serviceInfoList = new ArrayList<>();
        for (String key : serviceMap.keySet()) {
            String[] serviceInfo = key.split(SysConstant.SERVICE_CONCAT_TOKEN);
            if (serviceInfo.length > 0) {
                RpcServiceInfo rpcServiceInfo = new RpcServiceInfo();
                rpcServiceInfo.setServiceName(serviceInfo[0]);
                if (serviceInfo.length == 2) {
                    rpcServiceInfo.setVersion(serviceInfo[1]);
                } else {
                    rpcServiceInfo.setVersion("");
                }
                serviceInfoList.add(rpcServiceInfo);
                logger.info("Service {} has been added", key);
            } else {
                logger.warn("Unable to resolve service: {}", key);
            }
        }
        String[] address = serverAddress.split(":");
        RpcServerNodeInfo rpcServerNodeInfo = new RpcServerNodeInfo();
        rpcServerNodeInfo.setHost(address[0]);
        rpcServerNodeInfo.setPort(Integer.parseInt(address[1]));
        rpcServerNodeInfo.setServiceList(serviceInfoList);
        byte[] data = JsonUtil.objectToJson(rpcServerNodeInfo).getBytes();
        String path = SysConstant.ZOOKEEPER_REGISTRY_DATA_PATH_PREFIX + rpcServerNodeInfo.hashCode();
        try {
            path = curatorClient.createPathData(path, data);
            pathList.add(path);
            logger.info("Register service successfully");
        } catch (Exception e) {
            logger.error("Register service fail, exception: {}", e.getMessage());
        }
        curatorClient.addConnectionStateListener((client, newState) -> {
            if (newState == ConnectionState.RECONNECTED) {
                logger.info("server reconnecting, register service after reconnected");
                registry(serverAddress, serviceMap);
            }
        });
    }

    public void unregister() {
        for (String path : pathList) {
            try {
                curatorClient.deletePath(path);
            } catch (Exception e) {
                logger.error("Delete service path error: " + e.getMessage());
            }
        }
        logger.info("Services unregistered successfully");
        curatorClient.close();
        logger.info("Registration connection closed");
    }

}
