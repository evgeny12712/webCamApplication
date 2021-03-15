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

import Driving.DrivingActivity;
import MainWindow.MainActivity;

public class sizeOfFilesDialog extends AppCompatDialogFragment {
    private EditText editTextResetFreq;
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_number_of_files, null);
        builder.setView(view).setMessage("Please choose the size of your clips, it must be between 0 and 60 minutes for each")
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
                    for(int i = 0 ; i< text.length() ; i++) {
                        if (!Character.isDigit(text.charAt(i))) {
                            Toast.makeText(getContext(), "Please enter only digits", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if(num == 0) {
                        Toast.makeText(getContext(), "File size cannot be 0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(num > 60) {
                        Toast.makeText(getContext(), "Please select an hour or less", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(num < 10) {
                        text = "0" + text;
                        MainActivity.getSharedPreferencesEditor().putString("sizeOfFiles", text).commit();
                        return;
                    }
                    MainActivity.getSharedPreferencesEditor().putString("sizeOfFiles", text).commit();
                    }
                }
        });
        editTextResetFreq = (EditText)view.findViewById(R.id.frequencyEditText);
        editTextResetFreq.setHint("" + DrivingActivity.getIntSizeOfFile());
        return builder.create();
    }
}
