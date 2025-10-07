package pcd.ass_single.part1;

import java.io.File;
import java.util.List;

public interface ExtractText {
    void extractText(List<File> pdfs, String word) throws Exception;
}
