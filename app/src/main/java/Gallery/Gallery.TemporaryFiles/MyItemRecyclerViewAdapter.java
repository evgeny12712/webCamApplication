package Gallery.Gallery.TemporaryFiles;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Environment;
import android.service.autofill.Dataset;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.webcamapplication.R;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import Gallery.GalleryPicturesFragment;
import Gallery.PictureItem;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "CustomAdapter";
    private ArrayList<File> mDataset;
    private ArrayList<ImageView> imageViews;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
        }

    }


    public MyItemRecyclerViewAdapter(File dir) {
        mDataset = new ArrayList<File>();
        loadSavedImages(dir);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        convertFilesToImages(mDataset, holder);
        //holder.mImageView = imageViews.get(0);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ArrayList<File> getmDataset() {
        return mDataset;
    }

    public void convertFilesToImages(ArrayList<File> files, ViewHolder holder) {
        for(File file : files) {
            try {
                imageViews.add(convertFileToImageView(file, holder));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ImageView convertFileToImageView(File file, ViewHolder holder) throws IOException {
        ImageView imageView = holder.mImageView;
//        Size mSize = new Size(96,96);
//        CancellationSignal ca = new CancellationSignal();
//        Bitmap bitmapThumbnail = ThumbnailUtils.createVideoThumbnail(file, mSize, ca);
//        imageView.setImageBitmap(bitmapThumbnail);
        return holder.mImageView;
    }

    public void loadSavedImages(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                String absolutePath = file.getAbsolutePath();
                String extension = absolutePath.substring(absolutePath.lastIndexOf("."));
                if (extension.equals(".mp4")) {
                    mDataset.add(file);
                }
            }
        }
    }

}
