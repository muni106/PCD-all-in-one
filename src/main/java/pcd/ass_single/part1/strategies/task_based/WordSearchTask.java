package pcd.ass_single.part1.strategies.task_based;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.RecursiveTask;

public class WordSearchTask extends RecursiveTask<Integer> {
    private final File pdf;
    private final String searchedWord;
    private final FileCounter fc;

    public WordSearchTask(FileCounter fc, File pdf, String searchedWord) {
        super();
        this.pdf = pdf;
        this.searchedWord = searchedWord;
        this.fc = fc;
    }

    @Override
    protected Integer compute() {
        try {
            return fc.containsWord(pdf, searchedWord);
        } catch (IOException e) {
            System.err.println("Error occurred while searching for in the file: " + pdf.getName());
            return 0;
        }
    }
}
