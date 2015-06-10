package com.xiaoqq.practise.threadmonitor.relationship.model;

/**
 * Created by rxiao on 6/10/15.
 */
public enum EventType {
    BEFORE_WAIT, //(obj, class_name, method_name, timestamp)
    AFTER_WAIT,  //(obj, class_name, method_name, timestamp)
    BEFORE_NOTIFY, //(obj, class_name, method_name, timestamp)
    AFTER_NOTIFY, //(obj, class_name, method_name, timestamp)
    BEFORE_NOTIFY_ALL, //(obj, class_name, method_name, timestamp)
    AFTER_NOTIFY_ALL //(obj, class_name, method_name, timestamp)
}
