package com.example.webcamapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class DrivingActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT = 1;
    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;
    private TextureView textureView;
    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //setting up the camera - camera id, preview size , rotation
            camera.setupCamera(textureView.getWidth(), textureView.getHeight(), getWindowManager().getDefaultDisplay().getRotation(), cameraManager);
            //making sure that the camera does'nt reset when moving from landscape and portrait mode
            textureView = Functions.transformImage(textureView.getWidth(), textureView.getHeight(), getWindowManager().getDefaultDisplay().getRotation(), camera.getPreviewSize(), textureView);
            mMediaRecorder = camera.setupMediaRecorder();
            connectCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    private CameraDevice cameraDevice;
    private CameraDevice.StateCallback cameraDeviceStateCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            checkWriteStoragePermission();
        }


        public void onDisconnected(CameraDevice camera) {
            camera.close();
            cameraDevice = null;
        }

        public void onError( CameraDevice camera, int error) {
            camera.close();
            cameraDevice = null;

        }
    };

    private File movieFile;

    private HandlerThread backgroundHandlerThread;
    private Handler backgroundHandler;

    private MediaRecorder mMediaRecorder;
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private CameraManager cameraManager;

    private ImageButton btnMinimize;
    private ImageButton btnStop;
    private ImageButton btnPicture;
    private Chronometer mChronometer;
    private CameraClass camera;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);
        mMediaRecorder = new MediaRecorder();
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        camera = new CameraClass();
        textureView = (TextureView)findViewById(R.id.textureView);
        mChronometer = (Chronometer) findViewById(R.id.videoTimer);
        btnMinimize = (ImageButton) findViewById(R.id.btnMinimize);
        movieFile = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        btnMinimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DrivingActivity.this, "Minimize", Toast.LENGTH_SHORT).show();
            }
        });

        btnStop = (ImageButton)findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
//                mChronometer.stop();
//                mChronometer.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btnPicture = (ImageButton)findViewById(R.id.btnPicture);
        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DrivingActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        startBackgroundThread();
        //creating folder to save videos
        camera.createVideoFolder(movieFile);
        try {
            camera.createVideoFileName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(textureView.isAvailable()) {
            //setting up the camera - camera id, preview size , rotation
            camera.setupCamera(textureView.getWidth(), textureView.getHeight(), getWindowManager().getDefaultDisplay().getRotation(), cameraManager);
            //making sure that the camera does'nt reset when moving from landscape and portrait mode
            textureView = Functions.transformImage(textureView.getWidth(), textureView.getHeight(), getWindowManager().getDefaultDisplay().getRotation(), camera.getPreviewSize(), textureView);
            mMediaRecorder = camera.setupMediaRecorder();
            connectCamera();
        } else {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }


    protected void onPause() {
        camera.closeCamera(cameraDevice);
        stopBackGroundThread();
        super.onPause();

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

    //give camera permission to preview and save files
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        if(requestCode == REQUEST_CAMERA_PERMISSION_RESULT) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Application will not run without camera permission", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission successfully granted", Toast.LENGTH_SHORT).show();
            }  else {
                Toast.makeText(this, "App needs to save video to run", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //connecting to the camera, getting the camera service, asking for permission
    private void connectCamera() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED) {
                    cameraManager.openCamera(camera.getCameraId(), cameraDeviceStateCallBack, backgroundHandler); //open the connection to the camera
                }
                else
                {
                    //check if we should show a request for permission
                    if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        Toast.makeText(this, "video app required access to camera", Toast.LENGTH_SHORT).show();
                    }
                    // asking for the permission
                    requestPermissions(new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_RESULT);
                }
            } else {
                cameraManager.openCamera(camera.getCameraId(), cameraDeviceStateCallBack, backgroundHandler); //open the connection to the camera
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void startRecord() {
        try {
            //creating the surface on which we gonna display the preview while recording
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(camera.getPreviewSize().getWidth(), camera.getPreviewSize().getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            Surface recordSurface = mMediaRecorder.getSurface();

            mCaptureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            mCaptureRequestBuilder.addTarget(previewSurface);
            mCaptureRequestBuilder.addTarget(recordSurface);

            cameraDevice.createCaptureSession(Arrays.asList(previewSurface, recordSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            try {
                                session.setRepeatingRequest(mCaptureRequestBuilder.build(), null, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
       } catch (IllegalStateException e) {
            Toast.makeText(this, "ILLEGAL STATE", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "ILLEGAL ARGUMENT", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void startBackgroundThread() {
        backgroundHandlerThread = new HandlerThread("DrivingActivity");
        backgroundHandlerThread.start();
        backgroundHandler = new Handler(backgroundHandlerThread.getLooper());
    }

    private void stopBackGroundThread() {
        backgroundHandlerThread.quitSafely();
        try {
            backgroundHandlerThread.join();
            backgroundHandlerThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkWriteStoragePermission() {
        //checking if our version is greater then marshmallow
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //checking if we already got permission
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                    //create file to save video
                    startRecord();
                    mMediaRecorder.start();
//                mChronometer.setBase(SystemClock.elapsedRealtime());
//                mChronometer.start();
            } else {
                //showing message to the user if he decided to refuse to give permission
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(this, "app needs to be able to save videos", Toast.LENGTH_SHORT).show();
                }
                //requesting the permission
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT);
            }
        } else {
                startRecord();
                mMediaRecorder.start();
//            mChronometer.setBase(SystemClock.elapsedRealtime());
//            mChronometer.setVisibility(View.VISIBLE);
//            mChronometer.start();
        }
    }

}