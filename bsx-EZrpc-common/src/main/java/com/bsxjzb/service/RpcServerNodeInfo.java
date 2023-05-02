package com.bsxjzb.service;

import org.apache.commons.collections4.CollectionUtils;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpcServerNodeInfo that = (RpcServerNodeInfo) o;
        return this.port == that.getPort() &&
                this.host.equals(that.getHost()) &&
                isListEquals(this.serviceList, that.getServiceList());
    }

    private boolean isListEquals(List<RpcServiceInfo> a, List<RpcServiceInfo> b) {
        if (a == b) return true;
        if (a == null && b != null ||
            a != null && b == null ||
            a.size() != b.size()) return false;
        return CollectionUtils.isEqualCollection(a, b);
    }
}
