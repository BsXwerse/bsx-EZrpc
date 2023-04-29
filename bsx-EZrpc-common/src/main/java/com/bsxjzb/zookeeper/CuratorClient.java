package com.bsxjzb.zookeeper;

import com.bsxjzb.constant.SysConstant;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CuratorClient {
    private static final Logger logger = LoggerFactory.getLogger(CuratorClient.class);

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
        logger.info("Registration connection is successful");
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

    public String createPathData(String path, byte[] data) throws Exception {
        return client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath(path, data);
    }

    public void deletePath(String path) throws Exception {
        client.delete().deletingChildrenIfNeeded().forPath(path);
    }

    public void addConnectionStateListener(ConnectionStateListener connectionStateListener) {
        client.getConnectionStateListenable().addListener(connectionStateListener);
    }

    public void close() {
        client.close();
    }
}
