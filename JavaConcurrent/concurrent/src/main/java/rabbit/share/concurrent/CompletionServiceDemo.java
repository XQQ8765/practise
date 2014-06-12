package rabbit.share.concurrent;

/**
 http://blog.csdn.net/wxwzy738/article/details/8497853
 */
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class CompletionServiceDemo {
    static int LOOP_COUNT = 1000;
    public static void main(String[] args) throws Exception {
        CompletionServiceDemo t = new CompletionServiceDemo();
        t.count1();
        t.count2();
    }
    boolean isLongTask(Integer i) {
        return i < LOOP_COUNT/2;
    }
    /**
     * Adding the Future to BlockingQueue, we will take it from the queue sequencely.
     * @throws Exception
     */
    public void count1() throws Exception{
        long t0 = System.currentTimeMillis();
        ExecutorService exec = Executors.newCachedThreadPool();
        BlockingQueue<Future<Integer>> queue = new LinkedBlockingQueue<Future<Integer>>();
        for(int i=0; i<LOOP_COUNT; i++){
            Future<Integer> future = exec.submit(getTask(isLongTask(i)));
            queue.add(future);
        }
        int sum = 0;
        int queueSize = queue.size();
        for(int i=0; i<queueSize; i++){
            sum += queue.take().get();
        }
        long spentTime = System.currentTimeMillis() - t0;
        System.out.println("count1(): The sum value is：" + sum + ", spent " + spentTime +"ms.");
        exec.shutdown();
    }
    //Using CompletionService to save of the result of Executor
    public void count2() throws InterruptedException, ExecutionException{
        long t0 = System.currentTimeMillis();
        ExecutorService exec = Executors.newCachedThreadPool();
        CompletionService<Integer> execcomp = new ExecutorCompletionService<Integer>(exec);
        for(int i=0; i<LOOP_COUNT; i++){
            execcomp.submit(getTask(isLongTask(i)));
        }
        int sum = 0;
        for(int i=0; i<LOOP_COUNT; i++){
            //take an future from CompletionService. If not exist, then will wait until one finish.
            Future<Integer> future = execcomp.take();
            sum += future.get();
        }
        long spentTime = System.currentTimeMillis() - t0;
        System.out.println("count2(): The sum value is：" + sum + ", spent " + spentTime +"ms.");
        exec.shutdown();
    }



    public Callable<Integer> getTask(final boolean isLongTask){
        final Random rand = new Random();
        Callable<Integer> task = new Callable<Integer>(){

            public Integer call() throws Exception {
                int intVar = rand.nextInt(10);
                Thread.sleep(1000L);
                if (isLongTask) {
                    Thread.sleep(1000L);
                }
                //System.out.print(intVar+"\t");
                return intVar;
            }
        };
        return task;
    }
}
