package Settings;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;

import com.example.webcamapplication.R;

import CameraAndSupport.CameraClass;
import MainWindow.MainActivity;

public class SoundDialog extends AppCompatDialogFragment {
    private static final int REQUEST_AUDIO_PERMISSION_RESULT = 0;

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
        soundSwitch.setChecked(CameraClass.getIsSound());
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
                if(soundSwitch.isChecked()) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        //if has permission so update shared preferences
                        MainActivity.getSharedPreferencesEditor().putBoolean("isSound", true).commit();
                    } else {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                            Toast.makeText(getActivity().getApplicationContext(), "app will not be able to record audio", Toast.LENGTH_SHORT).show();
                        }
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION_RESULT);
                    }
                } else
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_AUDIO_PERMISSION_RESULT) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MainActivity.getSharedPreferencesEditor().putBoolean("isSound", true).commit();
            }
        }
    }
}
