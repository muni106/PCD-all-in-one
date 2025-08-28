package pcd.ass_single.part1.task_based;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TextExtractionService {

    private final File[] files;
    private ExecutorService executor;
    private int numFiles;

    public TextExtractionService (int numFiles, int poolSize, File[] files){
        this.files = files;
        this.numFiles = numFiles;
        executor = Executors.newFixedThreadPool(poolSize);
    }

    public double compute() throws InterruptedException {

        TextExtractionResult result = new TextExtractionResult(numFiles);
        for (int i = 0; i < numFiles; i++) {
            try {
                executor.execute(new TextExtractionTask(result, ));
                log("submitted task " + i );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        double res = result.getResult();
        return res;
    }


    private void log(String msg){
        System.out.println("[SERVICE] " + msg);
    }
}
