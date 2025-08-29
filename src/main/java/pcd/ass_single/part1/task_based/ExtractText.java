package pcd.ass_single.part1.task_based;

import java.io.File;
import java.io.IOException;

public class ExtractText {

    public static void main(String[] args) throws IOException {
        if ( args.length != 2 ) {
            usage();
        }
        int Ncpu = Runtime.getRuntime().availableProcessors();
        // A system with Ncpu processors usually achieves optimum utilization with a thread pool of Ncpu + 1 threads
        int Nthreads = Ncpu + 1;

        String directoryPath = args[0]; // first argument is the directory path
        String word = args[1]; // second argument is the word
        File directory = new File(directoryPath);

        File[] files = directory.listFiles();

        if (files != null) {
            // i can decide the number of threads based on the number of files
            int numFiles = files.length;

            if (numFiles < Ncpu) {
                Nthreads = numFiles;
            }

            TextExtractionService service = new TextExtractionService(Nthreads, files, word);

            try {
                int result = service.compute();
                System.out.println(result);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.exit(0);

        }else {
            System.err.println("No files found");
            System.exit(-1);

        }
    }

    private static void usage() {
        System.err.println("Usage: java " + pcd.ass_single.part1.example.BaseProgram.class.getName() + " <directory> <word>");
        System.exit(-1);
    }
}


