package pcd.ass_single.part1;

import pcd.ass_single.part1.strategies.virtual_threads.ExtractTextVirtualThreads;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExtractionModel {

    private List<ModelObserver> observers;
    private volatile int countFiles;
    private volatile int countPdfFiles;
    private volatile int countPdfFilesWithWord;
    private List<File> pdfs;

    public ExtractionModel(){
        countFiles = 0;
        countPdfFiles = 0;
        countPdfFilesWithWord = 0;
        observers = new ArrayList<>();
    }

    public void startFromScratch(String directoryPath, String searchWord) {
        pdfs = collectPdfFiles(directoryPath);
        scrapePdfsWithWord(searchWord);
    }

    private void scrapePdfsWithWord(String searchedWord) {
        ExtractText textScraper = new ExtractTextVirtualThreads();
        try {
            textScraper.extractText(pdfs, searchedWord, this);
        } catch ( Exception e ) {
            System.err.println(e.getMessage());
        }

        notifyObservers();
    }

    private List<File> collectPdfFiles(String directoryPath) {
        System.out.println("Extracting files from directory: " + directoryPath);
        File directory = new File(directoryPath);

        File[] files = directory.listFiles();

        ArrayList<File> pdfs = new ArrayList<>();

        if (files != null) {
            for ( File file : files ) {
                setCountFiles(countFiles + 1);
                if (file.isDirectory()) {
                    pdfs.addAll(collectPdfFiles(file.getAbsolutePath()));
                } else if (file.isFile() && file.getName().endsWith(".pdf")) {
                    setCountPdfFiles(countPdfFiles + 1);
                    pdfs.add(file);
                }
            }
        }
        return pdfs;
    }

    public void addObserver(ModelObserver obs) {
        observers.add(obs);
    }

    public int getCountFiles() {
        return countFiles;
    }

    public int getCountPdfFiles() {
        return countPdfFiles;
    }

    public int getCountPdfFilesWithWord() {
        return countPdfFilesWithWord;
    }

    public void setCountPdfFilesWithWord(int countPdfFilesWithWord) {
        this.countPdfFilesWithWord = countPdfFilesWithWord;
        notifyObservers();
    }

    public void setCountFiles(int countFiles) {
        this.countFiles = countFiles;
        notifyObservers();
    }

    public void setCountPdfFiles(int countPdfFiles) {
        this.countPdfFiles = countPdfFiles;
        notifyObservers();
    }

    private void notifyObservers() {
        for (ModelObserver obs: observers) {
            obs.modelUpdated(this);
        }
    }
}
