package Driving;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import CameraAndSupport.CameraClass;
import Gallery.Items;
import MainWindow.MainActivity;
import com.example.webcamapplication.R;

import java.io.File;
import java.io.Serializable;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class DrivingActivity extends AppCompatActivity {
    //notification constants
    private final String notTitle = "webCamApplication";
    private final String notText = "Your webCamApplication is recording!";
    private final String channelName = "webCamApplication channel";
    private final String channelDesctiption = "channel for one notification";
    private final String CHANNEL_ID = "webC";
    private final int notificationId = 1;
    //buttons
    private ImageButton btnMinimize;
    private ImageButton btnStop;
    private ImageButton btnPicture;

    private Chronometer mChronometer;

    private CameraRecordingFragment cameraFragment;

    private static NotificationManager mNotificationManager;

    //files size
    private static String sizeOfFile;
    private static int intSizeOfFile;

    //for checking if the application is on "do not disturb" state while driving
    private static Boolean isDnd;
    private static Context context;

    //on minimized notification
    private NotificationCompat.Builder builder;
    NotificationManagerCompat notificationCompatManager;



    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        btnMinimize = (ImageButton) findViewById(R.id.btnMinimize);
        cameraFragment = (CameraRecordingFragment) getSupportFragmentManager().findFragmentById(R.id.cameraRecordingFragment);
        context = getApplicationContext();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationCompatManager = NotificationManagerCompat.from(this);
        createNotificationChannel();
        Intent intent = new Intent(this, DrivingActivity.class);
        intent.putExtra("isFirstTime", false);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                intent,PendingIntent.FLAG_UPDATE_CURRENT);

        //the builder for the no minimized notification
        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_camera_alt_24)
                .setContentTitle(notTitle)
                .setContentText(notText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        //checking if the app should start with do not disturb mode
        if (isDnd) {
            mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
            Toast.makeText(context, "Caution, do not disturb is active", Toast.LENGTH_LONG).show();
        }

        btnMinimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnStop = (ImageButton) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraFragment.setIsFirstTime(true);
                stopRecording();
                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                if (cameraFragment.getCamera().getIsLandscape()) {
                    boolean isSuccess = renameFile();
                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnPicture = (ImageButton) findViewById(R.id.btnPicture);
        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraFragment.lockFocus();
            }
        });
        if(!getIntent().hasExtra("isFirstTime")) {
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
        }

        //setting up the timer for the current video file
        Handler handler = new Handler();
        int delay = intSizeOfFile*60*1000;
        handler.postDelayed( new Runnable() {
            public void run() {
                handler.postDelayed(this, delay);
//                stopRecording();
                recreate();

            }
        }, delay);

    }

    @Override
    protected void onPause() {
        super.onPause();
        notificationCompatManager.notify(notificationId, builder.build());
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(context, "RESTART", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if we were filming the video on landscape mode so we need to update the file name
        if (cameraFragment.getCamera().getIsLandscape()) {
            boolean isSuccess = renameFile();
        }
        //if we were on "do not disturb" mode while driving so we need to cancel it now
        if (isDnd) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            }
        }
        //deleting the notification when we stop recording
        notificationCompatManager.cancel(notificationId);
    }


    //setting the application fullscreen
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    public static NotificationManager getmNotificationManager() {
        return mNotificationManager;
    }

    public static void setSizeOfFiles(String size) {
        sizeOfFile = size + ":01";
        intSizeOfFile = Integer.parseInt(size);
    }

    public static void setIsDnd(Boolean is_dnd) {
        isDnd = is_dnd;
    }

    private void stopRecording() {
        cameraFragment.setIsFirstTime(true);
        cameraFragment.getMediaRecorder().stop();
        cameraFragment.getMediaRecorder().reset();
        cameraFragment.getCamera().closeCamera(cameraFragment.getCameraDevice());
        cameraFragment.stopBackGroundThread();
        mChronometer.stop();
    }

    private boolean renameFile() {
        String fileName = cameraFragment.getCamera().getFileName();
        fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
        File directory = cameraFragment.getMovieFolder();
        File from = new File(directory, fileName);
        File to = new File(directory, "L" + fileName);
        Log.d("fileName", fileName);
        return from.renameTo(to);
    }

    public void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = channelName;
            String description = channelDesctiption;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Registering the channel to the service
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}