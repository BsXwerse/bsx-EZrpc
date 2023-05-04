package com.bsxjzb.handler;

import com.bsxjzb.protocol.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class RpcFuture implements Future<Object> {
    private static final Logger logger = LoggerFactory.getLogger(RpcFuture.class);

    private Sync sync = new Sync();
    private Object result;
    private RpcRequest request;

    public RpcFuture(RpcRequest request) {
        this.request = request;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public Object get() {
        sync.acquire(0);
        if (!sync.isDone() || result == null) {
            logger.error("Error occurred while getting result, request id: {}\nclass name: {}\nmethod name: {}",
                    request.getRequestId(),
                    request.getClassName(),
                    request.getMethodName());
            return null;
        }
        return result;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Thread.sleep(unit.toMillis(timeout));
        if (sync.isDone() && result != null) return result;
        else {
            logger.warn("Timeout exception, request id: {}\nclass name: {}\nmethod name: {}",
                    request.getRequestId(),
                    request.getClassName(),
                    request.getMethodName());
            return null;
        }
    }

    public void done(Object obj) {
        result = obj;
        sync.release(0);
    }

    private static class Sync extends AbstractQueuedSynchronizer {
        @Override
        public boolean tryAcquire(int arg) {
            return getState() == 1;
        }

        @Override
        public boolean tryRelease(int arg) {
            setState(1);
            return true;
        }

        public boolean isDone() {
            return getState() == 1;
        }
    }
}
