package future;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Calculator {

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public Future<Integer> calculate(Integer i){
        return executor.submit(() -> {
           Thread.sleep(1000);
           return i * i;
        });
    }

    public static void main(String[] args) throws Exception{
        Calculator calculator = new Calculator();

        Future<Integer> f = calculator.calculate(100);

        while (!f.isDone()){
            System.out.println("wait...");
            Thread.sleep(300);
        }
        System.out.println(f.get());
    }
}
