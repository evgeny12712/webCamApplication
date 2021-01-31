package MainWindow;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.webcamapplication.R;

import Driving.DrivingActivity;
import Gallery.GalleryActivity;
import Gallery.Items;

import static Gallery.GalleryActivity.*;
import static Gallery.Items.loadFiles;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {
//        private CameraMainPreviewFragment cameraMainPreviewFragment;
        private ImageButton startBtn;
        private ImageButton galleryBtn;
        private ImageButton settingsBtn;
        public static String[] fileTypes;
        private static SharedPreferences sharedPreferences;
        private static SharedPreferences.Editor sharedPreferencesEditor;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            fileTypes = getResources().getStringArray(R.array.fileTypes);

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();


        loadFiles(getExternalFilesDir(Environment.DIRECTORY_MOVIES), fileTypes[0], getApplicationContext());
            loadFiles(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileTypes[1], getApplicationContext());
            loadFiles(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileTypes[2], getApplicationContext());

//            cameraMainPreviewFragment = (CameraMainPreviewFragment) getSupportFragmentManager().findFragmentById(R.id.cameraPreviewFragment);
            startBtn = (ImageButton) findViewById(R.id.btnStart);
            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED ) {
                        Intent intent = new Intent(getApplicationContext(), DrivingActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "You can't start recording without giving permission to use camera!",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            galleryBtn = (ImageButton)findViewById(R.id.btnGallery);
            galleryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                    startActivity(intent);
                }
            });

            settingsBtn = (ImageButton)findViewById(R.id.btnSettings);
            settingsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "HEY", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Settings.SettingsActivity.class);
                    startActivity(intent);
                }
            });

        }

        @Override
        protected void onResume() {
            super.onResume();
            if (sharedPreferences.getBoolean("firstRun", true)) {
                // Do first run stuff here then set 'firstrun' as false
                // using the following line to edit/commit prefs
                sharedPreferencesEditor.putBoolean("firstRun", false).commit();
                //setting up the default settings
                sharedPreferencesEditor.putInt("howToStart", 0);
                sharedPreferencesEditor.putInt("numOfFiles", 12);
                sharedPreferencesEditor.putInt("sizeOfFiles", 5);
                sharedPreferencesEditor.putBoolean("notificationsWhileDriving", true);
                sharedPreferencesEditor.putBoolean("soundWhileDriving", true);
            }
            //initialize settings
            Items.setMaxTempFiles(sharedPreferences.getInt("numOfFiles", 12));
            DrivingActivity.setSizeOfFiles(sharedPreferences.getString("sizeOfFiles", "01"));
        }

        @Override
        protected void onPause() {
//            camera.closeCamera(cameraMainPreviewFragment.getCameraDevice());
//            stopBackGroundThread();
            super.onPause();
        }

        public void onWindowFocusChanged(boolean hasFocus) {
            super.onWindowFocusChanged(hasFocus);
            View decorView = getWindow().getDecorView();
            if(hasFocus) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
         );
            }
        }

        public static SharedPreferences.Editor getSharedPreferencesEditor() {
            return sharedPreferencesEditor;
        }
}