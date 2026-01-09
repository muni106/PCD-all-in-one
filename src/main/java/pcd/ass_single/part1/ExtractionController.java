package pcd.ass_single.part1;


public class ExtractionController {
    private ExtractionModel model;
    private boolean started;

    public ExtractionController(ExtractionModel model) {
        this.model = model;
        this.started = false;
    }

    public void processEvent(ExtractionEvent event) {
        try {
            new Thread(() -> {
                try {
                    log("[Controller] processing the event: " + event);
                    Thread.sleep(1000);
                    switch (event.eventType()) {
                        case START -> {

                        }
                        case STOP -> {
                        }
                        case SUSPEND -> {
                        }
                        case RESUME -> {
                        }
                    }
                    model.update();
                    log("[Controller] event processing done");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void log(String msg) {
        System.out.println(msg);
    }
}
