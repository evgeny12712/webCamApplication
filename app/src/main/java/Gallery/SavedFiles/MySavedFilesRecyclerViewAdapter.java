package Gallery.SavedFiles;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.webcamapplication.R;

import java.io.File;
import java.util.List;

import Gallery.Items;

import static Gallery.GalleryActivity.fileTypes;
import static Gallery.Items.getFilesFromItems;
import static Gallery.SelectedGalleryAdapter.clearSelection;
import static Gallery.SelectedGalleryAdapter.deleteSelectedItems;
import static Gallery.SelectedGalleryAdapter.getAllItems;
import static Gallery.SelectedGalleryAdapter.getSelectedItemCount;
import static Gallery.SelectedGalleryAdapter.getSelectedItems;
import static Gallery.SelectedGalleryAdapter.initSelection;
import static Gallery.SelectedGalleryAdapter.isSelected;
import static Gallery.SelectedGalleryAdapter.shareSelectedItems;
import static Gallery.SelectedGalleryAdapter.toggleOffSelection;
import static Gallery.SelectedGalleryAdapter.toggleSelection;

public class MySavedFilesRecyclerViewAdapter extends RecyclerView.Adapter<MySavedFilesRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "CustomAdapter";
    private Context mContext;
    private boolean isSelectionState;
    protected Toolbar toolbarSelection;
    private Activity parentActivity;
    //selection toolbar
    private TextView numOfSelectedItems;
    private ImageButton selectionShare;
    private ImageButton selectionDelete;
    private ImageButton deselectButton;


    public MySavedFilesRecyclerViewAdapter(Context context, Activity parentActivity) {
        mContext = context;
        isSelectionState = false;
        this.parentActivity = parentActivity;
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
                .inflate(R.layout.imageview_saved_files, parent, false);
        initSelection(fileTypes[1]);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        toolbarSelection = (Toolbar) parentActivity.findViewById(R.id.toolbar_selection);
        numOfSelectedItems = (TextView) parentActivity.findViewById(R.id.items_num);
        selectionShare = (ImageButton) parentActivity.findViewById(R.id.selection_share);
        selectionDelete = (ImageButton) parentActivity.findViewById(R.id.selection_delete);
        deselectButton = (ImageButton) parentActivity.findViewById(R.id.deselect_button);
        toolbarSelection.setElevation(5);

        Glide.with(mContext)
                .load(Uri.fromFile(Items.getSavedFiles().get(position).getFile()))
                .into(holder.mImageView);


        holder.mImageViewSelected.setVisibility(View.GONE);


        selectionShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<File> files = getFilesFromItems(getSelectedItems(fileTypes[1]));
                shareSelectedItems(files, mContext);
            }
        });

        selectionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<File> files = getFilesFromItems(getSelectedItems(fileTypes[1]));
                deleteSelectedItems(files, mContext, getSelectedItems(fileTypes[1]));
                isSelectionState = false;
            }
        });

        deselectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarSelection.setVisibility(View.GONE);
                deselectButton.setVisibility(View.GONE);
                isSelectionState = false;
                clearSelection();
                holder.mImageViewSelected.setVisibility(View.GONE);
                notifyDataSetChanged();
            }
        });

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSelectionState) {
                    if (isSelected(position)) {
                        toggleOffSelection(position);
                        holder.mImageViewSelected.setVisibility(View.GONE);
                        numOfSelectedItems.setText("" + getSelectedItemCount());
                    } else if (!isSelected(position)) {
                        toggleSelection(position);
                        holder.mImageViewSelected.setVisibility(View.VISIBLE);
                        numOfSelectedItems.setText("" + getSelectedItemCount());
                    }
                }
                else {
                    Intent intent = new Intent(mContext, SavedVideoDisplayActivity.class);
                    intent.putExtra(fileTypes[1], position);
                    mContext.startActivity(intent);
                }
            }
        });

        holder.mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isSelectionState) {
                    toolbarSelection.setVisibility(View.VISIBLE);
                    isSelectionState = true;
                    toggleSelection(position);
                    numOfSelectedItems.setText("" + getSelectedItemCount());
                    Toast.makeText(mContext, "" + numOfSelectedItems.getText(), Toast.LENGTH_SHORT).show();
                    deselectButton.setVisibility(View.VISIBLE);
                    for (int i = 0 ; i < getAllItems().size() ; i++) {
                        if (isSelected(i)) {
                            holder.mImageViewSelected.setVisibility(View.VISIBLE);
                        }
                    }
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return Items.getSavedFiles().size();
    }

}
