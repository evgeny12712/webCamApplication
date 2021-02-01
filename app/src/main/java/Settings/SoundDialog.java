package Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.webcamapplication.R;

import MainWindow.MainActivity;

public class SoundDialog extends AppCompatDialogFragment {
    private Switch soundSwitch;
    private TextView soundOn;
    private TextView soundOff;
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        View view = inflater.inflate(R.layout.activity_sound_dialog, null);
        soundSwitch = (Switch)view.findViewById(R.id.soundSwitch);
        soundOn = (TextView)view.findViewById(R.id.on);
        soundOff = (TextView)view.findViewById(R.id.off);
        ischecked(soundSwitch);
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ischecked(soundSwitch);
            }
        });

        builder.setView(view).setMessage("Would you like to have sound on your video clips?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(soundSwitch.isChecked())
                    MainActivity.getSharedPreferencesEditor().putBoolean("isSound", true).commit();
                else
                    MainActivity.getSharedPreferencesEditor().putBoolean("isSound", false).commit();
                }
        });

        return builder.create();
    }

    public void ischecked(Switch soundSwitch) {
        if(soundSwitch.isChecked()) {
            soundOn.setText("On");
            soundOff.setText("");
        }
        else {
            soundOff.setText("Off");
            soundOn.setText("");
        }
    }
}
