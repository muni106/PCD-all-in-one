package pcd.ass_single.part1.strategies.virtual_threads;

import pcd.ass_single.part1.SearchModel;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor {
    private int count;
    private Lock mutex;
    private Condition workersFinished;
    private int numFiles;
    private SearchModel model;

    public Monitor(int numFiles, SearchModel model){
        mutex = new ReentrantLock();
        workersFinished = mutex.newCondition();
        this.numFiles = numFiles;
        count = 0;
        this.model = model;
    }

    public void foundWord(boolean found){
        try {
            mutex.lock();
            if (found) {
                count += 1;
                model.setCountPdfFilesWithWord(count);
            }
            numFiles -= 1;
            if (numFiles == 0) {
                workersFinished.signal();
            }
        } finally {
            mutex.unlock();
        }
    }

    public int get() {
        try {
            mutex.lock();
            while (numFiles > 0){
                try {
                    workersFinished.await();
                } catch (InterruptedException ex){}
            }
            return count;
        } finally {
            mutex.unlock();
        }
    }
}

