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

import androidx.core.content.FileProvider;

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
    private static int maxTempFiles;

    public static List<Item> getTemporaryFiles() {
        return temporaryFiles;
    }

    public static List<Item> getSavedFiles() {
        return savedFiles;
    }

    public static List<Item> getImages() {
        return images;
    }

    public static int getMaxTempFiles() {
        return maxTempFiles;
    }

    public static void setMaxTempFiles(int maxTemp) {
        maxTempFiles = maxTemp;
    }

    //LOAD ALL FILES FROM A SPECIFIC DIRECTORY
    public static void loadFiles(File dir, String filesType, Context context) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                Item newItem = new Item(file, filesType);
                if (getItemByFile(temporaryFiles, file) != null
                        || getItemByFile(savedFiles, file) != null
                        || getItemByFile(images, file) != null) {
                    continue;
                } else {
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
        switch (filesType) {
            case "temporary videos":
                if (!Items.getTemporaryFiles().contains(item)) {
                    temporaryFiles.add(item);
                }
                break;
            case "saved videos":
                if (!Items.getSavedFiles().contains(item)) {
                    savedFiles.add(item);
                }
                break;
            case "images":
                if (!Items.getImages().contains(item)) {
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
        deleteFile(itemSrc, context, fileTypes[0]);
    }

    public static void deleteFile(Item item, Context context, String fileType) {

        switch (fileType) {
            case "temporary videos":
                Log.d(TAG, item.getFile().getPath());
                item.getFile().delete();
                temporaryFiles.remove(item);
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

    public static Item findItemByUri(List<Item> items, Uri uri, Context context) {
        Uri itemsUri;
        for (Item item : items) {
            itemsUri = FileProvider.getUriForFile(context
                    , context.getApplicationContext().getPackageName() + ".provider"
                    , item.getFile());
            if (itemsUri == uri) {
                return item;
            }
        }

        return null;
    }

    public static Item getItemByFile(List<Item> items, File file) {
        for (Item item : items) {
            Log.d(TAG, item.getFile().getPath());
            Log.d(TAG, file.getPath());

            if (item.getFile().equals(file)) {
                return item;
            }
        }
        return null;
    }

    public static List<File> getFilesFromItems(List<Item> items) {
        List<File> files = new ArrayList<File>();
        for (Item item : items) {
            files.add(item.getFile());
        }
        return files;
    }

    public static void deleteOldestItem() {
        long oldestDate = Long.MAX_VALUE;
        File oldestFile = null;
        for (Item item : temporaryFiles) {
            if (item.getFile().lastModified() < oldestDate) {
                oldestDate = item.getFile().lastModified();
                oldestFile = item.getFile();
            }
        }

        if (oldestFile != null) {
            oldestFile.delete();
            temporaryFiles.remove(getItemByFile(temporaryFiles, oldestFile));
        }
    }

}
