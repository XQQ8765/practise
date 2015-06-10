package com.xiaoqq.practise.threadmonitor.relationship.model;

/**
 * Created by rxiao on 4/2/15.
 */
public class ThreadInfo {
    private String threadName;
    private Long threadId;

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }
}
