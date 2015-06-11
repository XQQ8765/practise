package com.xiaoqq.practise.threadmonitor.relationship.model;

/**
 * Created by rxiao on 6/10/15.
 */
public class CodePosition {
    private String className;
    private String methodName;
    private String methodDesc;

    public CodePosition() {

    }
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public void setMethodDesc(String methodDesc) {
        this.methodDesc = methodDesc;
    }
}
