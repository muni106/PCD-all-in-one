package pcd.ass_single.part1.thread;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.rmi.server.LogStream.log;


public class ExtractTextSimpleThread {

    public static void main(String[] args) throws IOException {
        if ( args.length != 2 ) {
            usage();
        }

        int Ncpu = Runtime.getRuntime().availableProcessors();

        //A system with Ncpu processors usually achieves optimum utilization with a thread pool of Ncpu + 1 threads
        int Nthreads = Ncpu + 1;
        System.out.println(Nthreads);


        int count = 0;

        String directoryPath = args[0]; // first argument is the directory path
        String word = args[1]; // second argument is the word
        File directory = new File(directoryPath);

        File[] files = directory.listFiles();

        if (files != null) {
            int threadFiles = files.length / Nthreads;
            for (int i = 0; i < threadFiles - 1; ++i) {

            }
        }


        // i can decide the number of threads based on the number of files

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".pdf")) {
                    System.out.println("File: " + file.getName());
                    if (containsWord(file, word)) {
                        count += 1;
                    }
                }
            }
        }

        System.out.println(count + " pdf files contains the word: " + word);

    }




    private static void usage() {
        System.err.println("Usage: java " + pcd.ass_single.part1.example.BaseProgram.class.getName() + " <directory> <word>");
        System.exit(-1);
    }
}


class SynchCell {

    private int count;
    private boolean available;
    private Lock mutex;
    private Condition isAvailable;

    public SynchCell(){
        available = false;
        mutex = new ReentrantLock();
        isAvailable = mutex.newCondition();
        count = 0;
    }



    public void countNewFiles(int v){
        try {
            mutex.lock();
            count += v;
            available = true;
            isAvailable.signalAll();
        } finally {
            mutex.unlock();
        }
    }

    public int get() {
        try {
            mutex.lock();
            if (!available){
                try {
                    isAvailable.await();
                } catch (InterruptedException ex){}
            }
            return count;
        } finally {
            mutex.unlock();
        }
    }
}

class Worker extends Thread {



    private SynchCell cell;

    public Worker(SynchCell cell, int start, int end){
        super("getter");
        this.cell = cell;
    }

    public void run(){
        log("before getting");
        int value = cell.get();
        log("got value:"+value);
    }

    private static boolean containsWord(File pdf, String word) throws IOException {
        PDDocument document = PDDocument.load(pdf);

        AccessPermission ap = document.getCurrentAccessPermission();
        if (!ap.canExtractContent()) {
            throw new IOException("You do not have permission to extract text");
        }
        PDFTextStripper stripper = new PDFTextStripper();

        String text = stripper.getText(document);

        for (int i = 0; i < text.length() - word.length() ; ++i) {
            char[] currWord = word.toCharArray();
            text.getChars(i, i + word.length(), currWord, 0);
            if (Arrays.equals(currWord, word.toCharArray())) {
                document.close();
                return true;
            }
        }
        document.close();
        return false;
    }
}

class Getter extends Worker {

    private SynchCell cell;

    public Getter(SynchCell cell){
        this.cell = cell;
    }

    public void run(){
        log("before getting");
        int value = cell.get();
        log("got value:"+value);
    }
}
