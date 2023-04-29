package com.bsxjzb.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(NettyThread.class);

    private ChannelInitializer<SocketChannel> channelInitializer;
    private Channel serverChannel;
    private String serverAddress;

    public NettyThread(String serverAddress, ChannelInitializer<SocketChannel> channelInitializer) {
        this.channelInitializer = channelInitializer;
        this.serverAddress = serverAddress;
    }

    @Override
    public void run() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            String[] split = serverAddress.split(":");
            int port = Integer.parseInt(split[1]);
            serverChannel = serverBootstrap.bind(split[0], port).sync().channel();
            logger.info("server runs on : {}", port);
            serverChannel.closeFuture().sync();
        } catch (Exception e) {
            logger.error("server stops with error");
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
            logger.info("server stopped");
        }
    }

    public void closeServer() {
        if (serverChannel != null) {
            serverChannel.close();
        }
    }
}
