package manager;

import com.bsxjzb.constant.SysConstant;
import com.bsxjzb.service.RpcServerNodeInfo;
import com.bsxjzb.util.CuratorClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceNodeManager {
    private CuratorClient curatorClient;
    Set<RpcServerNodeInfo> nodeSet = new HashSet<>();

    public ServiceNodeManager(String registryAddress) {
        curatorClient = new CuratorClient(registryAddress);
    }

    private void updateServiceNode() {
        try {
            List<String> nodePathList = curatorClient.getChildren(SysConstant.ZOOKEEPER_REGISTRY_PATH);
            List<RpcServerNodeInfo> nodeList = new ArrayList<>();
            for (String path : nodePathList) {
               String json = new String(curatorClient.getData(path));

            }
        } catch (Exception e) {


        }
    }
}
