package pcd.ass_single.part1;

public class PdfSearchApp {

    public static void main(String[] args) {

        SearchModel model = new SearchModel();
        SearchController controller = new SearchController(model);
        SearchView view = new SearchView(controller);

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
