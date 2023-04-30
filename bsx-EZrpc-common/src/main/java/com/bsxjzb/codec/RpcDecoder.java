package com.bsxjzb.codec;

import com.bsxjzb.util.KryoPoolUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(RpcDecoder.class);

    private Class<?> targetClass;

    public RpcDecoder(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataSize = in.readInt();
        if (in.readableBytes() < dataSize) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataSize];
        in.readBytes(data);
        try {
            Object obj = KryoPoolUtil.deserialize(data, targetClass);
            out.add(obj);
        } catch (Exception e) {
            logger.error(targetClass.getName() + " deserialization failed, error: " + e);
        }

    }
}
