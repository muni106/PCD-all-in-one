package pcd.ass_single.part1.task_based;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TextExtractionService {

    private final File[] files;
    private ExecutorService executor;
    private final String searchedWord;
    private int numFiles;

    public TextExtractionService (int poolSize, File[] files, String searchedWord){
        this.files = files;
        this.numFiles = files.length;
        executor = Executors.newFixedThreadPool(poolSize);
        this.searchedWord = searchedWord;
    }

    public int compute() throws InterruptedException {

        TextExtractionResult result = new TextExtractionResult(numFiles);
        for (int i = 0; i < numFiles; i++) {
            try {
                executor.execute(new TextExtractionTask(result, files[i], searchedWord));
                log("submitted task " + i );
            } catch (Exception e) {
                System.out.println("errore");
                e.printStackTrace();
            }
        }
        return result.getResult();
    }


    private void log(String msg){
        System.out.println("[SERVICE] " + msg);
    }
}
