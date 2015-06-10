package com.xiaoqq.practise.threadmonitor.relationship.model;

import java.sql.Timestamp;

/**
 * Created by rxiao on 6/10/15.
 */
public class Event {
    private EventType eventType;
    private Object obj;
    private CodePosition codePosition;
    private Timestamp timestamp;
    private ThreadInfo threadInfo;
    private String eventId;
    public Event(EventType eventType, Object obj, CodePosition codePosition) {
        this.eventType = eventType;
        this.obj = obj;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.codePosition = codePosition;

        threadInfo = new ThreadInfo();
        threadInfo.setThreadId(Thread.currentThread().getId());
        threadInfo.setThreadName(Thread.currentThread().getName());

        eventId = eventType + "_" + System.identityHashCode(obj);
    }

    @Override
    public String toString() {
        //timestamp | eventType | threadId | threadName | className | methodName | obj | obj_hashId |
        String sperator = " | ";
        StringBuilder sb = new StringBuilder();
        sb.append(timestamp);
        sb.append(sperator);

        sb.append(eventType);
        sb.append(sperator);

        sb.append(threadInfo.getThreadId());
        sb.append(sperator);
        sb.append(threadInfo.getThreadName());
        sb.append(sperator);

        sb.append(codePosition.getClassName());
        sb.append(sperator);
        sb.append(codePosition.getMethodName());
        sb.append(sperator);

        sb.append(obj);
        sb.append(sperator);
        sb.append(System.identityHashCode(obj));//Object hashcode
        sb.append(sperator);

        return sb.toString();
    }

    public String getEventId() {
        return eventId;
    }

    public EventType getEventType() {

        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public CodePosition getCodePosition() {
        return codePosition;
    }

    public void setCodePosition(CodePosition codePosition) {
        this.codePosition = codePosition;
    }
}
