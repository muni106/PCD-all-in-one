package pcd.ass_single.part1.async_event;

import io.vertx.core.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import pcd.ass_single.part1.ExtractText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


class MyVerticle extends AbstractVerticle {
    private final List<File> pdfs;
    private final String searchedWord;

    MyVerticle(List<File> pdfs, String searchedWord) {
        this.pdfs = pdfs;
        this.searchedWord = searchedWord;
    }

    public void start() {
        long startTime = System.currentTimeMillis();
        List<Future<Integer>> futures = new ArrayList<>();

        for (File pdf : pdfs) {
            Future<Integer> future = vertx.executeBlocking(() -> {
                try {
                    return containsWord(pdf, searchedWord);
                } catch (IOException e) {
                    throw new RuntimeException("Problem in extracting word in the file : " + pdf.getName() + "..." + e);
                }
            });
            futures.add(future);
        }
        Future.all(futures).map(compositeFuture -> {
            int sum = 0;
            for (int i = 0; i < compositeFuture.size(); i++) {
                sum += compositeFuture.<Integer>resultAt(i);
        }
            return sum;
        }).onSuccess(result -> {
            long finishTime = System.currentTimeMillis();
            System.out.println("Total sum is: " + result);
            System.out.println("Total time ms: " + (finishTime - startTime));
        });
    }

    private Integer containsWord(File pdf, String word) throws IOException {
        PDDocument document = PDDocument.load(pdf);

        AccessPermission ap = document.getCurrentAccessPermission();
        if (!ap.canExtractContent()) {
            throw new IOException("You do not have permission to extract text");
        }
        PDFTextStripper stripper = new PDFTextStripper();

        String text = stripper.getText(document);

        if (text.contains(word)) {
            document.close();
            return 1;
        }

        document.close();
        return 0;
    }
}


public class AsyncExtractText implements ExtractText {
    @Override
    public void extractText(List<File> pdfs, String word) throws Exception {

        int workerPoolSize = Runtime.getRuntime().availableProcessors();
        VertxOptions options = new VertxOptions().setWorkerPoolSize(workerPoolSize);
        Vertx vertx = Vertx.vertx(options);
        vertx.deployVerticle(new MyVerticle(pdfs, word));
    }
}
