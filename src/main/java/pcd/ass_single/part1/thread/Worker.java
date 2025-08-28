package pcd.ass_single.part1.thread;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Worker extends Thread {
    final private Monitor cell;
    final private File[] files;
    final private int start;
    final private int end;
    final private String searchedWord;

    public Worker(Monitor cell, int start, int end, File[] files, String word) {
        super("getter");
        this.cell = cell;
        this.files = files;
        this.start = start;
        this.end = end;
        this.searchedWord = word;
    }

    public void run(){
        int count = 0;
        for (int i = start; i < end; ++i) {
            if (files[i].isFile() && files[i].getName().endsWith(".pdf")) {
                System.out.println("File: " + files[i].getName());
                try {
                    if (containsWord(files[i], searchedWord)) {
                        count += 1;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        System.out.println("files from [ " + start + "-" + end + "], which are: " + count);
        cell.updateFoundFiles(end - start, count);


    }

    private static boolean containsWord(File pdf, String word) throws IOException {
        PDDocument document = PDDocument.load(pdf);

        AccessPermission ap = document.getCurrentAccessPermission();
        if (!ap.canExtractContent()) {
            throw new IOException("You do not have permission to extract text");
        }
        PDFTextStripper stripper = new PDFTextStripper();

        String text = stripper.getText(document);

        if (text.contains(word)) {
            document.close();
            return true;
        }
//
//        for (int i = 0; i < text.length() - word.length() ; ++i) {
//            char[] currWord = word.toCharArray();
//            text.getChars(i, i + word.length(), currWord, 0);
//            if (Arrays.equals(currWord, word.toCharArray())) {
//
//            }
//        }
        document.close();
        return false;
    }
}
