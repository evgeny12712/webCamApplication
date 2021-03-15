package Gallery.General;

import android.net.Uri;

import java.io.File;

public class Item {
    private Uri uri;
    private String date;
    private File file;
    private String fileType;
    private boolean isLandscape;

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
    public boolean getIsLandscape() {
        String isLorV = file.getPath().substring(file.getPath().lastIndexOf("/")+1, file.getPath().lastIndexOf("/")+2);
        if(isLorV.equals("L")) {
            return true;
        }
        return false;
    }
}
