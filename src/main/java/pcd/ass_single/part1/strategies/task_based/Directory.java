package pcd.ass_single.part1.strategies.task_based;


import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Directory {
    private final List<Directory> subDirectories;
    private final List<File> pdfs;

    public Directory(List<Directory> subDirectories, List<File> pdfs) {
        this.subDirectories = subDirectories;
        this.pdfs = pdfs;
    }

    public List<Directory> getSubDirectories() {
        return this.subDirectories;
    }

    public List<File> getPdfs() {
        return this.pdfs;
    }

    public static Directory fromDirectory(File dir) throws IOException {
        List<File> pdfs = new LinkedList<File>();
        List<Directory> subDirectories = new LinkedList<Directory>();
        for (File entry : dir.listFiles()) {
            if (entry.isDirectory()) {
                subDirectories.add(Directory.fromDirectory(entry));
            } else if (entry.getName().endsWith("pdf")){
                pdfs.add(entry);
            }
        }
        return new Directory(subDirectories, pdfs);
    }
}
