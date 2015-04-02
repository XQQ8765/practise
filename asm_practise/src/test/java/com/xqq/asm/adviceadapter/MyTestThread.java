package com.xqq.asm.adviceadapter;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by rxiao on 4/1/15.
 */ /*
* See more at: http://www.java2novice.com/java_thread_examples/implementing_runnable/#sthash.s5d7fw08.dpuf
 */
public class MyTestThread extends Thread{
    public static int myCount = 0;
    public void run(){
        while(MyTestThread.myCount <= 10){
            try{
                Date date = new Date();
               int i = 2 + 3;
               int j = i + 3;
                Timestamp time = new Timestamp(date.getTime());
                System.out.println("Expl Thread: "+(++MyTestThread.myCount) + ", timestamp:" + time);
                Thread.sleep(100);
            } catch (InterruptedException iex) {
                System.out.println("Exception in thread: "+iex.getMessage());
            }
        }
    }
}
