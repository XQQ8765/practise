package com.xiaoqq.practise.threadsamplecode;

import com.xiaoqq.practise.threadmonitor.util.MonitorUtil;

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
        //System.out.print("MySmpThread.run() start to execute");
        while(MySmpThread.myCount <= 10){
            try{
                System.out.println("Expl Thread: "+(++MySmpThread.myCount));
                /*
                StackTraceElement[]	statckTraces = getStackTrace();
                if (statckTraces != null) {
                    for (StackTraceElement se: statckTraces) {
                        System.out.println("trace -> " + se.toString());
                    }
                }*/

                Thread.sleep(100);
            } catch (InterruptedException iex) {
                System.out.println("Exception in thread: "+iex.getMessage());
            }
        }

    }
}
