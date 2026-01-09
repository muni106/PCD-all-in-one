package pcd.ass_single.part1.strategies.thread;

import pcd.ass_single.part1.ExtractText;
import pcd.ass_single.part1.ExtractionModel;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class ExtractTextThread implements ExtractText {
    // TODO fix model logic
    @Override
    public void extractText(List<File> files, String word, ExtractionModel model) throws IOException {

        int Ncpu = Runtime.getRuntime().availableProcessors();
        long startTime = System.currentTimeMillis();
        // A system with Ncpu processors usually achieves optimum utilization with a thread pool of Ncpu + 1 threads
        int Nthreads = Ncpu + 1;


        Monitor m;

        if (files != null) {
            // i can decide the number of threads based on the number of files
            int numFiles = files.size();
            m = new Monitor(numFiles);

            if (numFiles < Ncpu) {
                Nthreads = numFiles;
            }

            int i = 0;
            int step = numFiles / Nthreads;


            while ((i + step) < numFiles) {
                new Worker(m, i, i + step, files, word).start();
                i += step;
            }

            new Worker(m, i, numFiles, files, word).start();
            new Output(m, startTime).start();
        }else {
            System.err.println("No files found");
        }
    }

}


