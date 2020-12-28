package Gallery.Pictures;

import android.content.Context;
import android.content.Intent;
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

import Gallery.SavedFiles.SavedVideoDisplayActivity;
import Gallery.SavedFiles.VideoFiles;

public class MyPicturesRecyclerViewAdapter extends RecyclerView.Adapter<MyPicturesRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "CustomImagesAdapter";
    private Context mContext;

    public MyPicturesRecyclerViewAdapter(File dir, Context context) {
        mContext = context;
        VideoFiles.loadFiles(dir, "images");
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
        }

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
                holder.mImageView.setImageBitmap(VideoFiles.convertFileToThumbnailBitmap(new File(VideoFiles.getSavedFiles().get(position).getUri().getPath())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SavedVideoDisplayActivity.class);
                intent.putExtra("video", VideoFiles.getImages().get(position).getUri().getPath());
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return VideoFiles.getSavedFiles().size();
    }


}
