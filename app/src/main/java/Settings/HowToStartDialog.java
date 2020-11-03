package Settings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.webcamapplication.R;

import java.util.ArrayList;
import java.util.List;

public class HowToStartDialog extends DialogFragment {

    private List<String> selectedItems;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        selectedItems = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("How to start recording");
        builder.setMultiChoiceItems(R.array.howToStartOptions, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String[] items = getActivity().getResources().getStringArray(R.array.howToStartOptions);

                if(isChecked) {
                    selectedItems.add(items[which]);
                }
                else if(selectedItems.contains(items[which])) { // if item is not selected so we check if we selected it before and removing it.
                    selectedItems.remove(items[which]);
                }
            }
        });

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String final_selection = "";
                for(String item : selectedItems) {
                    final_selection = final_selection+"\n" + item;
                }
                if(selectedItems.size() != 0)
                    Toast.makeText(getActivity(),"selected : " + final_selection,  Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }
}

