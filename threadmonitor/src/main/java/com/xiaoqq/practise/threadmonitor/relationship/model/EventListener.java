package com.xiaoqq.practise.threadmonitor.relationship.model;

/**
 * Created by rxiao on 6/10/15.
 */
public class EventListener {
    private static EventWriter eventWriter = new EchoEventWriter();

    public static void beforeWait(Object waitObj, CodePosition codePosition) {
        Event e = new Event(EventType.BEFORE_WAIT, waitObj, codePosition);
        eventWriter.writeEvent(e);
    }

    public static void afterWait(Object waitObj, CodePosition codePosition) {
        Event e = new Event(EventType.AFTER_WAIT, waitObj, codePosition);
        eventWriter.writeEvent(e);
    }

    public static void beforeNotify(Object waitObj, CodePosition codePosition) {
        Event e = new Event(EventType.BEFORE_NOTIFY, waitObj, codePosition);
        eventWriter.writeEvent(e);
    }

    public static void beforeNotifyAll(Object waitObj, CodePosition codePosition) {
        Event e = new Event(EventType.BEFORE_NOTIFY_ALL, waitObj, codePosition);
        eventWriter.writeEvent(e);
    }

    public static void afterNotify(Object waitObj, CodePosition codePosition) {
        Event e = new Event(EventType.AFTER_NOTIFY, waitObj, codePosition);
        eventWriter.writeEvent(e);
    }

    public static void afterNotifyAll(Object waitObj, CodePosition codePosition) {
        Event e = new Event(EventType.AFTER_NOTIFY_ALL, waitObj, codePosition);
        eventWriter.writeEvent(e);
    }

    public static void beforeMonitorEnter(Object waitObj, CodePosition codePosition) {
        Event e = new Event(EventType.BEFORE_MONITOR_ENTER, waitObj, codePosition);
        eventWriter.writeEvent(e);
    }

    public static void afterMonitorEnter(Object waitObj, CodePosition codePosition) {
        Event e = new Event(EventType.AFTER_MONITOR_ENTER, waitObj, codePosition);
        eventWriter.writeEvent(e);
    }

    public static void monitorExit(Object waitObj, CodePosition codePosition) {
        Event e = new Event(EventType.MONITOR_EXIT, waitObj, codePosition);
        eventWriter.writeEvent(e);
    }
}
