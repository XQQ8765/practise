package com.xiaoqq.practise.threadsamplecode;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Demo for Producer/Consumer implement by BlockingQueue
 */
public class ProducerConsumerService {

    public static void main(String[] args) {
        //ExecutorService threadPool = Executors.newSingleThreadExecutor(); //WAIT
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        //Creating BlockingQueue of size 10
        BlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(10);
        Producer producer = new Producer(queue);

        //starting producer to produce messages in queue
        threadPool.submit(producer);

        for (int i=0; i<3; ++i) {
            Consumer consumer = new Consumer(queue, "Consumer"+i);
            //starting consumer to consume messages from queue
            threadPool.submit(consumer);
        }
        System.out.println("Producer and Consumer has been started");
        threadPool.shutdown();
    }
    private static class Message {
        private String msg;

        public Message(String str){
            this.msg=str;
        }

        public String getMsg() {
            return msg;
        }

    }

    private static class Producer implements Runnable {

        private BlockingQueue<Message> queue;

        public Producer(BlockingQueue<Message> q){
            this.queue=q;
        }
        public void run() {
            //produce messages
            for(int i=0; i<10; i++){
                Message msg = new Message(""+i);
                try {
                    Thread.sleep(i);
                    //System.out.println("Put "+msg.getMsg() + ", queue.size():" + queue.size());
                    queue.put(msg);
                    System.out.println("Produced "+msg.getMsg());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //adding exit message
            Message msg = new Message("exit");
            try {
                queue.put(msg);
                System.out.println("Produced "+msg.getMsg());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    private static class Consumer implements Runnable{

        private BlockingQueue<Message> queue;
        private String name;
        public Consumer(BlockingQueue<Message> q, String name){
            this.queue=q;
            this.name = name;
        }

        public void run() {
            try{
                //consuming messages until exit message is received
                while(true){
                    //System.out.println("Consumer:(" + name +") start to poll. queue.size():" + queue.size());
                    //Message msg = queue.poll(); //NullPointerException will occur
                    Message msg = queue.take();
                    Thread.sleep(10);
                    System.out.println("Consumer:(" + name +") Consumed "+ msg.getMsg());
                    if (msg.getMsg() == "exit") {
                        System.out.println("Consumer:(" + name +") exit.");
                        break;
                    }
                }
                System.out.println("Consumer:(" + name +") finish.");
            }catch(InterruptedException e) {
                System.out.println("Consumer:(" + name + ") throw a InterruptedException");
                e.printStackTrace();
            } catch (RuntimeException e) {
                System.out.println("Consumer:(" + name + ") throw a RuntimeException");
                e.printStackTrace();
            }
        }
    }

}
