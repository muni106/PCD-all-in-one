package pcd.ass_single.part1;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ExtractionModel {

    private List<ModelObserver> observers;
    private int countFiles;
    private int countPdfFiles;
    private int countPdfFilesWithWord;

    public ExtractionModel(){
        countFiles = 0;
        countPdfFiles = 0;
        countPdfFilesWithWord = 0;
        observers = new ArrayList<>();
    }

    public synchronized void update() {

        notifyObservers();
    }

    private List<File> collectPdfFiles(String directoryPath) {
        System.out.println("Extracting files from directory: " + directoryPath);
        File directory = new File(directoryPath);

        File[] files = directory.listFiles();

        ArrayList<File> pdfs = new ArrayList<>();

        if (files != null) {
            for ( File file : files ) {
                countFiles += 1;
                if ( file.isDirectory()) {
                    pdfs.addAll(collectPdfFiles(file.getAbsolutePath()));
                }
                else if (file.isFile() && file.getName().endsWith(".pdf")) {
                    countPdfFiles += 1;
                    pdfs.add(file);
                }
            }
        }
        return pdfs;
    }

    public void addObserver(ModelObserver obs) {
        observers.add(obs);
    }

    public synchronized int getCountFiles() {
        return countFiles;
    }

    public synchronized int getCountPdfFiles() {
        return countPdfFiles;
    }

    public synchronized int getCountPdfFilesWithWord() {
        return countPdfFilesWithWord;
    }

    public void setCountPdfFilesWithWord(int countPdfFilesWithWord) {
        this.countPdfFilesWithWord = countPdfFilesWithWord;
    }

    private void notifyObservers() {
        for (ModelObserver obs: observers) {
            obs.modelUpdated(this);
        }
    }
}
