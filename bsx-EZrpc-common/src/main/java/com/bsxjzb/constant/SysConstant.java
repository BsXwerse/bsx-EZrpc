package com.bsxjzb.constant;

public class SysConstant {
    public static final String ZOOKEEPER_NAMESPACE = "bsx-EZrpc";
    public static final int ZOOKEEPER_CONNECTION_TIMEOUT = 5 * 1000;
    public static final int ZOOKEEPER_SESSION_TIMEOUT = 10 * 1000;
    public static final String ZOOKEEPER_REGISTRY_DATA_PATH_PREFIX = "/server-registry/data-";
    public static final String SERVICE_CONCAT_TOKEN = "#";
    public static final int BEAT_INTERVAL = 30;
    public static final int BEAT_TIME_OUT = BEAT_INTERVAL * 3;
    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    public static final String BEAT_ID = "beat!";
}
