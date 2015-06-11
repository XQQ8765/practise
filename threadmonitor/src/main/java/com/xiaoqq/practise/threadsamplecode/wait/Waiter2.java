package com.xiaoqq.practise.threadsamplecode.wait;

import com.xiaoqq.practise.threadmonitor.relationship.model.CodePosition;
import com.xiaoqq.practise.threadmonitor.relationship.model.EventListener;
import java.io.PrintStream;

public class Waiter2 implements Runnable
{
    private Message msg;

    public Waiter2(Message m)
    {
        super();
        this.msg = m;
    }

    public void run()
    {
        String name = Thread.currentThread().getName();
        synchronized (this.msg)
        {
            try
            {
                System.out.println(name + " waiting to get notified at time:" + System.currentTimeMillis());
                CodePosition localCodePosition = new CodePosition();
                localCodePosition.setClassName("com/xiaoqq/practise/threadsamplecode/wait/Waiter");
                localCodePosition.setMethodName("run");
                localCodePosition.setMethodDesc("()V");
                Waiter2 localWaiter = this;
                EventListener.beforeWait(localWaiter, localCodePosition);
                this.msg.wait();

            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            System.out.println(name + " waiter thread got notified at time:" + System.currentTimeMillis());

            System.out.println(name + " processed: " + this.msg.getMsg());
        }
    }
}
