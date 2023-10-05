import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        String[] texts = new String[25];

        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Callable<Integer>> callables = new ArrayList<>();

        for (String text : texts) {
            Callable<Integer> callable = () -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);
                return maxSize;
            };

            callables.add(callable);
        }
        long startTs = System.currentTimeMillis();

        List<Future<Integer>> futures = executorService.invokeAll(callables);

        int maxInterval = 0;
        for (Future<Integer> future : futures) {
            int maxSize = future.get();
            if (maxSize > maxInterval) {
                maxInterval = maxSize;
            }
        }

        executorService.shutdown();
        long endTs = System.currentTimeMillis();

        System.out.println("Max Interval: " + maxInterval);
        System.out.println("Time: " + (endTs - startTs) + "ms");

    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}