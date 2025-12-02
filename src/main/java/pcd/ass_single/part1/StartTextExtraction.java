package pcd.ass_single.part1;

import pcd.ass_single.part1.actors.ExtractTextWithActors;
import pcd.ass_single.part1.async_event.AsyncExtractText;
import pcd.ass_single.part1.example.BaseProgram;
import pcd.ass_single.part1.reactive_prog.ReactiveExtractText;
import pcd.ass_single.part1.task_based.ExtractTextTasks;
import pcd.ass_single.part1.thread.ExtractTextThread;
import pcd.ass_single.part1.virtual_threads.ExtractTextVirtualThreads;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StartTextExtraction {

    public static void main(String[] args) {
        if ( args.length != 2 ) {
            usage();
        }

        String directoryPath = args[0]; // first argument is the directory path
        String word = args[1]; // second argument is the word

        ExtractText extractor = new ExtractTextWithActors();

        try {
            extractor.extractText(collectPdfFiles(directoryPath), word);
        } catch ( Exception e ) {
            System.err.println(e.getMessage());
        }

    }

    private static List<File> collectPdfFiles(String directoryPath) {
        System.out.println("Extracting files from directory: " + directoryPath);
        File directory = new File(directoryPath);

        File[] files = directory.listFiles();

        ArrayList<File> pdfs = new ArrayList<>();

        if (files != null) {
            for ( File file : files ) {
                if ( file.isDirectory()) {
                    pdfs.addAll(collectPdfFiles(file.getAbsolutePath()));
                }
                else if (file.isFile() && file.getName().endsWith(".pdf")) {
                    pdfs.add(file);
                }
            }
        }

        return pdfs;

    }


    private static void usage() {
        System.err.println("Usage: java " + pcd.ass_single.part1.example.BaseProgram.class.getName() + " <directory> <word>");
        System.exit(-1);
    }
}
