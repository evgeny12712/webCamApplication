package Driving;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import MainWindow.MainActivity;
import com.example.webcamapplication.R;

import java.io.File;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class DrivingActivity extends AppCompatActivity {
    private ImageButton btnMinimize;
    private ImageButton btnStop;
    private ImageButton btnPicture;
    private Chronometer mChronometer;
    private CameraRecordingFragment cameraFragment;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        btnMinimize = (ImageButton) findViewById(R.id.btnMinimize);
        cameraFragment = (CameraRecordingFragment) getSupportFragmentManager().findFragmentById(R.id.cameraRecordingFragment);
        btnMinimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnStop = (ImageButton)findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraFragment.setIsFirstTime(true);
                cameraFragment.getMediaRecorder().stop();
                cameraFragment.getMediaRecorder().reset();
                mChronometer.stop();

                Intent mediaStoreUpdateIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaStoreUpdateIntent.setData(Uri.fromFile(new File(cameraFragment.getCamera().getmVideoFileName())));
                sendBroadcast(mediaStoreUpdateIntent);

                cameraFragment.getCamera().closeCamera(cameraFragment.getCameraDevice());
                cameraFragment.stopBackGroundThread();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btnPicture = (ImageButton)findViewById(R.id.btnPicture);
        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraFragment.lockFocus();
//                cameraFragment.galleryAddPic();
                Toast.makeText(DrivingActivity.this, "PICTURE TAKEN!", Toast.LENGTH_SHORT).show();
            }
        });

        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
    }

    //setting the application fullscreen
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if(hasFocus) {
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

}