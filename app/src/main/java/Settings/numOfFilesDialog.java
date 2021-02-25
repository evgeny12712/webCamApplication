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

import Gallery.Items;
import MainWindow.MainActivity;

public class numOfFilesDialog extends AppCompatDialogFragment {
    private EditText editTextResetFreq;
    public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_number_of_files, null);
            builder.setView(view).setMessage("In which frequency you want to reset your clips?")
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
                        if(num < Items.getTemporaryFiles().size()) {
                            int needToRemove = Items.getTemporaryFiles().size()-num;
                            Toast.makeText(getContext(),
                                    "You already got more then " + num + " files, please remove " + needToRemove + " files or choose smaller number",
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            MainActivity.getSharedPreferencesEditor().putInt("numOfFiles", num).commit();
                        }
                    }
                }
            });
            editTextResetFreq = (EditText)view.findViewById(R.id.frequencyEditText);
            editTextResetFreq.setHint("" + Items.getMaxTempFiles());
            return builder.create();
        }

        public interface EditTextInputListener {
            void theText(String text);
        }
}
