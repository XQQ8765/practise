package com.xiaoqq.practise.threadsamplecode.wait;

/**
 * The code example is from: http://www.journaldev.com/1037/java-thread-wait-notify-and-notifyall-example
 */
public class WaitNotifyTest {
 
    public static void main(String[] args) throws InterruptedException {
       /* Object obj = new Object();
        obj.wait(10L);  */
        Message msg = new Message("process it");
        Waiter waiter = new Waiter(msg);
        new Thread(waiter,"waiter").start();
         
        Waiter waiter1 = new Waiter(msg);
        new Thread(waiter1, "waiter1").start();
         
        Notifier notifier = new Notifier(msg);
        new Thread(notifier, "notifier").start();
        System.out.println("All the threads are started");
    }
 
}