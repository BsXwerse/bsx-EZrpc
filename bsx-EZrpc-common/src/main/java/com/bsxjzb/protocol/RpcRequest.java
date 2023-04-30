package com.bsxjzb.protocol;

import com.bsxjzb.constant.SysConstant;

import java.io.Serializable;

public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestId;
    private String className;
    private String version;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameterValues;

    public static RpcRequest BEAT = new RpcRequest(SysConstant.BEAT_ID);

    public RpcRequest(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameterValues() {
        return parameterValues;
    }

    public void setParameterValues(Object[] parameterValues) {
        this.parameterValues = parameterValues;
    }
}
