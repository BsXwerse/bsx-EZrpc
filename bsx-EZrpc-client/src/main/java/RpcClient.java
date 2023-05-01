import manager.ServiceNodeManager;

public class RpcClient {
    private ServiceNodeManager serviceNodeManager;

    public RpcClient(String registryAddress) {
        serviceNodeManager = new ServiceNodeManager(registryAddress);
    }
}
