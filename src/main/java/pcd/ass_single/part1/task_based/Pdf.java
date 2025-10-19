package pcd.ass_single.part1.task_based;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Pdf {

    private final List<String> lines;

    public Pdf(List<String> lines) {
        this.lines = lines;
    }

    public List<String> getLines() {
        return this.lines;
    }

    public static Pdf fromFile(File file) throws IOException {
        List<String> lines = new LinkedList<String>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return new Pdf(lines);
    }
}
