package Gallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static Gallery.Items.*;

public class SelectedGalleryAdapter {
    @SuppressWarnings("unused")
    private static final String TAG = SelectedGalleryAdapter.class.getSimpleName();

    private static SparseBooleanArray allItems = new SparseBooleanArray();;

    /**
     * Indicates if the item at position position is selected
     * @param position Position of the item to check
     * @return true if the item is selected, false otherwise
     */
    public static boolean isSelected(int position) {
        return allItems.get(position) == true;
    }

    /**
     * Toggle the selection status of the item at a given position
     * @param position Position of the item to toggle the selection status for
     */

    public static void toggleSelection(int position) {
        allItems.setValueAt(position, true);
    }
    public static void toggleOffSelection(int position) {
        allItems.setValueAt(position, false);
    }

    public static void initSelection(String filesType) {
        List<Item> items = null;
        switch(filesType) {
            case "temporary videos" :
                items = getTemporaryFiles();
                break;
            case "saved videos" :
                items = getSavedFiles();
                break;
            case "images" :
                items = getImages();
                break;
        }
        for(Item i : items) {
            allItems.append(items.indexOf(i), false);
        }
    }

    public static SparseBooleanArray getAllItems() {
        return allItems;
    }
    /**
     * Clear the selection status for all items
     */
    public static void clearSelection() {
        for(int i = 0 ; i < allItems.size() ; i++) {
            allItems.setValueAt(i, false);
        }
    }

    /**
     * Count the selected items
     * @return Selected items count
     */
    public static int getSelectedItemCount() {
        int counter = 0;
        for(int i = 0; i< allItems.size() ; i++) {
            if(allItems.get(i) == true) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * Indicates the list of selected items
     * @return List of selected items ids
     */
    public static List<Item> getSelectedItems(String filesType) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < allItems.size(); i++) {
            if (isSelected(i)) {
                switch (filesType) {
                    case "temporary videos":
                        items.add(getTemporaryFiles().get(i));
                        break;
                    case "saved videos":
                        items.add(getSavedFiles().get(i));
                        break;
                    case "images":
                        items.add(getImages().get(i));
                        break;
                }
            }
        }
        return items;
    }

    public static void saveSelectedItems(Context context, String filesType) throws IOException {
        for(Item item : getSelectedItems(filesType)) {
            saveFile(item, context);
        }
        allItems.clear();
        initSelection(filesType);
    }

    public static void shareSelectedItems(List<File> files, Context context) {
        ArrayList<Uri> uris = new ArrayList<>();
        for(File file : files) {
            uris.add(FileProvider.getUriForFile(context
                    , context.getApplicationContext().getPackageName() + ".provider"
                    , file));
        }
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("video/*");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        context.startActivity(Intent.createChooser(intent, "How to share"));
    }

    public static void deleteSelectedItems(List<File> files, Context context, List<Item> items) {
        for(File file : files) {
            deleteFile(getItemByFile(items, file), context, items.get(0).getFileType());
            initSelection(items.get(0).getFileType());
            Log.d(TAG, getSelectedItemCount() + "");
        }
    }


}
