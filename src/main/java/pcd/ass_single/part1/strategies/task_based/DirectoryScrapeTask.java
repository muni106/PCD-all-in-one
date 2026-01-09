package pcd.ass_single.part1.strategies.task_based;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class DirectoryScrapeTask extends RecursiveTask<Integer> {
    private final Directory directory;
    private final String searchedWord;
    private final FileCounter fc;

    public DirectoryScrapeTask(FileCounter fc, Directory directory, String searchedWord) {
        super();
        this.fc = fc;
        this.directory = directory;
        this.searchedWord = searchedWord;
    }

    @Override
    protected Integer compute() {
        int count = 0;
        List<RecursiveTask<Integer>> forks = new LinkedList<>();
        for (Directory subDirectory : directory.getSubDirectories()) {
            DirectoryScrapeTask task = new DirectoryScrapeTask(fc, subDirectory, searchedWord);
            forks.add(task);
            task.fork();
        }

        for (File pdf: directory.getPdfs()) {
            WordSearchTask task = new WordSearchTask(fc, pdf, searchedWord);
            forks.add(task);
            task.fork();
        }

        for (RecursiveTask<Integer> task : forks) {
            count = count + task.join();
        }
        return count;
    }
}
