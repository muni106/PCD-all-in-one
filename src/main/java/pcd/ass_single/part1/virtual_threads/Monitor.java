package pcd.ass_single.part1.virtual_threads;

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

    public void foundWord(boolean found){
        try {
            mutex.lock();
            if (found) {
                count += 1;
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

