package com.xiaoqq.practise.threadsamplecode;

/**
 * Code is from: http://www.programcreek.com/2009/02/notify-and-wait-example/
 */
public class WaitExample1 {
    public static void main(String[] args){
        ThreadB b = new ThreadB();
        b.start();

        synchronized(b){
            try{
                System.out.println("Waiting for b to complete...");
                b.wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }

            System.out.println("Total is: " + b.total);
        }
    }

    static class ThreadB extends Thread{
        int total;
        @Override
        public void run(){
            synchronized(this){
                for(int i=0; i<100 ; i++){
                    total += i;
                }
                notify();
            }
        }
    }
}
