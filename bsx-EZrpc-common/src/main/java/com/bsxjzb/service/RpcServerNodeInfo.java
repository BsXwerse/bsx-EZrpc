package com.bsxjzb.service;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class RpcServerNodeInfo implements Serializable {
    private static final Long serialVersionUID = 1L;

    private String host;
    private int port;
    private List<RpcServiceInfo> serviceList;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<RpcServiceInfo> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<RpcServiceInfo> serviceList) {
        this.serviceList = serviceList;
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, serviceList.hashCode());
    }
}
