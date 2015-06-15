package com.xiaoqq.practise.threadmonitor.relationship.model;

public enum EventType {
    BEFORE_WAIT("beforeWait"), //(obj, class_name, method_name, timestamp)
    AFTER_WAIT("afterWait"),  //(obj, class_name, method_name, timestamp)
    BEFORE_NOTIFY("beforeNotify"), //(obj, class_name, method_name, timestamp)
    AFTER_NOTIFY("afterNotify"), //(obj, class_name, method_name, timestamp)
    BEFORE_NOTIFY_ALL("beforeNotifyAll"), //(obj, class_name, method_name, timestamp)
    AFTER_NOTIFY_ALL("afterNotifyAll"); //(obj, class_name, method_name, timestamp)

    private String methodName;
    EventType(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return this.methodName;
    }
}
