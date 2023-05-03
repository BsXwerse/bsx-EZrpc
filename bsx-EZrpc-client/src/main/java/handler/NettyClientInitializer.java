package handler;

import com.bsxjzb.codec.RpcDecoder;
import com.bsxjzb.codec.RpcEncoder;
import com.bsxjzb.protocol.RpcRequest;
import com.bsxjzb.protocol.RpcResponse;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(65536, 0,
                4, 0, 0));
        pipeline.addLast(new RpcEncoder(RpcRequest.class));
        pipeline.addLast(new RpcDecoder(RpcResponse.class));
        pipeline.addLast(new RpcClientHandler());
    }
}
