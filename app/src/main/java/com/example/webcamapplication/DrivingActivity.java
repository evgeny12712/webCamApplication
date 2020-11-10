package com.example.webcamapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
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

import com.example.webcamapplication.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


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
    private File mVideoFolder; //file path
    private String mVideoFileName; // file name
    private CameraClass camera;
    private static SparseIntArray ORIENTATIONS = new SparseIntArray(); //converting surface orientation to real numbers
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);

    }


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
//                mMediaRecorder.stop();
//                mMediaRecorder.reset();
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
            mMediaRecorder = camera.setupMediaRecorder();
            connectCamera();
        } else {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }


    protected void onPause() {
        closeCamera();
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
                camera.createFolderAndFile();
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

    private static int sensorToDeviceRotation(CameraCharacteristics cameraCharacteristics, int deviceOrientation) {
        int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION); //getting the camera sensor orientations
        deviceOrientation = ORIENTATIONS.get(deviceOrientation); // getting the device orientations from ORIENTATIONS map
        return(sensorOrientation + deviceOrientation + 360) % 360;
    }


    public void startPreview() {
        //first convert texture view into surface view that the camera can understand.
        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(camera.getPreviewSize().getWidth(), camera.getPreviewSize().getHeight());
        Surface previewSurface = new Surface(surfaceTexture);
        try {
            //setting up a request builder for preview
            mCaptureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // adding the preview surface to the capture builder
            mCaptureRequestBuilder.addTarget(previewSurface);
            // creating the session
            cameraDevice.createCaptureSession(Arrays.asList(previewSurface),
                    new CameraCaptureSession.StateCallback() {
                        public void onConfigured(CameraCaptureSession session) {
                            try {
                                // creating the repeating capture request on the background thread
                                session.setRepeatingRequest(mCaptureRequestBuilder.build(), null, backgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Toast.makeText(getApplicationContext(), "unable to setup camera preview", Toast.LENGTH_SHORT).show();
                        }
                    }, null);

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

//            cameraDevice.createCaptureSession(Arrays.asList(previewSurface, recordSurface),
//                    new CameraCaptureSession.StateCallback() {
//                        @Override
//                        public void onConfigured(@NonNull CameraCaptureSession session) {
//                            try {
//                                session.setRepeatingRequest(mCaptureRequestBuilder.build(), null, null);
//                            } catch (CameraAccessException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
//
//                        }
//                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
       }
        }

    private void closeCamera() {
        if(cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
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

//    private void createVideoFolder() {
//        //getting the directory in which we will create the folder for our files
//        File movieFile = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES);
//        //creating the folder that we want to save into
//        Log.d("ASD", "createVideoFolder: " + movieFile);
//        mVideoFolder = new File(movieFile, "webCamVideos");
//        //checking if we don't have the folder yet
//        if(!mVideoFolder.exists()) {
//            //creating the folder
//            mVideoFolder.mkdirs();
//        }
//    }

//    private File createVideoFileName() throws IOException {
//        //creating the time string
//        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        //creating the file name
//        String prepend = "VIDEO_" + timestamp + "_";
//        //creating the actual file
//        File videoFile = File.createTempFile(prepend, ".mp4", mVideoFolder);
//        //setting the file inside the folder that we created on "createVideoFolder" func
//        mVideoFileName = videoFile.getAbsolutePath();
//        return videoFile;
//    }

    private void checkWriteStoragePermission() {
        //checking if our version is greater then marshmallow
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //checking if we already got permission
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                    //create file to save video
                    camera.createFolderAndFile();
                    startPreview();
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

//    public void setupMediaRecorder() {
//        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
//        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        mMediaRecorder.setOutputFile(mVideoFileName);
//        mMediaRecorder.setVideoEncodingBitRate(1000000);
//        mMediaRecorder.setVideoFrameRate(30);
//        mMediaRecorder.setVideoSize(camera.getPreviewSize().getWidth(), camera.getPreviewSize().getHeight());
//        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//        mMediaRecorder.setOrientationHint(camera.getTotaoRotation());
//        try {
//            mMediaRecorder.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "EXCEPT-PREPARE", Toast.LENGTH_SHORT).show();
//        }
//    }


    //fitting the camera resolution to the device
    private static class CompareSizeByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() /
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }





    //    public void startPreview() {
//        //first convert texture view into surface view that the camera can understand.
//        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
//        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
//        Surface previewSurface = new Surface(surfaceTexture);
//
//        try {
//            mCaptureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//            mCaptureRequestBuilder.addTarget(previewSurface);
//
//            cameraDevice.createCaptureSession(Arrays.asList(previewSurface),
//                    new CameraCaptureSession.StateCallback() {
//                        @RequiresApi(api = Build.VERSION_CODES.P)
//                        @Override
//                        public void onConfigured(@NonNull CameraCaptureSession session) {
//                            try {
//                                session.setRepeatingRequest(mCaptureRequestBuilder.build(), null, backgroundHandler);
//                            } catch (CameraAccessException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
//                            Toast.makeText(DrivingActivity.this, "unable to setup camera preview", Toast.LENGTH_SHORT).show();
//                        }
//                    }, null);
//
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }

}