package Gallery.Gallery.TemporaryFiles;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.webcamapplication.R;

import java.io.File;
import java.util.List;

import Gallery.GalleryPicturesFragment;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<File> mFiles;
    private final GalleryTemporaryFilesFragment.OnListFragmentInteractionListener mListener;
    private ImageView mImageView;
    public MyItemRecyclerViewAdapter(List<File> mFiles, GalleryTemporaryFilesFragment.OnListFragmentInteractionListener mListener) {
        this.mFiles = mFiles;
        this.mListener = mListener;
    }


    @NonNull
    @Override
    public MyItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_gallery_temporary_files, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mImageView = convertFileToImageView(mFiles.get(position));

        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mImageView);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public ImageView convertFileToImageView(File file) {
        ImageView imageView = null;
        String filePath = file.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        imageView.setImageBitmap(bitmap);
        return imageView;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mItemView;
        public ImageView mImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mItemView = itemView;
            mImageView = (ImageView) itemView.findViewById(R.id.imageView);

        }
    }


}
