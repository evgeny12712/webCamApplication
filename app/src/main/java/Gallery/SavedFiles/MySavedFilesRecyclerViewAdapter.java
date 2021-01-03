package Gallery.SavedFiles;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.webcamapplication.R;

import java.io.File;
import java.io.IOException;

import Gallery.GalleryActivity;
import Gallery.Items;

public class MySavedFilesRecyclerViewAdapter extends RecyclerView.Adapter<MySavedFilesRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "CustomAdapter";
    private Context mContext;


    public MySavedFilesRecyclerViewAdapter(Context context, File filesDir) {
        mContext = context;
        Items.loadFiles(filesDir, GalleryActivity.fileTypes[1]);
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
                .inflate(R.layout.imageview_saved_files, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            holder.mImageView.setImageBitmap(Items.convertFileToThumbnailBitmap(new File(Items.getSavedFiles().get(position).getUri().getPath()), GalleryActivity.fileTypes[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SavedVideoDisplayActivity.class);
                intent.putExtra(GalleryActivity.fileTypes[1], Items.getSavedFiles().get(position).getUri().getPath());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return Items.getSavedFiles().size();
    }

}
