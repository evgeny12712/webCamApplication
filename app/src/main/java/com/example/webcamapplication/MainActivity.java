package com.example.webcamapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

import Gallery.GalleryActivity;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {
    private CameraClass camera;
    private CameraManager cameraManager;
    private TextureView textureView;
    boolean isPermission;
    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Toast.makeText(getApplicationContext(), "IN SURFACETEXTUREAVAILABLE", Toast.LENGTH_SHORT).show();
            camera.setupCamera(width, height, cameraManager, getWindowManager());
            transformImage(textureView.getWidth(), textureView.getHeight());
            camera.connectCamera(cameraManager, isPermission, backgroundHandlerThread);
            Toast.makeText(getApplicationContext(), "CAMERA READY", Toast.LENGTH_SHORT).show();
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
    private HandlerThread backgroundHandlerThread;
    private Handler backgroundHandler;;
    private ImageButton startBtn;
    private ImageButton galleryBtn;
    private ImageButton settingsBtn;
    private CaptureRequest.Builder mCaptureRequestBuilder;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = new CameraClass();
        textureView = (TextureView)findViewById(R.id.textureView);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        isPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED;


        startBtn = (ImageButton) findViewById(R.id.btnStart);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DrivingActivity.class);
                startActivity(intent);
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
        Toast.makeText(getApplicationContext(), "onRESUME", Toast.LENGTH_SHORT).show();
         startBackgroundThread();

        if(textureView.isAvailable()) {
            Toast.makeText(getApplicationContext(), "AVAILABLE", Toast.LENGTH_SHORT).show();
            camera.setupCamera(textureView.getWidth(), textureView.getHeight(), cameraManager, getWindowManager());
            transformImage(textureView.getWidth(), textureView.getHeight());
            camera.connectCamera(cameraManager, isPermission, backgroundHandlerThread);
            startPreview();
        } else {
            Toast.makeText(getApplicationContext(), "textureViewIs-NOT-AVAILABLE", Toast.LENGTH_SHORT).show();
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    protected void onPause() {
        camera.closeCamera();
        stopBackGroundThread();
        super.onPause();

    }



    public void onWindowFocusChanged(boolean hasFocas) {
        super.onWindowFocusChanged(hasFocas);
        View decorView = getWindow().getDecorView();
        if(hasFocas) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_FULLSCREEN
);
        }
    }

    public void startPreview() {
        //first convert texture view into surface view that the camera can understand.
        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        //surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface previewSurface = new Surface(surfaceTexture);

        try {
            mCaptureRequestBuilder = camera.getCameraDevice().createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(previewSurface);

            camera.getCameraDevice().createCaptureSession(Arrays.asList(previewSurface),
                    new CameraCaptureSession.StateCallback() {
                        public void onConfigured(CameraCaptureSession session) {
                           try {
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

    public void startBackgroundThread() {
        backgroundHandlerThread = new HandlerThread("MainActivity");
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

    public void transformImage(int width, int height){
        if(camera.getmPreviewSize() == null || textureView == null) {
            return;
        }

        Matrix matrix = new Matrix();
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        RectF textureRectF = new RectF(0,0,width,height);
        RectF previewRectF = new RectF(0,0,camera.getmPreviewSize().getHeight(), camera.getmPreviewSize().getWidth());
        float centerX = textureRectF.centerX();
        float centerY = textureRectF.centerY();
        if(rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            previewRectF.offset(centerX - previewRectF.centerX(), centerY - previewRectF.centerY());
            matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float)width / camera.getmPreviewSize().getWidth(),
                    (float)height / camera.getmPreviewSize().getHeight());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        textureView.setTransform(matrix);

    }

}