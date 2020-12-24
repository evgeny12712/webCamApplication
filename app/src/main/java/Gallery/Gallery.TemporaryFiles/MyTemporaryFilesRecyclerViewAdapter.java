package Gallery.Gallery.TemporaryFiles;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Toast;

import com.example.webcamapplication.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import Gallery.SavedFiles.VideoFiles;

public class MyTemporaryFilesRecyclerViewAdapter extends RecyclerView.Adapter<MyTemporaryFilesRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "CustomAdapter";
    private Context mContext;

    public MyTemporaryFilesRecyclerViewAdapter(Context context, File filesDir) {
        mContext = context;
        VideoFiles.loadTemporaryFiles(filesDir);
        Toast.makeText(context, "" + VideoFiles.getTemporaryFiles().size(), Toast.LENGTH_SHORT).show();
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
                .inflate(R.layout.imageview_temporary_files, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
                holder.mImageView.setImageBitmap(VideoFiles.convertFileToThumbnailBitmap(new File(VideoFiles.getTemporaryFiles().get(position).getUri().getPath())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VideoPreviewActivity.class);
                intent.putExtra("video", VideoFiles.getTemporaryFiles().get(position).getUri().getPath());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return VideoFiles.getTemporaryFiles().size();
    }


}
