package Gallery;

import android.util.SparseBooleanArray;


import java.util.ArrayList;
import java.util.List;

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

    public static void initSelection(List<Item> items) {
        for(Item i : items) {
            allItems.append(items.indexOf(i), false);
        }
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
    public static List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(allItems.size());
        for (int i = 0; i < allItems.size(); ++i) {
            items.add(allItems.keyAt(i));
        }
        return items;
    }
}
