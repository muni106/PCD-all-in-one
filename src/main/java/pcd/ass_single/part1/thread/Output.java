package pcd.ass_single.part1.thread;

public class Output  extends Thread {
    private final Monitor cell;

    public Output(Monitor cell){
        this.cell = cell;
    }

    public void run(){
        int value = cell.get();
        System.out.println("OOOOOOOOOOO: " + value);
    }
}
