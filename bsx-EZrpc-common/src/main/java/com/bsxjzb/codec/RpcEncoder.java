package com.bsxjzb.codec;

import com.bsxjzb.util.KryoPoolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcEncoder extends MessageToByteEncoder {
    private static final Logger logger = LoggerFactory.getLogger(RpcEncoder.class);

    private Class<?> targetClass;

    public RpcEncoder(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (targetClass.isInstance(msg)) {
            try {
                byte[] data = KryoPoolUtil.serialize(msg);
                out.writeInt(data.length);
                out.writeBytes(data);
            } catch (Exception e) {
                logger.error(targetClass.getName() + " serialization failed, error: " + e);
            }
        }
    }
}
