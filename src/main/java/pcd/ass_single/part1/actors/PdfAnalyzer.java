package pcd.ass_single.part1.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

class PdfAnalyzer extends AbstractActor {
    static class GetCount {}

    static class PdfWordMessage {
        public final File pdf;
        public final String word;

        public PdfWordMessage(File pdf, String word) {
            this.pdf = pdf;
            this.word = word;
        }
    }

    private int count = 0;
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PdfWordMessage.class, pdfWordMessage -> {
                    this.count += containsWord(pdfWordMessage.pdf, pdfWordMessage.word);
                    Thread.sleep(1000);
                })
                .match(GetCount.class, msg -> {
                    getSender().tell(count, ActorRef.noSender());
                })
                .build();
    }

    private Integer containsWord(File pdf, String word) throws IOException {
        try (PDDocument document = PDDocument.load(pdf)) {
            AccessPermission ap = document.getCurrentAccessPermission();
            if (!ap.canExtractContent()) {
                throw new IOException("You do not have permission to extract text");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();
            return text.contains(word) ? 1 : 0;
        }
    }

}
