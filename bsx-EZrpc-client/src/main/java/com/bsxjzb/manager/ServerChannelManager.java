package com.bsxjzb.manager;

import com.bsxjzb.service.RpcServerNodeInfo;
import com.bsxjzb.handler.NettyClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class ServerChannelManager {
    private static final Logger logger = LoggerFactory.getLogger(ServerChannelManager.class);

    private ConcurrentHashMap<RpcServerNodeInfo, Channel> cacheMap = new ConcurrentHashMap<>();
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

    public void update(CopyOnWriteArraySet<RpcServerNodeInfo> set) {
            List<RpcServerNodeInfo> list = new ArrayList<>();
            for (RpcServerNodeInfo node : cacheMap.keySet()) {
                if (!set.contains(node)) list.add(node);
            }
            for (RpcServerNodeInfo node : list) {
                cacheMap.get(node).close();
                cacheMap.remove(node);
            }
    }

    public Channel getChannel(RpcServerNodeInfo node) {
        Channel channel = cacheMap.get(node);
        if (Objects.isNull(channel) || !channel.isActive()) {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new NettyClientInitializer())
                    .option(ChannelOption.SO_KEEPALIVE, true);
            try {
                channel = bootstrap.connect(node.getHost(), node.getPort()).sync().channel();
            } catch (InterruptedException e) {
                logger.error("Connection to server failed: host-{} port-{} error:\n{}",
                        node.getHost(), node.getPort(), e);
                return null;
            }
            cacheMap.put(node, channel);
        }
        return channel;
    }
}
