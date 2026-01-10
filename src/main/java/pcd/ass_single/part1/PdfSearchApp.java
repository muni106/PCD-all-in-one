package pcd.ass_single.part1;

public class PdfSearchApp {

    public static void main(String[] args) {

        SearchModel model = new SearchModel();
        SearchController controller = new SearchController(model);
        SearchView view = new SearchView(controller);

        model.addObserver(view);
        view.setVisible(true);
    }
}
