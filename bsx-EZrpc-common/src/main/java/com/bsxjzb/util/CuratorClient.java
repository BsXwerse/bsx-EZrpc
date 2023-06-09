package com.bsxjzb.util;

import com.bsxjzb.constant.SysConstant;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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

    public List<String> getChildren(String path) throws Exception {
        return client.getChildren().forPath(path);
    }

    public byte[] getData(String path) throws Exception {
        return client.getData().forPath(path);
    }

    public void watchPathChildrenNode(String path, PathChildrenCacheListener listener) throws Exception {
        PathChildrenCache cache = new PathChildrenCache(client, path, true);
        cache.start(PathChildrenCache.StartMode.NORMAL);
        cache.getListenable().addListener(listener);
    }

    public void close() {
        client.close();
    }
}
