package Gallery;

import android.net.Uri;

public class VideoItem {
    private Uri uri;
    private String date;

    public VideoItem(Uri uri, String date) {
        this.uri = uri;
        this.date = date;
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
