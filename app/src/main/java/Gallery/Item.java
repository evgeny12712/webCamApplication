package Gallery;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class Item {
    private Uri uri;
    private String date;
    private File file;
    String fileType;

    public Item(File file, String fileType) {
        this.file = file;
        this.date = Items.getDateFromFile(file);
        this.fileType = fileType;
    }

    public File getFile() {
        return file;
    }
    public Uri getUri() {
        return Uri.fromFile(file);
    }
    public String getDate() {
        return date;
    }
    public String getFileType() {
        return fileType;
    }
    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
