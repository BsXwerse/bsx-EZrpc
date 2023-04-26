package com.bsxjzb.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import java.util.Map;

public class NettyThread implements Runnable{
    private ChannelInitializer<SocketChannel> channelInitializer;
    private Map<String, Object> serviceMap;

    public NettyThread(ChannelInitializer<SocketChannel> channelInitializer, Map<String, Object> serviceMap) {
        this.channelInitializer = channelInitializer;
        this.serviceMap = serviceMap;
    }

    @Override
    public void run() {

    }
}
