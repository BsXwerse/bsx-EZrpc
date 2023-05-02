package com.bsxjzb.service;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpcServiceInfo that = (RpcServiceInfo) o;
        return Objects.equals(version, that.getVersion()) &&
                Objects.equals(serviceName, that.getServiceName());
    }
}
