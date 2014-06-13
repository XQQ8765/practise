package rabbit.share.concurrent.cases;

import java.math.BigInteger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BigIntegerProducerConsumer3 {
    public static void main(String[] args) {
        final int CAPACITY = 10;
        BlockingQueue<BigInteger> numbers = new ArrayBlockingQueue<BigInteger>(CAPACITY);

        //Start the producer
        BigIntegerProducer producer = new BigIntegerProducer(numbers);
        producer.start();

        //Start the consumer
        BitIntegerConsumer consumer = new BitIntegerConsumer(numbers);
        consumer.start();

        try {
            Thread.sleep(1000L);
            consumer.cancel();
            Thread.sleep(1000L);
            producer.cancel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class BigIntegerProducer extends Thread {
        private final BlockingQueue<BigInteger> numbers;
        private volatile boolean cancelled = false;

        BigIntegerProducer(BlockingQueue<BigInteger> numbers) {
            this.numbers = numbers;
        }

        public void run() {
            try {
                BigInteger p = BigInteger.ONE;
                while (!Thread.currentThread().isInterrupted()) {
                    p = p.add(BigInteger.ONE);
                    System.out.println("BigIntegerProducer producer " + p + ",numbers.size():" + numbers.size());
                    numbers.put(p);
                }
            } catch (InterruptedException e) { }
            finally {
                System.out.println("BigIntegerProducer exit.");
            }
        }

        public void cancel() { interrupt();  }
    }

    private static class BitIntegerConsumer extends Thread {
        private final BlockingQueue<BigInteger> numbers;
        private volatile boolean cancelled = false;

        BitIntegerConsumer(BlockingQueue<BigInteger> numbers) {
            this.numbers = numbers;
        }

        public void run() {
            try {
                BigInteger p = BigInteger.ONE;
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(1000L);
                    BigInteger v = numbers.take();
                    System.out.println("BitIntegerConsumer consume " + v + ",numbers.size():" + numbers.size());
                }
            } catch (InterruptedException e) { }
            finally {
                System.out.println("BitIntegerConsumer exit.");
            }
        }

        public void cancel() { interrupt();  }
    }

}








