package Driving;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import Gallery.General.Items;
import MainWindow.MainActivity;

import com.example.webcamapplication.R;

import java.io.File;
import java.io.IOException;

import static Gallery.General.Items.loadFiles;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class DrivingActivity extends AppCompatActivity {
    private static final String TAG = "DrivingActivity";
    private static final int GPS_PERMISSION_CODE = 0;

    //regular notification
    private final String notTitle = "webCamApplication";
    private final String notText = "Your webCamApplication is recording!";
    private final String channelName = "webCamApplication channel";
    private final String channelDescription = "channel for one notification";
    private final String CHANNEL_ID = "webC";
    private final int regularNotId = 1;

    //auto stop notification
    private final int autoStopNotId = 2;
    private final String autoStopNotText = "Static mode detected, application will stop soon!" ;
    private boolean isFromAutoStopNotification;
    private Long milisUntilAutoStopTimerDone;
    private CountDownTimer autoStopCountDownTimer;
    //buttons
    private ImageButton btnMinimize, btnStop, btnPicture;

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
    private NotificationCompat.Builder regularNotBuilder;
    private NotificationCompat.Builder autoStopNotBuilder;
    private NotificationManagerCompat notificationCompatManager;
    private static boolean isOnBackground;
    //handler for auto stop and start recording.
    private Handler handler;



    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);

        btnMinimize = (ImageButton) findViewById(R.id.btnMinimize);
        cameraFragment = (CameraRecordingFragment) getSupportFragmentManager().findFragmentById(R.id.cameraRecordingFragment);
        speedAndChronoFragment = (SpeedAndChronoFragment) getSupportFragmentManager().findFragmentById(R.id.speed_and_chronometer);
        isOnBackground = false;
        context = getApplicationContext();
        Toast.makeText(context, "DrivingActivity onCreate", Toast.LENGTH_SHORT).show();
        //notifications
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationCompatManager = NotificationManagerCompat.from(this);
        createNotificationsChannel();
        createNotifications();
        if(getIntent().hasExtra("fromAutoStopIntent")) {
            isFromAutoStopNotification = true;
        }
        else {
            isFromAutoStopNotification = false;
        }

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
        if(getIntent().getBooleanExtra("isFirstTime", true)) {
            speedAndChronoFragment.startChronoFromZero();
        }

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
                        if (handler != null && cameraFragment != null) {
                            loadFiles(getExternalFilesDir(Environment.DIRECTORY_MOVIES), MainActivity.fileTypes[0], getApplicationContext());
                            Log.d(TAG, "temporaryFilesSize : "  + Items.getTemporaryFiles().size());

                            cameraFragment.stopMediaRecorder(false);
                            try {
                                cameraFragment.getCamera().createVideoFileName();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            cameraFragment.startMediaRecorder();
                            cameraFragment.startRecord();
                            resetChronometer();
                        }
                    }
                }
            }, delay);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //check if we already got permission
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
        isOnBackground = true;
        notificationCompatManager.notify(regularNotId, regularNotBuilder.build());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(getIntent().hasExtra("isFirstTime")) {
            Log.d(TAG, "onRestart: hasEXTRA");
            boolean isItFirstTIme = getIntent().getBooleanExtra("isFirstTime", true);
            if(isItFirstTIme) {
                Log.d(TAG, "onRestart: isFIRSTITME!!!!");
            }
        }
        Toast.makeText(context, "DrivingActivity : onRestart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if(isFromAutoStopNotification) {
//            Long timerLeft = milisUntilAutoStopTimerDone;
//            isFromAutoStopNotification = false;
//            speedAndChronoFragment.openAutoStopDialog();
//            Log.d(TAG, "IN_isFromAutoStopNotification" + speedAndChronoFragment.getIsAutoStopDialogOn());
//
//        }
        isOnBackground = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if we were filming the video on landscape mode so we need to update the file name
        if (cameraFragment.getCamera().getIsLandscape()) {
            boolean isSuccess = renameFile();
        }
        cameraFragment.setIsFirstTime(true);
        cameraFragment.stopMediaRecorder(true);
        //deleting the notification when we stop recording
        notificationCompatManager.cancel(regularNotId);
        //notificationCompatManager.cancelAll();
        handler = null;
        cameraFragment = null;
        //if we were on "do not disturb" mode while driving so we need to cancel it now
        if (isDnd) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            }
        }
        if(autoStopCountDownTimer != null) {
            autoStopCountDownTimer.cancel();
            autoStopCountDownTimer = null;
        }
        Toast.makeText(context, "DrivingActivity Destroyed", Toast.LENGTH_SHORT).show();
    }


    //GETTERS
    public static int getIntSizeOfFile() {
        return intSizeOfFile;
    }
    public static boolean getIsDnd() {
        return isDnd;
    }
    public static boolean getIsOnBackground() { return isOnBackground; }
    public static NotificationManager getmNotificationManager() {
        return mNotificationManager;
    }
    public CountDownTimer getAutoStopCountDownTimer() {
        return autoStopCountDownTimer;
    }
    //SETTERS
    public static void setSizeOfFiles(String size) {
        sizeOfFile = size + ":01";
        intSizeOfFile = Integer.parseInt(size);
    }
    public static void setIsDnd(Boolean is_dnd) {
        isDnd = is_dnd;
    }

    public static void setupNotificationManager(Context context) {
        mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
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

    private void resetChronometer() {
        speedAndChronoFragment.startChronoFromZero();
    }

    // rename file to detect if shooted landscape
    private boolean renameFile() {
        String fileName = cameraFragment.getCamera().getFileName();
        fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
        File directory = cameraFragment.getMovieFolder();
        File from = new File(directory, fileName);
        File to = new File(directory, "L" + fileName);
        Log.d("fileName", fileName);
        return from.renameTo(to);
    }

    //////////////////////////////////////Notifications//////////////////////////////////////
    public void createNotificationsChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = channelName;
            String description = channelDescription;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setVibrationPattern(new long[]{300, 300, 300});
            // Registering the channel to the service
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotifications() {
        ////////////////////////////REGULAR NOTIFICATION////////////////////////////
        //Intent for the regular notification onClick
        Intent intent = new Intent(this, DrivingActivity.class);
        intent.putExtra("isFirstTime", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), regularNotId,
                intent,PendingIntent.FLAG_UPDATE_CURRENT);

        //The builder for the minimized notification
        regularNotBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_camera_alt_24)
                .setColor(getResources().getColor(R.color.green))
                .setContentTitle(notTitle)
                .setContentText(notText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(true)
                .setNotificationSilent()
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        ////////////////////////////AUTO-STOP NOTIFICATION////////////////////////////
        Intent autoStopIntent = new Intent(this, DrivingActivity.class);
        autoStopIntent.putExtra("fromAutoStopIntent", "fromAutoStopIntent");
        autoStopIntent.putExtra("isFirstTime", false);
        autoStopIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent autoStopPendingIntent = PendingIntent.getActivity(this,
                autoStopNotId, autoStopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Builder for auto stop driving notification
        autoStopNotBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_camera_alt_24)
                .setColor(getResources().getColor(R.color.green))
                .setContentTitle(notTitle)
                .setContentText(autoStopNotText)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true) // closing the notification automatically when tapping it
                .setOngoing(true) // do'es not let the user close the notification with swap
                .setOnlyAlertOnce(true) // alerting sound only one time
                .setContentIntent(autoStopPendingIntent) // action on click
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Static mode detected, if you won't start driving soon so the application will stop recording!")); // adding big text
    }

    public void startAutoStopNotification() {
        notificationCompatManager.cancel(regularNotId);
        mNotificationManager.notify(autoStopNotId, autoStopNotBuilder.build());
        startAutoStopNotificationTimer();
    }

    //Start the timer for auto stop dialog open.
    private void startAutoStopNotificationTimer() {
        autoStopCountDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Update timer every tick
                milisUntilAutoStopTimerDone = millisUntilFinished;
                Log.d(TAG, "onTick: " + millisUntilFinished);
            }

            // When timer finish we need to open our dialog.
            @Override
            public void onFinish() {
                autoStopCountDownTimer.cancel();
                autoStopCountDownTimer = null;
                mNotificationManager.cancel(autoStopNotId);
                finish();
            }
        }.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == GPS_PERMISSION_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(context, "Permission was not granted", Toast.LENGTH_SHORT).show();
                speedAndChronoFragment.getSpeedTextView().setVisibility(View.GONE);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.hasExtra("fromAutoStopIntent")) {
            if(autoStopCountDownTimer != null) {
                Log.d(TAG, "onNewIntent: autoStopCountDownTimer != null");
                autoStopCountDownTimer.cancel();
                autoStopCountDownTimer = null;
            }
                Long timerLeft = milisUntilAutoStopTimerDone;
                isFromAutoStopNotification = false;
                speedAndChronoFragment.openAutoStopDialog(milisUntilAutoStopTimerDone);
                Log.d(TAG, "IN_isFromAutoStopNotification" + speedAndChronoFragment.getIsAutoStopDialogOn());
        }
    }
}