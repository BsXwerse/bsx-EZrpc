package com.bsxjzb.netty;

import com.bsxjzb.codec.RpcDecoder;
import com.bsxjzb.codec.RpcEncoder;
import com.bsxjzb.constant.SysConstant;
import com.bsxjzb.protocol.RpcRequest;
import com.bsxjzb.protocol.RpcResponse;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NettyInitializer extends ChannelInitializer<SocketChannel> {
    private Map<String, Object> serviceMap;

    public NettyInitializer(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new IdleStateHandler(0, 0,
                SysConstant.BEAT_TIME_OUT, TimeUnit.SECONDS));
        pipeline.addLast(new LengthFieldBasedFrameDecoder(65536, 0,
                4, 0, 0));
        pipeline.addLast(new RpcDecoder(RpcRequest.class));
        pipeline.addLast(new RpcEncoder(RpcResponse.class));
        pipeline.addLast(new RpcRequestHandler(serviceMap));
    }
}
