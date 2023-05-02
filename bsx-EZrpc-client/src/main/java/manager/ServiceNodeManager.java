package manager;

import com.bsxjzb.constant.SysConstant;
import com.bsxjzb.service.RpcServerNodeInfo;
import com.bsxjzb.util.CuratorClient;
import com.bsxjzb.util.JsonUtil;
import io.netty.channel.Channel;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import route.ServerNodeRouter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

public class ServiceNodeManager {
    private static final Logger logger = LoggerFactory.getLogger(ServiceNodeManager.class);

    private CuratorClient curatorClient;
    private ServerChannelManager serverChannelManager = new ServerChannelManager();
    private ServerNodeRouter serverNodeRouter = new ServerNodeRouter();
    private CopyOnWriteArraySet<RpcServerNodeInfo> nodeSet = new CopyOnWriteArraySet<>();

    public ServiceNodeManager(String registryAddress) {
        curatorClient = new CuratorClient(registryAddress);
        init();
    }

    private void init() {
        updateServiceNode();
        try {
            curatorClient.watchPathChildrenNode(SysConstant.ZOOKEEPER_REGISTRY_PATH, (client, event) -> {
                ChildData data = event.getData();
                PathChildrenCacheEvent.Type type = event.getType();
                switch (type) {
                    case CONNECTION_RECONNECTED:
                        logger.info("Reconnected to zk, try to get latest service list");
                        updateServiceNode();
                        break;
                    case CHILD_ADDED:
                        String add = new String(data.getData());
                        if (nodeSet.add(JsonUtil.jsonToObject(add, RpcServerNodeInfo.class))) {
                            serverNodeRouter.update(nodeSet);
                            logger.info("add service node : {}", add);
                        }
                        break;
                    case CHILD_REMOVED:
                        String remove = new String(data.getData());
                        if (nodeSet.remove(JsonUtil.jsonToObject(remove, RpcServerNodeInfo.class))) {
                            serverNodeRouter.update(nodeSet);
                            serverChannelManager.update(nodeSet);
                            logger.info("remove service node : {}", remove);
                        }
                        break;
                    default:
                        logger.warn("service node status error, update...");
                        updateServiceNode();
                }
            });
        } catch (Exception e) {
            logger.error("Watch node exception: " + e.getMessage());
        }
    }

    private void updateServiceNode() {
        List<RpcServerNodeInfo> nodeList = new ArrayList<>();
        try {
            List<String> nodePathList = curatorClient.getChildren(SysConstant.ZOOKEEPER_REGISTRY_PATH);
            for (String path : nodePathList) {
               String json = new String(curatorClient.getData(SysConstant.ZOOKEEPER_REGISTRY_PATH + "/" +path));
               RpcServerNodeInfo node = JsonUtil.jsonToObject(json, RpcServerNodeInfo.class);
               nodeList.add(node);
            }
        } catch (Exception e) {
            logger.error("Failed to get service nodes");
            nodeList.clear();
        }
        nodeSet.clear();
        nodeSet.addAll(nodeList);
        serverChannelManager.update(nodeSet);
        serverNodeRouter.update(nodeSet);
        logger.info("service nodes update completed");
    }

    public Channel getConnection(String serviceKey) {
        RpcServerNodeInfo node = serverNodeRouter.route(serviceKey);
        return serverChannelManager.getChannel(node);
    }
}
