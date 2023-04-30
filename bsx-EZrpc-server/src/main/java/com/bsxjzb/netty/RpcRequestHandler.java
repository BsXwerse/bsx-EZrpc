package com.bsxjzb.netty;

import com.bsxjzb.constant.SysConstant;
import com.bsxjzb.protocol.RpcRequest;
import com.bsxjzb.protocol.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import net.sf.cglib.reflect.FastClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;

public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);

    private final Map<String, Object> serviceMap;

    public RpcRequestHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) {
        if (msg.getRequestId().equals(SysConstant.BEAT_ID)) {
            logger.info("Received the heartbeat from : " + ctx.channel().id());
            return;
        }

        NettyThread.serviceThreadPool.execute(() -> {
            RpcResponse response = new RpcResponse();
            response.setRequestId(msg.getRequestId());
            logger.info("Received request id : " + msg.getRequestId());
            try {
                Object res = handle(msg);
                response.setResult(res);
            } catch (Throwable e) {
                response.setError(e.toString());
                logger.error("Request processing failed, request id : " + msg.getRequestId() + "\n" + e);
            }
            ctx.writeAndFlush(response).addListener((ChannelFutureListener) future ->
                    logger.info("Send response for request " + msg.getRequestId()));
        });
    }

    private Object handle(RpcRequest request) throws InvocationTargetException {
        String className = request.getClassName();
        String version = request.getVersion();
        String serviceKey = className.concat(SysConstant.SERVICE_CONCAT_TOKEN).concat(version);
        Object serviceBean = serviceMap.get(serviceKey);
        if (Objects.isNull(serviceBean)) {
            logger.error("Can not find service implement with interface name: {} and version: {}",
                    className, version);
            return null;
        }
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameterValues = request.getParameterValues();
        FastClass fastClass = FastClass.create(serviceBean.getClass());
        return fastClass.invoke(methodName, parameterTypes, serviceBean, parameterValues);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            logger.warn("Channel idle in last {} seconds, close it", SysConstant.BEAT_TIME_OUT);
            ctx.close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("Server caught exception: " + cause.getMessage());
        ctx.close();
    }
}
