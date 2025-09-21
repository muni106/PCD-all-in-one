package pcd.ass_single.part1.thread;

public class Output  extends Thread {
    private final Monitor cell;
    private final long startTime;

    public Output(Monitor cell, long startTime) {
        this.cell = cell;
        this.startTime = startTime;
    }

    public void run(){
        int value = cell.get();
        long time = System.currentTimeMillis() - startTime;
        System.out.println("god: " + value);
        System.out.println("time: " + time);
    }
}
