package Gallery.SavedFiles;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.CancellationSignal;
import android.util.Log;
import android.util.Size;

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
import Gallery.VideoItem;

public class VideoFiles {
    private static final List<VideoItem> temporaryFiles = new ArrayList<>();
    private static final List<VideoItem> savedFiles = new ArrayList<>();
    private static final List<Image> images = new ArrayList<>();

    public static String theDate;
    public static List<VideoItem> getTemporaryFiles() {
        return temporaryFiles;
    }
    public static List<VideoItem> getSavedFiles() {
        return savedFiles;
    }
    public static List<Image> getImages() { return images; };


    //LOAD ALL FILES FROM A SPECIFIC DIRECTORY
    public static void loadFiles(File dir, String filesType) {
        String ending = "end";
        switch(filesType) {
            case "temporary" :
                temporaryFiles.clear();
                ending = ".mp4";
                break;
            case "saved" :
                savedFiles.clear();
                ending = ".mp4";
                break;
            case "images" :
                images.clear();
                ending = ".jpg";
                break;
        }
        if(dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                String absolutePath = file.getAbsolutePath();
                String extension = absolutePath.substring(absolutePath.lastIndexOf("."));
                if(extension.equals(ending)) {
                    loadFile(file, filesType);
                }
            }
        }
    }

    //LOAD SPECIFIC VIDEO
    public static void loadFile(File file, String fileType) {
        VideoItem newItem = new VideoItem(file, Uri.fromFile(file), getDateFromFile(file));
        addItem(newItem, fileType);
    }

    public static String getDateFromFile(File file) {
        Date lastModDate = new Date(file.lastModified());
        String date = DateFormat.getDateInstance().format(lastModDate);
        String time = DateFormat.getTimeInstance().format(lastModDate);
        Log.d("dateTime", date + "," + time);
        return date + "," + time;
    }

    //convert files to thumbnails and return bitmap
    public static Bitmap convertFileToThumbnailBitmap(File file) throws IOException {
        Size mSize = new Size(10000000,10000000);
        CancellationSignal ca = new CancellationSignal();
        Bitmap bitmapThumbnail = ThumbnailUtils.createVideoThumbnail(file, mSize, ca);
        return bitmapThumbnail;
    }

    private static void addItem(VideoItem item, String filesType) {
        switch(filesType) {
            case "temporary" :
                temporaryFiles.add(item);
                break;
            case "saved" :
                savedFiles.add(item);
                break;
            case "images" :
                images.add(item);
                break;
        }
    }

    public static void saveFile(VideoItem itemSrc, File destDir, Context context) throws IOException {
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
        deleteVideoFile(itemSrc, context, "temporary");
    }

    public static void deleteVideoFile(VideoItem item, Context context, String fileType) {
        switch(fileType) {
            case "temporary" :
                temporaryFiles.remove(item);
                break;
            case "saved" :
                savedFiles.remove(item);
                break;
            case "images" :
                images.remove(item);
                break;
        }
        item.getFile().delete();
        Intent intent = new Intent(context, GalleryActivity.class);
        context.startActivity(intent);
    }

    public static VideoItem findItemByUri(List<VideoItem> items, Uri uri) {
        for(VideoItem item : items) {
            if (item.getUri().getPath().equals(uri.getPath())) {
            }
            if (item.getUri().getPath().equals(uri.getPath())) {
                return item;
            }
        }
        return items.get(0);
    }
}
