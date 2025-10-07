package pcd.ass_single.part1;

import pcd.ass_single.part1.thread.ExtractTextThread;
import pcd.ass_single.part1.virtual_threads.ExtractTextVirtualThreads;

public class StartTextExtraction {

    public static void main(String[] args) {
        if ( args.length != 2 ) {
            usage();
        }

        String directoryPath = args[0]; // first argument is the directory path
        String word = args[1]; // second argument is the word

        ExtractText extractor = new ExtractTextThread();

        try {
            extractor.extractText(word, directoryPath);
        } catch ( Exception e ) {
            System.err.println(e.getMessage());
        }

    }

    private static void usage() {
        System.err.println("Usage: java " + pcd.ass_single.part1.example.BaseProgram.class.getName() + " <directory> <word>");
        System.exit(-1);
    }
}
