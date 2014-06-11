package rabbit.share.concurrent;

import java.util.concurrent.*;

/**
 * Demo for Producer/Consumer implement by BlockingQueue
 */
public class BlockingDequeDemo {

    public static void main(String[] args) {
        //ExecutorService threadPool = Executors.newSingleThreadExecutor(); //WAIT
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        //Creating BlockingQueue of size 10
        BlockingDeque<Message> deque = new LinkedBlockingDeque<Message>(10);
        Producer producer = new Producer(deque);

        //starting producer to produce messages in deque
        threadPool.submit(producer);
        //new Thread(producer).start();

        for (int i=0; i<1; ++i) {
            Consumer consumer = new Consumer(deque, "Consumer"+i);
            //starting consumer to consume messages from deque
            threadPool.submit(consumer);
            //new Thread(consumer).start();
        }
        System.out.println("Producer and Consumer has been started");
        //threadPool.shutdown();//If we don't add this line, the main thread will not stop.
        //threadPool.shutdownNow();//If we add this line, threads in Threadpool will receive an InterruptedException
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

        private BlockingDeque<Message> deque;

        public Producer(BlockingDeque<Message> q){
            this.deque =q;
        }
        public void run() {
            //produce messages
            for(int i=0; i<100; i++){
                Message msg = new Message(""+i);
                try {
                    Thread.sleep(i);
                    if (i%2 == 0) {
                        deque.putFirst(msg);
                    } else {
                        deque.putLast(msg);
                    }
                    System.out.println("Produced "+msg.getMsg());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //adding exit message
            Message msg = new Message("exit");
            try {
                deque.putFirst(msg);
                System.out.println("Produced "+msg.getMsg());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    private static class Consumer implements Runnable{

        private BlockingDeque<Message> queue;
        private String name;
        public Consumer(BlockingDeque<Message> q, String name){
            this.queue=q;
            this.name = name;
        }

        public void run() {
            try{
                //consuming messages until exit message is received
                while(true){
                    Message msg = queue.takeLast();
                    Thread.sleep(10);
                    System.out.println("Consumer:(" + name +") Consumed "+msg.getMsg());
                    if (msg.getMsg() == "exit") {
                        System.out.println("Consumer:(" + name +") exit.");
                        break;
                    }
                }
            }catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
