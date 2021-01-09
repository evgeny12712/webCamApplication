package Gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.LocaleData;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.CancellationSignal;
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

public class Items {
    private static final List<Item> temporaryFiles = new ArrayList<>();
    private static final List<Item> savedFiles = new ArrayList<>();
    private static final List<Item> images = new ArrayList<>();

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
                addItem(newItem, filesType);
            }
        }

    }

    public static String getDateFromFile(File file) {
        Date lastModDate = new Date(file.lastModified());
        String date = DateFormat.getDateInstance().format(lastModDate);
        String time = DateFormat.getTimeInstance().format(lastModDate);
        return date + "," + time;
    }

    //convert files to thumbnails and return bitmap
    public static Bitmap convertFileToThumbnailBitmap(File file, String fileType) throws IOException {
        Size mSize = new Size(10000000,10000000);
        CancellationSignal ca = new CancellationSignal();
        if(!fileType.equals("images")) {
            Bitmap bitmapThumbnail = ThumbnailUtils.createVideoThumbnail(file, mSize, ca);
            return bitmapThumbnail;
        }
        Bitmap bitmapThumbnail = ThumbnailUtils.createImageThumbnail(file, mSize, ca);
        return bitmapThumbnail;
    }

    private static void addItem(Item item, String filesType) {
        switch(filesType) {
            case "temporary videos" :
                temporaryFiles.add(item);
                break;
            case "saved videos" :
                savedFiles.add(item);
                break;
            case "images" :
                images.add(item);
                break;
        }
    }

    public static void saveFile(Item itemSrc, File destDir, Context context) throws IOException {
            // getting the file name
            String path = itemSrc.getFile().getPath();
            String fileName = path.substring(path.lastIndexOf("/") + 1);

            File dst = new File(destDir, fileName);
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
        context.startActivity(intent);
    }

    public static Item findItemByUri(List<Item> items, Uri uri) {
        Log.d("TAG", "HERE?");

        for(Item item : items) {
            Log.d("TAG", "AND HERE?");
            if (item.getUri().equals(uri)) {
                Log.d("TAG", "GOTITEM!");

                return item;
            }
        }
        Log.d("TAG", "DIDNT!");
        return items.get(0);
    }
}
