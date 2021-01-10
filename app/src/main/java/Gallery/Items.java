package Gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.LocaleData;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Gallery.GalleryActivity;

import static Gallery.GalleryActivity.fileTypes;

public class Items {
    private static final List<Item> temporaryFiles = new ArrayList<>();
    private static final List<Item> savedFiles = new ArrayList<>();
    private static final List<Item> images = new ArrayList<>();
    private static final String TAG = "Items";

    public static List<Item> getTemporaryFiles() {
        return temporaryFiles;
    }
    public static List<Item> getSavedFiles() {
        return savedFiles;
    }
    public static List<Item> getImages() { return images; };


    //LOAD ALL FILES FROM A SPECIFIC DIRECTORY
    public static void loadFiles(File dir, String filesType) {
        if(dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                Item newItem = new Item(file, filesType);
                if(findItemByUri(temporaryFiles, newItem.getUri()) != null
                        || findItemByUri(savedFiles, newItem.getUri()) != null
                        || findItemByUri(images, newItem.getUri()) != null) {
                    continue;
                }
                else {
                    addItem(newItem, filesType);
                }
            }
        }
    }

    public static String getDateFromFile(File file) {
        Date lastModDate = new Date(file.lastModified());
        String date = DateFormat.getDateInstance().format(lastModDate);
        String time = DateFormat.getTimeInstance().format(lastModDate);
        return date + "," + time;
    }


    private static void addItem(Item item, String filesType) {
        switch(filesType) {
            case "temporary videos" :
                if(!Items.getTemporaryFiles().contains(item)) {
                    temporaryFiles.add(item);
                }
                    break;
            case "saved videos" :
                if(!Items.getSavedFiles().contains(item)) {
                    savedFiles.add(item);
                }
                break;
            case "images" :
                if(!Items.getImages().contains(item)) {
                    images.add(item);
                }
                break;
        }
    }

    public static void saveFile(Item itemSrc, Context context) throws IOException {
            // getting the file name
            String path = itemSrc.getFile().getPath();
            String fileName = path.substring(path.lastIndexOf("/") + 1);

            File dst = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
            try (InputStream in = new FileInputStream(itemSrc.getFile())) {
                try (OutputStream out = new FileOutputStream(dst)) {
                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
            }
        deleteFile(itemSrc, context, "temporary videos");
    }

    public static void deleteFile(Item item, Context context, String fileType) {

        switch(fileType) {
            case "temporary videos":
                temporaryFiles.remove(item);
                item.getFile().delete();
                break;
            case "saved videos":
                item.getFile().delete();
                savedFiles.remove(item);
                break;
            case "images":
                images.remove(item);
                item.getFile().delete();
                break;
        }
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra("fragment", fileType);
        context.startActivity(intent);
    }

    public static Item findItemByUri(List<Item> items, Uri uri) {
        for(Item item : items) {

            if (item.getUri().equals(uri)) {
                return item;
            }
        }
        return null;
    }

}
