package pcd.ass_single.part1.task_based;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor {
    private int count;
    private Lock mutex;
    private Condition workersFinished;
    private int numFiles;

    public Monitor(int numFiles){
        mutex = new ReentrantLock();
        workersFinished = mutex.newCondition();
        this.numFiles = numFiles;
        count = 0;
    }

    public void updateFoundFiles(int analizedFiles, int filesFound){
        try {
            mutex.lock();
            count += filesFound;
            numFiles -= analizedFiles;
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

