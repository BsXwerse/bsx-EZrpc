package com.bsxjzb.netty;

import com.bsxjzb.constant.SysConstant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class NettyThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(NettyThread.class);

    private final ChannelInitializer<SocketChannel> channelInitializer;
    private Channel serverChannel;
    private final String serverAddress;
    static ThreadPoolExecutor serviceThreadPool = new ThreadPoolExecutor(
            SysConstant.AVAILABLE_PROCESSORS + 1,
            SysConstant.AVAILABLE_PROCESSORS * 3,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(1000),
            new ThreadFactory() {
                private final AtomicInteger index = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "RpcServer-thread-" + index.incrementAndGet());
                }
            },
            new ThreadPoolExecutor.AbortPolicy()
    );

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
            logger.info("server runs on : {}:{}",split[0], port);
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
        serviceThreadPool.shutdownNow();
    }
}
