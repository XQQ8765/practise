package com.xiaoqq.practise.threadsamplecode;

import com.xiaoqq.practise.threadmonitor.MonitorUtil;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by rxiao on 4/1/15.
 */ /*
* See more at: http://www.java2novice.com/java_thread_examples/implementing_runnable/#sthash.s5d7fw08.dpuf
 */
public class MySmpThread extends Thread{
    public static int myCount = 0;

    public MySmpThread(String name) {
        super(name);
    }

    public void run(){
        System.out.print("MySmpThread.run() start to execute");
        while(MySmpThread.myCount <= 10){
            try{
                //String threadName = Thread.currentThread().getName();
                String threadName = MonitorUtil.getCurrentThreadName();
                System.out.println("Current Thread Name:" + threadName);
                Date date = new Date();
               int i = 2 + 3;
               int j = i + 3;
                Timestamp time = new Timestamp(date.getTime());
                System.out.println("Expl Thread: "+(++MySmpThread.myCount) + ", timestamp:" + time);

                StackTraceElement[]	statckTraces = getStackTrace();
                if (statckTraces != null) {
                    for (StackTraceElement se: statckTraces) {
                        System.out.println("trace -> " + se.toString());
                    }
                }

                Thread.sleep(100);
            } catch (InterruptedException iex) {
                System.out.println("Exception in thread: "+iex.getMessage());
            }
        }

    }

    private void test() {
        System.out.println("test");
    }
}
