package com.bsxjzb.registry;

import com.bsxjzb.zookeeper.CuratorClient;

public class ServiceRegistry {
    private CuratorClient curatorClient;

    public ServiceRegistry(String registryAddress) {
        curatorClient = new CuratorClient(registryAddress);
    }


}
