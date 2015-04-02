package com.xiaoqq.practise.threadmonitor.model;

/**
 * Created by rxiao on 4/2/15.
 */
public class ThreadInfo {
    private String threadName;
    private String threadId;
    private Thread.State threadState;
    private ThreadGroup threadGroup;
    private long enterTimeStamp;
    private long exitTimeStamp;
    private String threadClassName;
}
