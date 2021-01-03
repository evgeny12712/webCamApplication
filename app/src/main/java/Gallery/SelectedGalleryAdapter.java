package Gallery;

import android.util.SparseBooleanArray;


import java.util.ArrayList;
import java.util.List;

public class SelectedGalleryAdapter {
    @SuppressWarnings("unused")
    private static final String TAG = SelectedGalleryAdapter.class.getSimpleName();

    private static SparseBooleanArray selectedItems = new SparseBooleanArray();;

    /**
     * Indicates if the item at position position is selected
     * @param position Position of the item to check
     * @return true if the item is selected, false otherwise
     */
    public static boolean isSelected(int position) {
        return getSelectedItems().contains(position);
    }

    /**
     * Toggle the selection status of the item at a given position
     * @param position Position of the item to toggle the selection status for
     */
    public static void toggleSelection(int position) {
            selectedItems.put(position, true);
    }

    public static void initSelection(List<Item> items) {
        for(Item i : items) {
            selectedItems.append(items.indexOf(i), false);
        }
    }

    /**
     * Clear the selection status for all items
     */
    public static void clearSelection() {
        List<Integer> selection = getSelectedItems();
        selectedItems.clear();
        for (Integer i : selection) {
            //notifyItemChanged(i);
        }
    }

    /**
     * Count the selected items
     * @return Selected items count
     */
    public static int getSelectedItemCount() {
        return selectedItems.size();
    }

    /**
     * Indicates the list of selected items
     * @return List of selected items ids
     */
    public static List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); ++i) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }
}
