package Gallery.Pictures;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.CancellationSignal;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webcamapplication.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MyPicturesRecyclerViewAdapter extends RecyclerView.Adapter<MyPicturesRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "CustomAdapter";
    private ArrayList<File> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
        }

    }


    public MyPicturesRecyclerViewAdapter(File dir) {
        mDataset = new ArrayList<File>();
        loadSavedImages(dir);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.imageview_pictures, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
                holder.mImageView.setImageBitmap(convertFileToThumbnailBitmap(mDataset.get(position)));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ArrayList<File> getmDataset() {
        return mDataset;
    }

    //convert files to thumbnails and return bitmap
    public Bitmap convertFileToThumbnailBitmap(File file) throws IOException {
        Size mSize = new Size(10000000,10000000);
        CancellationSignal ca = new CancellationSignal();
        Bitmap bitmapThumbnail = ThumbnailUtils.createImageThumbnail(file, mSize, ca);
        return bitmapThumbnail;
    }

    // load Files from folder
    public void loadSavedImages(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                String absolutePath = file.getAbsolutePath();
                String extension = absolutePath.substring(absolutePath.lastIndexOf("."));
                if (extension.equals(".jpg")) {
                    mDataset.add(file);
                }
            }
        }
    }

}
