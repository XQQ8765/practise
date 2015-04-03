package com.xiaoqq.practise.threadmonitor.model;

/**
 * Created by rxiao on 4/2/15.
 */
public class ThreadBean {
    private long threadId;
    private String threadName;

    public ThreadBean(long threadId, String threadName) {
        this.threadId = threadId;
        this.threadName = threadName;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
}
