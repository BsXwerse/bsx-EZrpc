package route;

import com.bsxjzb.constant.SysConstant;
import com.bsxjzb.service.RpcServerNodeInfo;
import com.bsxjzb.service.RpcServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ServerNodeRouter {
    private static final Logger logger = LoggerFactory.getLogger(ServerNodeRouter.class);

    private final ConcurrentHashMap<String, List<RpcServerNodeInfo>> routeMap = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final AtomicInteger order = new AtomicInteger(0);

    public void update(CopyOnWriteArraySet<RpcServerNodeInfo> set) {
        routeMap.clear();
        for (RpcServerNodeInfo node : set) {
            for (RpcServiceInfo serviceInfo : node.getServiceList()) {
                String serviceKey = serviceInfo.getServiceName().concat(SysConstant.SERVICE_CONCAT_TOKEN)
                        .concat(serviceInfo.getVersion());
                List<RpcServerNodeInfo> nodes = routeMap.get(serviceKey);
                if (Objects.isNull(nodes)) {
                    nodes = new ArrayList<>();
                }
                nodes.add(node);
                routeMap.putIfAbsent(serviceKey, nodes);
            }
        }
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public RpcServerNodeInfo route(String serviceKey) {
        List<RpcServerNodeInfo> nodeList = routeMap.get(serviceKey);
        while (Objects.isNull(nodeList) || nodeList.size() == 0) {
            lock.lock();
            try {
                condition.await(SysConstant.WAITING_TIME, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("Waiting for available service is interrupted!", e);
            } finally {
                lock.unlock();
            }
            nodeList = routeMap.get(serviceKey);
        }
        int index = order.getAndAdd(1) % nodeList.size();
        return nodeList.get(index);
    }
}
