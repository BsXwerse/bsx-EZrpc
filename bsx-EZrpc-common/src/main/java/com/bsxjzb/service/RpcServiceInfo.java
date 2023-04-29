package com.bsxjzb.service;

import java.io.Serializable;

public class RpcServiceInfo implements Serializable {
    private static final Long serialVersionUID = 1L;

    private String serviceName;
    private String version;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
