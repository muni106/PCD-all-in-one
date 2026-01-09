package pcd.ass_single.part1;

public class StartTextExtraction {

    public static void main(String[] args) {

        ExtractionModel model = new ExtractionModel();
        ExtractionController controller = new ExtractionController(model);
        ExtractionView view = new ExtractionView(controller);

        model.addObserver(view);
        view.setVisible(true);

    }

//    private static List<File> collectPdfFiles(String directoryPath) {
//        System.out.println("Extracting files from directory: " + directoryPath);
//        File directory = new File(directoryPath);
//
//        File[] files = directory.listFiles();
//
//        ArrayList<File> pdfs = new ArrayList<>();
//
//        if (files != null) {
//            for ( File file : files ) {
//                if ( file.isDirectory()) {
//                    pdfs.addAll(collectPdfFiles(file.getAbsolutePath()));
//                }
//                else if (file.isFile() && file.getName().endsWith(".pdf")) {
//                    pdfs.add(file);
//                }
//            }
//        }
//        return pdfs;
//    }
//

//    private static void usage() {
//        System.err.println("Usage: java " + pcd.ass_single.part1.example.BaseProgram.class.getName() + " <directory> <word>");
//        System.exit(-1);
//    }
}
