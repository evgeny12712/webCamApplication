package Gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesContent {

    static final List<File> ITEMS = new ArrayList<>();

    public static List<File> getTemporaryItems() {
        return ITEMS;
    }
    public static List<File> getSavedItems() {
        return ITEMS;
    }
    public static List<File> getPictures() {
        return ITEMS;
    }

    public static void loadSavedFiles(File dir) {
        ITEMS.clear();
        if(dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                ITEMS.add(file);
            }
        }
    }

    private static void addItem(File file) {
        ITEMS.add(0, file);
    }


}
