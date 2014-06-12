package rabbit.share.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CompletionServiceExample {

    private static class LongRunningTask implements Callable<String> {

        public String call() {
            // do stuff and return some String
            try {
                Thread.sleep(Math.abs(new Random().nextLong() % 5000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return Thread.currentThread().getName();
        }
    }

    // dummy helper to create a List of Callables return a String
    public static List<Callable<String>> createCallableList() {
        List<Callable<String>> callables = new ArrayList<Callable<String>>();
        for (int i = 0; i < 10; i++) {
            callables.add(new LongRunningTask());
        }
        return callables;
    }

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        CompletionService<String> taskCompletionService = new ExecutorCompletionService<String>(
                executorService);

        try {
            List<Callable<String>> callables = createCallableList();
            for (Callable<String> callable : callables) {
                taskCompletionService.submit(callable);
            }
            for (int i = 0; i < callables.size(); i++) {
                Future<String> result = taskCompletionService.take();
                System.out.println(result.get());
            }
        } catch (InterruptedException e) {
            // no real error handling. Don't do this in production!
            e.printStackTrace();
        } catch (ExecutionException e) {
            // no real error handling. Don't do this in production!
            e.printStackTrace();
        }
        executorService.shutdown();
    }
}