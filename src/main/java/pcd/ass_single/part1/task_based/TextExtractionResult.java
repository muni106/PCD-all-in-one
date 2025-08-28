package pcd.ass_single.part1.task_based;

public class TextExtractionResult {
    private int count;
    private int numFiles;

    public TextExtractionResult(int numFiles) {
        this.numFiles = numFiles;
        count = 0;
    }

    public synchronized void foundFiles(boolean found) {
        if (found) {
            count += 1;
        }
        numFiles -= 1;
        if (numFiles == 0) {
            notify();
        }
    }

    public synchronized int getResult() throws InterruptedException {
        while (numFiles > 0) {
            wait();
        }
        return count;
    }
}
