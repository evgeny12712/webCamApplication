package Gallery;

import android.net.Uri;

import java.io.File;

public class VideoItem {
    private Uri uri;
    private String date;
    private File file;

    public VideoItem(File file, Uri uri, String date) {
        this.file = file;
        this.uri = uri;
        this.date = date;
    }

    public File getFile() {
        return file;
    }
    public Uri getUri() {
        return uri;
    }
    public String getDate() {
        return date;
    }
    public void setUri(Uri uri) {
        this.uri = uri;
    }
    public void setDate(String date) {
        this.date = date;
    }

}
