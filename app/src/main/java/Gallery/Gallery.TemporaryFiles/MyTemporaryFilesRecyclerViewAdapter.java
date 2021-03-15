package Gallery.Gallery.TemporaryFiles;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.webcamapplication.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

import Gallery.General.Items;

import static Gallery.General.GalleryActivity.*;
import static Gallery.General.Items.getFilesFromItems;
import static Gallery.General.SelectedGalleryAdapter.*;

public class MyTemporaryFilesRecyclerViewAdapter extends RecyclerView.Adapter<MyTemporaryFilesRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "CustomAdapter";
    private Context mContext;
    private boolean isSelectionState;
    protected Toolbar toolbarSelection;
    private Activity parentActivity;
    //selection toolbar
    private TextView numOfSelectedItems;
    private ImageButton selectionSave;
    private ImageButton selectionShare;
    private ImageButton selectionDelete;
    private ImageButton deselectButton;
    public MyTemporaryFilesRecyclerViewAdapter(Context context, Activity parentActivity) {
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
                .inflate(R.layout.imageview_temporary_files, parent, false);
        initSelection(fileTypes[0]);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        toolbarSelection = (Toolbar) parentActivity.findViewById(R.id.toolbar_temporary_selection);
        numOfSelectedItems = (TextView) parentActivity.findViewById(R.id.items_temporary_num);
        selectionSave = (ImageButton) parentActivity.findViewById(R.id.selection_temporary_save);
        selectionShare = (ImageButton) parentActivity.findViewById(R.id.selection_temporary_share);
        selectionDelete = (ImageButton) parentActivity.findViewById(R.id.selection_temporary_delete);
        deselectButton = (ImageButton) parentActivity.findViewById(R.id.deselect_button);
        toolbarSelection.setElevation(5);

        Glide.with(mContext)
                .load(Uri.fromFile(Items.getTemporaryFiles().get(position).getFile()))
                .into(holder.mImageView);


        holder.mImageViewSelected.setVisibility(View.GONE);
        selectionSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        saveSelectedItems(mContext, fileTypes[0]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        selectionShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<File> files = getFilesFromItems(getSelectedItems(fileTypes[0]));
                shareSelectedItems(files, mContext);
            }
        });

        selectionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<File> files = getFilesFromItems(getSelectedItems(fileTypes[0]));
                deleteSelectedItems(files, mContext, getSelectedItems(fileTypes[0]));
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
                    Intent intent = new Intent(mContext, TemporaryVideoDisplayActivity.class);
                    intent.putExtra(fileTypes[0], position);
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
        return Items.getTemporaryFiles().size();
    }


}
