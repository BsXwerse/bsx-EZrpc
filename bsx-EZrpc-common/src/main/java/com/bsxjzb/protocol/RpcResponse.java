package com.bsxjzb.protocol;

import java.io.Serializable;

public class RpcResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestId;
    private String error;
    private Object result;

    public boolean isError() {
        return error != null;
    }

    public String getError() {
        return error;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public void setError(String error) {
        this.error = error;
    }
}
