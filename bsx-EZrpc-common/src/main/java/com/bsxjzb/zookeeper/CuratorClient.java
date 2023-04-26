package com.bsxjzb.zookeeper;

import com.bsxjzb.constant.SysConstant;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CuratorClient {
    private CuratorFramework client;

    public CuratorClient(String connectString, String namespace,
                         int connectionTimeout, int sessionTimeout) {
        client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .namespace(namespace)
                .sessionTimeoutMs(sessionTimeout)
                .connectionTimeoutMs(connectionTimeout)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10))
                .build();
        client.start();
    }

    public CuratorClient(String connectString, String namespace) {
        this(connectString, namespace,
                SysConstant.ZOOKEEPER_CONNECTION_TIMEOUT, SysConstant.ZOOKEEPER_SESSION_TIMEOUT);
    }

    public CuratorClient(String connectString) {
        this(connectString,
                SysConstant.ZOOKEEPER_NAMESPACE,
                SysConstant.ZOOKEEPER_CONNECTION_TIMEOUT,
                SysConstant.ZOOKEEPER_SESSION_TIMEOUT);
    }
}
