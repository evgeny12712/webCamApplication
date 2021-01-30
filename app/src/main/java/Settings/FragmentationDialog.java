package Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.webcamapplication.R;

public class FragmentationDialog extends AppCompatDialogFragment {
    private EditText editTextResetFreq;
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_number_of_files, null);
        builder.setView(view).setMessage("How do you want to fragment your clips? please choose with jumps of 5 minute's drive.")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = editTextResetFreq.getText().toString();
                if(text.length() > 0) {
                    int num = Integer.parseInt(text);
                    if (num % 5 != 0) {
                        Toast.makeText(getContext(), "Wrong Input!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        editTextResetFreq = (EditText)view.findViewById(R.id.frequencyEditText);
        return builder.create();
    }

    public interface EditTextInputListener {
        void theText(String text);
    }
}
