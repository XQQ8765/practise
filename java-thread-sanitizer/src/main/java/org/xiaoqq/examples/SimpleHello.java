package org.xiaoqq.examples;

/**
 * Created by rxiao on 7/2/15.
 */
public class SimpleHello {
    private static int count = 0;
    private class IncThread extends Thread {

        @Override
        public void run() {
            System.out.println("count is: " + count);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
            count++;
        }
    }

    public void race() {
        Thread thr1 = new IncThread();
        Thread thr2 = new IncThread();
        thr1.start();
        thr2.start();
    }

    public static void main(String[] args) {
        SimpleHello hello = new SimpleHello();
        hello.race();
    }
}
