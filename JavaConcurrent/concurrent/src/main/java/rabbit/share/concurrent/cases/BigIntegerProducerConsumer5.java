package rabbit.share.concurrent.cases;

import java.math.BigInteger;
import java.util.concurrent.*;

public class BigIntegerProducerConsumer5 {
    public static void main(String[] args) {
        final int CAPACITY = 10;
        BlockingQueue<BigInteger> numbers = new ArrayBlockingQueue<BigInteger>(CAPACITY);
        final int THREAD_COUNT = 2;
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        //Start the producer
        BigIntegerProducer producer = new BigIntegerProducer(numbers);
        FutureTask<Boolean> producerFuture = new FutureTask(producer);
        executor.submit(producerFuture);

        //Start the consumer
        BitIntegerConsumer consumer = new BitIntegerConsumer(numbers);
        FutureTask<Boolean> consumerFuture = new FutureTask(consumer);
        executor.submit(consumerFuture);
        try {
            Thread.sleep(1000L);
            consumerFuture.cancel(true);
            Thread.sleep(1000L);
            producerFuture.cancel(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }

    private static class BigIntegerProducer implements Callable<Boolean> {
        private final BlockingQueue<BigInteger> numbers;

        BigIntegerProducer(BlockingQueue<BigInteger> numbers) {
            this.numbers = numbers;
        }

        public Boolean call() throws Exception {
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
            return true;
        }
    }

    private static class BitIntegerConsumer implements Callable<Boolean> {
        private final BlockingQueue<BigInteger> numbers;

        BitIntegerConsumer(BlockingQueue<BigInteger> numbers) {
            this.numbers = numbers;
        }

        public Boolean call() throws Exception {
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
            return true;
        }
    }

}








