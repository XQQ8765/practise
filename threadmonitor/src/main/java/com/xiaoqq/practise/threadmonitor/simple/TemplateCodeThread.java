package com.xiaoqq.practise.threadmonitor.simple;

import com.xiaoqq.practise.threadmonitor.model.ThreadBean;

/**
 * Created by rxiao on 4/2/15.
 */
public class TemplateCodeThread extends Thread{
     private ThreadBean xxx_parent_threadBean;
    private static int myCount;

    public TemplateCodeThread() {
        recordParentThread();
    }

    private void recordParentThread() {
        long threadId = Thread.currentThread().getId();
        String threadName = Thread.currentThread().getName();
        xxx_parent_threadBean = new ThreadBean(threadId, threadName);
    }

    public void run() {
        //System.out.println("$$$Parent Thread Name:" + xxx_parent_threadBean.getThreadName());
        printThreadRelationship();
        while(TemplateCodeThread.myCount <= 10){
            myCount++;
        }
    }

    private void printThreadRelationship() {
        System.out.println("$$$xxx_parent_threadBean="+xxx_parent_threadBean);
        String parentThreadName =  xxx_parent_threadBean.getThreadName();
        String currentThreadName = Thread.currentThread().getName();
        StringBuilder sb = new StringBuilder();
        sb.append("$$$: Parent Thread Name:");
        sb.append(parentThreadName);
        sb.append(", Current Thread Name:");
        sb.append(currentThreadName);
        System.out.println(sb.toString());
    }
}
