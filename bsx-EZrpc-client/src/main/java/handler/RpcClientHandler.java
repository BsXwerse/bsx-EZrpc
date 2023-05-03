package handler;

import com.bsxjzb.protocol.RpcRequest;
import com.bsxjzb.protocol.RpcResponse;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);

    private Channel channel;
    private ConcurrentHashMap<String, RpcFuture> pendingTask = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        RpcFuture rpcFuture = pendingTask.get(msg.getRequestId());
        if (Objects.isNull(rpcFuture)) {
            logger.error("Unknown call id: {}", msg.getRequestId());
            return;
        }
        if (msg.isError()) {
            logger.error("Rpc call error, id:{}\nerror: \n{}", msg
                    .getRequestId(), msg.getError());
            rpcFuture.done(null);
        }
        rpcFuture.done(msg.getResult());
        pendingTask.remove(msg.getRequestId());
    }

    public RpcFuture sendRequest(RpcRequest request) {
        RpcFuture rpcFuture = new RpcFuture(request);
        pendingTask.put(request.getRequestId(), rpcFuture);
        channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                logger.error("Failed to send request, id: {}\nclass name: {}\nmethod name: {}",
                        request.getRequestId(),
                        request.getClassName(),
                        request.getMethodName());
                pendingTask.remove(request.getRequestId());
                rpcFuture.done(null);
            }
        });
        return rpcFuture;
    }
}
