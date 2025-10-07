package pcd.ass_single.part1.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class BaseProgram {

    public static void main(String[] args) throws IOException {

        if ( args.length != 2 ) {
            usage();
        }

        long startTime = System.currentTimeMillis();

        int count = 0;

        String directoryPath = args[0]; // first argument is the directory path
        String word = args[1]; // second argument is the word
        File directory = new File(directoryPath);

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".pdf")) {
                    System.out.println("File: " + file.getName());
                    if (containsWord(file, word)) {
                        count += 1;
                    }
                }
            }
        }

        System.out.println(count + " pdf files contains the word: " + word);
        long endTime = System.currentTimeMillis();

        System.out.println("Total time: " + (endTime - startTime) + " ms");

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
        document.close();
        return false;
    }


    private static void usage() {
        System.err.println("Usage: java " + BaseProgram.class.getName() + " <directory> <word>");
        System.exit(-1);
    }
}
