package Driving;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {
    public static final String TAG = "NotificationReceiver";
    DrivingActivity drivingActivity;
    @Override
    public void onReceive(Context context, Intent intent) {
        //String action = intent.getStringExtra("delete");
        Toast.makeText(context, "YOYO", Toast.LENGTH_SHORT).show();
        drivingActivity.finish();
        Log.d(TAG, "onReceive: YOYO");
    }

    public void setDrivingActivity(DrivingActivity drivingActivity) {
        this.drivingActivity = drivingActivity;
    }
}
