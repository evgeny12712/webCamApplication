package Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.webcamapplication.R;

import Driving.DrivingActivity;
import MainWindow.MainActivity;

public class NotificationsDialog extends AppCompatDialogFragment {
    private Switch notificationsSwitch;
    private TextView notificationsOn;
    private TextView notificationsOff;
    private int interruptionFilter;
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        View view = inflater.inflate(R.layout.activity_notifications_dialog, null);
        notificationsSwitch = (Switch)view.findViewById(R.id.notificationsSwitch);
        notificationsOn = (TextView)view.findViewById(R.id.on);
        notificationsOff = (TextView)view.findViewById(R.id.off);
        ischecked(notificationsSwitch);
        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ischecked(notificationsSwitch);
            }
        });

        builder.setView(view).setMessage("Would you like to get notifications from other applications while driving?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(notificationsSwitch.isChecked()) {
                    MainActivity.getSharedPreferencesEditor().putBoolean("isDnd", true).commit();
                    interruptionFilter = NotificationManager.INTERRUPTION_FILTER_NONE;
                }
                else {
                    interruptionFilter = NotificationManager.INTERRUPTION_FILTER_ALL;
                    MainActivity.getSharedPreferencesEditor().putBoolean("isDnd", false).commit();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(DrivingActivity.getmNotificationManager().isNotificationPolicyAccessGranted()) {
                        DrivingActivity.getmNotificationManager().setInterruptionFilter(interruptionFilter);
                    } else {
                        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        startActivity(intent);
                    }
                }

            }
        });

        return builder.create();
    }

    public void ischecked(Switch notificationsSwitch) {
        if(notificationsSwitch.isChecked()) {
            notificationsOn.setText("On");
            notificationsOff.setText("");
        }
        else {
            notificationsOff.setText("Off");
            notificationsOn.setText("");
        }
    }
}
