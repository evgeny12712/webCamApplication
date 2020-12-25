package Gallery.SavedFiles;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Gallery.VideoItem;

public class VideoFiles {
    private static final List<VideoItem> temporaryFiles = new ArrayList<>();
    private static final List<VideoItem> savedFiles = new ArrayList<>();
    private static final List<VideoItem> images = new ArrayList<>();

    public static List<VideoItem> getTemporaryFiles() {
        return temporaryFiles;
    }
    public static List<VideoItem> getSavedFiles() {
        return savedFiles;
    }

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
                    loadFile(file);
                }
            }
        }
    }

    //LOAD SPECIFIC VIDEO
    public static void loadFile(File file) {
        VideoItem newItem = new VideoItem(Uri.fromFile(file), getDateFromUri(Uri.fromFile(file)));
        addItem(newItem);
    }

    private static String getDateFromUri(Uri uri) {
        //leaving only the last part of the string
        String[] split = uri.getPath().split("/");
        String fileName = split[split.length - 1];
        //getting only the part of the date and time
        String fileNameNoExt = fileName.substring(6, 21);
        String year = fileNameNoExt.substring(0, 4);
        String month = fileNameNoExt.substring(4, 6);
        String day = fileNameNoExt.substring(6, 8);
        String hour = fileNameNoExt.substring(9, 11);
        String minutes = fileNameNoExt.substring(11, 13);
        String seconds = fileNameNoExt.substring(13, 15);
        return day + " : " + month + " : " + year +" - " + hour + ":" + minutes + ":" + seconds;
    }

    //convert files to thumbnails and return bitmap
    public static Bitmap convertFileToThumbnailBitmap(File file) throws IOException {
        Size mSize = new Size(10000000,10000000);
        CancellationSignal ca = new CancellationSignal();
        Bitmap bitmapThumbnail = ThumbnailUtils.createVideoThumbnail(file, mSize, ca);
        return bitmapThumbnail;
    }

    private static void addItem(VideoItem item) {
        temporaryFiles.add(item);
    }

    public static void saveFile(VideoItem item, Context context) {
        //savedFiles.add(item);
            String inputPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();
            String outputPath = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getPath();
            Uri inputFile = item.getUri();
//            Toast.makeText(context, "" + inputPath, Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, "" + outputPath, Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, "" + inputFile, Toast.LENGTH_SHORT).show();

            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(inputPath + inputFile.getPath());
                out = new FileOutputStream(outputPath + inputFile.getPath());

//                byte[] buffer = new byte[1024];
//                int read;
//                while ((read = in.read(buffer)) != -1) {
//                    out.write(buffer, 0, read);
//                }
//                in.close();
//                in = null;
//
//                // write the output file
//                out.flush();
//                out.close();
//                out = null;
//
//                // delete the original file
//                new File(inputPath + inputFile).delete();


            }

            catch (FileNotFoundException fnfe1) {
                Toast.makeText(context, "FNFE" + inputPath, Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "" + outputPath, Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "" + inputFile, Toast.LENGTH_SHORT).show();

                Log.e("tag", fnfe1.getMessage());
            }
            catch (Exception e) {
                Toast.makeText(context, "" + inputPath, Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "" + outputPath, Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "" + inputFile, Toast.LENGTH_SHORT).show();

                Log.e("tag", e.getMessage());
            }

        //temporaryFiles.remove(item);

    }



    public static void deleteFromTemporary(VideoItem item) {
        temporaryFiles.remove(item);
    }

    private static void deleteFromSavedFiles(VideoItem item) {
        savedFiles.remove(item);
    }

    public static VideoItem findItemByUri(List<VideoItem> items, Uri uri) {
        for(VideoItem item : items) {
            if (item.getUri() == uri) {
                return item;
            }
        }
        return items.get(0);
    }
}
