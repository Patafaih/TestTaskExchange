import java.io.IOException;
import java.util.Map;

public class Exchange {
    public static void main(String[] args) throws IOException, InterruptedException {
        long start = System.currentTimeMillis();

        FileProcessor fileProcessor = new FileProcessor("PATH");
        FileProcessor.threadFileProcessor = new Thread(fileProcessor);
        FileProcessor.threadFileProcessor.start();
        FileProcessor.threadFileProcessor.join();

        for (Map.Entry<String, Book> pair : fileProcessor.allBooks.entrySet()) {
            pair.getValue().bookThread.join();
            System.out.println(pair.getValue().toString());
        }

        System.out.println("Время выполнения программы - " + ((System.currentTimeMillis() - start)) / 1000 + " сек.");
    }
}