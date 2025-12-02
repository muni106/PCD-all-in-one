package pcd.ass_single.part1.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import pcd.ass_single.part1.actors.PdfAnalyzer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import pcd.ass_single.part1.ExtractText;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class ExtractTextWithActors implements ExtractText {

    static class RequesterActor extends  AbstractActor {
        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(Integer.class, count -> {
                        log("Number of pdfs with that word: " + count );
                        getContext().getSystem().terminate();
                    })
                    .build();
        }
    }

    @Override
    public void extractText(List<File> pdfs, String word) throws Exception {
        ActorSystem actorSystem = ActorSystem.create("PdfCounter");
        ActorRef counter = actorSystem.actorOf(Props.create(PdfAnalyzer.class));

        for (File pdf : pdfs) {
            counter.tell(new PdfAnalyzer.PdfWordMessage(pdf, word), ActorRef.noSender());
        }

        ActorRef requester = actorSystem.actorOf(Props.create(RequesterActor.class));
        counter.tell(new PdfAnalyzer.GetCount(), requester);

    }


    static private void log(String msg) {
        System.out.println("[" + Thread.currentThread().getName() + " ] " + msg);
    }
}
