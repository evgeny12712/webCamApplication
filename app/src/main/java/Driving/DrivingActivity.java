package Driving;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentTransaction;

import CameraAndSupport.CameraClass;
import Gallery.Items;
import MainWindow.MainActivity;
import com.example.webcamapplication.R;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class DrivingActivity extends AppCompatActivity {
    private static final String TAG = "DrivingActivity";
    private static final int GPS_PERMISSION_CODE = 0;

    //notification constants
    private final String notTitle = "webCamApplication";
    private final String notText = "Your webCamApplication is recording!";
    private final String channelName = "webCamApplication channel";
    private final String channelDesctiption = "channel for one notification";
    private final String CHANNEL_ID = "webC";
    private final int notificationId = 1;

    //buttons
    private ImageButton btnMinimize, btnStop, btnPicture;

    //speed TextView
    private TextView speedTextView;

    private CameraRecordingFragment cameraFragment;
    private SpeedAndChronoFragment speedAndChronoFragment;

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

    //handler for auto stop and start recording.
    Handler handler;


    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);
        btnMinimize = (ImageButton) findViewById(R.id.btnMinimize);
        cameraFragment = (CameraRecordingFragment) getSupportFragmentManager().findFragmentById(R.id.cameraRecordingFragment);
        speedAndChronoFragment = (SpeedAndChronoFragment) getSupportFragmentManager().findFragmentById(R.id.speed_and_chronometer);

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
                .setColor(getResources().getColor(R.color.green))
                .setColorized(true)
                .setContentTitle(notTitle)
                .setContentText(notText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
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
            speedAndChronoFragment.startChronoFromZero();
        }

        Intent recreateIntent = new Intent(this, DrivingActivity.class);

        //setting up the timer for the current video file
        handler = new Handler();
        ///CHANGE DELAY FOR TIME*60*1000
        int delay = 10*1000;
        if(handler != null) {
            handler.postDelayed(new Runnable() {
                public void run() {
                    Toast.makeText(DrivingActivity.this, "inside", Toast.LENGTH_SHORT).show();
                    if (handler != null) {
                        Toast.makeText(DrivingActivity.this, "inside2", Toast.LENGTH_SHORT).show();

                        handler.postDelayed(this, delay);
                        cameraFragment.stopMediaRecorder();
                        try {
                            cameraFragment.getCamera().createVideoFileName();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        cameraFragment.startMediaRecorder();
                        cameraFragment.startRecord();
                        startRecording();
                    }
                }
            }, delay);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //check if we already got permission
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                initLocation();
            }
            else {
                //check if we want to explain to the user why we need this permission
                if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(context, "Location permission is needed to show speed and " +
                            "automatic stop", Toast.LENGTH_SHORT).show();
                }
                //ask for the permission
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, GPS_PERMISSION_CODE);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        notificationCompatManager.notify(notificationId, builder.build());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        notificationCompatManager.cancel(notificationId);
        Log.d(TAG, "onRestart: ");
        Toast.makeText(context, "onRestart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if we were filming the video on landscape mode so we need to update the file name
        if (cameraFragment.getCamera().getIsLandscape()) {
            boolean isSuccess = renameFile();
        }
        cameraFragment.setIsFirstTime(true);
        cameraFragment.stopMediaRecorder();
        //deleting the notification when we stop recording
        notificationCompatManager.cancel(notificationId);
        handler = null;
        cameraFragment = null;
        //if we were on "do not disturb" mode while driving so we need to cancel it now
        if (isDnd) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            }
        }
        Toast.makeText(context, "Destroyed", Toast.LENGTH_SHORT).show();
    }

    public static int getIntSizeOfFile() {
        return intSizeOfFile;
    }

    public static boolean getIsDnd() {
        return isDnd;
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

    public static void setupNotificationManager(Context context) {
        mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }

    public static void setSizeOfFiles(String size) {
        sizeOfFile = size + ":01";
        intSizeOfFile = Integer.parseInt(size);
    }

    public static void setIsDnd(Boolean is_dnd) {
        isDnd = is_dnd;
    }

//    private void stopRecording() {
//        cameraFragment.setIsFirstTime(true);
//        cameraFragment.getMediaRecorder().stop();
//        cameraFragment.getMediaRecorder().reset();
//        cameraFragment.getMediaRecorder().release();
//        cameraFragment.getCamera().closeCamera(cameraFragment.getCameraDevice());
//        cameraFragment.stopBackGroundThread();
//        mChronometer.stop();
//    }

    private void startRecording() {
        speedAndChronoFragment.startChronoFromZero();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == GPS_PERMISSION_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLocation();
            } else {
                Toast.makeText(context, "Permission was not granted", Toast.LENGTH_SHORT).show();
                speedAndChronoFragment.getSpeedTextView().setVisibility(View.GONE);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void initLocation() {
        //speedDetector = new SpeedDetector();
    }
}