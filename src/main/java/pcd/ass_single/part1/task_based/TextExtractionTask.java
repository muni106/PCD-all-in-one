package pcd.ass_single.part1.task_based;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class TextExtractionTask implements Runnable {
    private File file;
    private TextExtractionResult result;
    private String searchedWord;

    public TextExtractionTask(TextExtractionResult result, File file, String searchedWord) {
        this.file = file;
        this.result = result;
        this.searchedWord = searchedWord;
    }

    @Override
    public void run() {
        if (file.isFile() && file.getName().endsWith(".pdf")) {
            System.out.println("File: " + file.getName());
            try {
                result.foundFiles(containsWord(file, searchedWord));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
}
