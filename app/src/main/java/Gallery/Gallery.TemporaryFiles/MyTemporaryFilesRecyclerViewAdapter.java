package Gallery.Gallery.TemporaryFiles;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.webcamapplication.R;

import java.io.IOException;

import Gallery.GalleryActivity;
import Gallery.Items;
import Gallery.SelectedGalleryAdapter;

import static Gallery.SelectedGalleryAdapter.*;

public class MyTemporaryFilesRecyclerViewAdapter extends RecyclerView.Adapter<MyTemporaryFilesRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "CustomAdapter";
    private Context mContext;

    public MyTemporaryFilesRecyclerViewAdapter(Context context) {
        mContext = context;

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public ImageView mImageViewSelected;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);
            mImageViewSelected = (ImageView) itemView.findViewById(R.id.imageViewSelected);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.imageview_temporary_files, parent, false);
        initSelection(Items.getTemporaryFiles());
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            holder.mImageView.setImageBitmap(Items.convertFileToThumbnailBitmap(Items.getTemporaryFiles().get(position).getFile(), GalleryActivity.fileTypes[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSelected(position)) {
                    toggleOffSelection(position);

                }
                Intent intent = new Intent(mContext, TemporaryVideoDisplayActivity.class);
                intent.putExtra(GalleryActivity.fileTypes[0], Items.getTemporaryFiles().get(position).getUri().getPath());
                mContext.startActivity(intent);
            }
        });

        holder.mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                toggleSelection(position);
                for(int pos : getSelectedItems()) {
                    if(isSelected(pos)) {
                        holder.mImageViewSelected.setVisibility(View.VISIBLE);
                    }
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return Items.getTemporaryFiles().size();
    }


}
