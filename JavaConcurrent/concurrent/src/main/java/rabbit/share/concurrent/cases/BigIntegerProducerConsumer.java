package rabbit.share.concurrent.cases;

import java.math.BigInteger;
import java.util.concurrent.*;

public class BigIntegerProducerConsumer {
    public static void main(String[] args) {
        final int CAPACITY = 10;
        BlockingQueue<BigInteger> numbers = new ArrayBlockingQueue<BigInteger>(CAPACITY);
        final int THREAD_COUNT = 2;
        Executor executor = Executors.newFixedThreadPool(THREAD_COUNT);

        //Start the producer
        BigIntegerProducer producer = new BigIntegerProducer(numbers);
        executor.execute(producer);

        //Start the consumer
        BitIntegerConsumer consumer = new BitIntegerConsumer(numbers);
        executor.execute(consumer);

        try {
            Thread.sleep(1000L);
            consumer.cancel();
            Thread.sleep(1000L);
            producer.cancel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class BigIntegerProducer implements Runnable {
        private final BlockingQueue<BigInteger> numbers;
        private volatile boolean cancelled = false;

        BigIntegerProducer(BlockingQueue<BigInteger> numbers) {
            this.numbers = numbers;
        }

        public void run() {
            try {
                BigInteger p = BigInteger.ONE;
                while (!cancelled) {
                    p = p.add(BigInteger.ONE);
                    System.out.println("BigIntegerProducer producer " + p + ",numbers.size():" + numbers.size());
                    numbers.put(p.nextProbablePrime());
                }
            } catch (InterruptedException e) { }
            finally {
                System.out.println("BigIntegerProducer exit.");
            }
        }

        public void cancel() { cancelled = true;  }
    }

    private static class BitIntegerConsumer implements Runnable {
        private final BlockingQueue<BigInteger> numbers;
        private volatile boolean cancelled = false;

        BitIntegerConsumer(BlockingQueue<BigInteger> numbers) {
            this.numbers = numbers;
        }

        public void run() {
            try {
                BigInteger p = BigInteger.ONE;
                while (!cancelled) {
                    Thread.sleep(1000L);
                    BigInteger v = numbers.take();
                    System.out.println("BitIntegerConsumer consume " + v + ",numbers.size():" + numbers.size());
                }
            } catch (InterruptedException e) { }
            finally {
                System.out.println("BitIntegerConsumer exit.");
            }
        }

        public void cancel() { cancelled = true;  }
    }

}








