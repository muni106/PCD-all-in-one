package pcd.ass_single.part1.virtual_threads;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;


public class ExtractText {

    public static void main(String[] args) throws IOException {
        if ( args.length != 2 ) {
            usage();
        }

        String directoryPath = args[0]; // first argument is the directory path
        String word = args[1]; // second argument is the word
        File directory = new File(directoryPath);

        File[] files = directory.listFiles();

        Monitor m;

        if (files != null) {
            // i can decide the number of threads based on the number of files
            int numFiles = files.length;
            m = new Monitor(numFiles);

            Thread outputThread = Thread.ofVirtual().name("outputThread").unstarted(() -> {
                int value = m.get();
                System.out.println("OOOOOOOOOOO: " + value);
            });


            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                IntStream.range(0, numFiles).forEach(i -> {
                    executor.submit(() -> {
                        Thread
                            .ofVirtual()
                            .name("virtualThread[" + i + "]")
                            .start(() -> {
                                System.out.println("Hello from " + Thread.currentThread());
                                try {
                                    m.foundWord(containsWord(files[i], word));
                                } catch (IOException e) {
                                    m.foundWord(false);
                                }
                            });
                    });
                });
            }

            outputThread.start();

            try {
                outputThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }else {
            System.err.println("No files found");
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

    private static void usage() {
        System.err.println("Usage: java " + pcd.ass_single.part1.example.BaseProgram.class.getName() + " <directory> <word>");
        System.exit(-1);
    }
}


