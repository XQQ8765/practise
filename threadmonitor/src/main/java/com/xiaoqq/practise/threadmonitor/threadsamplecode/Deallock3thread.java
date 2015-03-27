package com.xiaoqq.practise.threadmonitor.threadsamplecode;

/**
 * Created by rxiao on 3/27/15.
 */
public class Deallock3thread extends Thread{
    static Object a = new Object(); // a monitor object
    static Object b = new Object(); // b monitor object
    static Object c = new Object(); // c monitor object
    String name;
    public Deallock3thread(String name){
        this.name = name;
    }
    public void run() {
        if(name.equals("d1") ){
            synchronized(a) {
                System.out.println("d1 acquired a");
                try { Thread.sleep(1000); }
                catch (InterruptedException e) {}
                synchronized(b) {
                    System.out.println("d1 acquired b");
                }
            }
        }
        else if(name.equals("d2") ){
            synchronized(b) {
                System.out.println("d2 acquired b");
                try { Thread.sleep(1000); }
                catch (InterruptedException e) {}
                synchronized(c) {
                    System.out.println("d2 acquired c");
                }
            }
        }
        else if(name.equals("d3") ){
            synchronized(c) {
                System.out.println("d3 acquired c");
                try { Thread.sleep(1000); }
                catch (InterruptedException e) {}
                synchronized(a) {
                    System.out.println("d3 acquired a");
                }
            }
        }
    }

    public static void main(String[] args) {
        Deallock3thread d1 = new Deallock3thread("d1");
        Deallock3thread d2 = new Deallock3thread("d2");
        Deallock3thread d3 = new Deallock3thread("d3");
        d1.start();
        d2.start();
        d3.start();
    }
}
