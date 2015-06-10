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
}
