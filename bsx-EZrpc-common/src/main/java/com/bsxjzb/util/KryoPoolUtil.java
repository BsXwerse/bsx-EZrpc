package com.bsxjzb.util;

import com.bsxjzb.protocol.RpcRequest;
import com.bsxjzb.protocol.RpcResponse;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.util.Pool;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoPoolUtil {
    private static Pool<Kryo> kryoPool = new Pool<Kryo>(true, false, 8) {
        @Override
        protected Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setReferences(false);
            kryo.setRegistrationRequired(false);
            kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
            kryo.register(RpcRequest.class);
            kryo.register(RpcResponse.class);
            return kryo;
        }
    };

    public static <T> byte[] serialize(T obj) {
        Kryo kryo = kryoPool.obtain();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        try {
            kryo.writeObject(output, obj);
            output.flush();
            return byteArrayOutputStream.toByteArray();
        } finally {
            output.close();
            kryoPool.free(kryo);
        }
    }

    public static <T> T deserialize(byte[] data, Class<T> clazz) {
        Kryo kryo = kryoPool.obtain();
        Input input = new Input(new ByteArrayInputStream(data));
        try {
            return kryo.readObject(input, clazz);
        } finally {
            input.close();
            kryoPool.free(kryo);
        }
    }
}
