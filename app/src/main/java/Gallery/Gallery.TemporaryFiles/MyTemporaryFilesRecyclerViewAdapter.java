package Gallery.Gallery.TemporaryFiles;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.webcamapplication.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Gallery.GalleryActivity;
import Gallery.Item;
import Gallery.Items;
import Gallery.SelectedGalleryAdapter;

import static Gallery.GalleryActivity.*;
import static Gallery.Items.getFilesFromItems;
import static Gallery.Items.getTemporaryFiles;
import static Gallery.SelectedGalleryAdapter.*;

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
        toolbarSelection = (Toolbar) parentActivity.findViewById(R.id.toolbar_selection);
        numOfSelectedItems = (TextView) parentActivity.findViewById(R.id.items_num);
        selectionSave = (ImageButton) parentActivity.findViewById(R.id.selection_save);
        selectionShare = (ImageButton) parentActivity.findViewById(R.id.selection_share);
        selectionDelete = (ImageButton) parentActivity.findViewById(R.id.selection_delete);
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
                    intent.putExtra(fileTypes[0], Items.getTemporaryFiles().get(position).getUri().getPath());
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
