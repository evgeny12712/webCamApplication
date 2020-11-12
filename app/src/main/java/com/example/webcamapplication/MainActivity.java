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
        private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;
        private CameraClass camera;
        private CameraManager cameraManager;

        private TextureView textureView;
        private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                //setting up the camera - camera id, preview size , rotation
                camera.setupCamera(textureView.getWidth(), textureView.getHeight(), deviceOrientation, cameraManager);
                textureView = Functions.transformImage(textureView.getWidth(), textureView.getHeight(), deviceOrientation, camera.getPreviewSize(), textureView); //making sure that the camera does'nt reset when moving from landscape and portrait mode
                connectCamera(); //connecting to the camera, getting the camera service, asking for permission
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

        private CameraDevice cameraDevice;
        private CameraDevice.StateCallback cameraDeviceStateCallBack = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                  cameraDevice = camera;
                  startPreview(); //staring the preview
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                camera.close();
                cameraDevice = null;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
                camera.close();
                cameraDevice = null;

            }
        };

        private HandlerThread backgroundHandlerThread;
        private Handler backgroundHandler;

        private CaptureRequest.Builder mCaptureRequestBuilder;
        private int deviceOrientation;
        private ImageButton startBtn;
        private ImageButton galleryBtn;
        private ImageButton settingsBtn;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            camera = new CameraClass();
            textureView = (TextureView)findViewById(R.id.textureView);
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);


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
            startBackgroundThread();
            deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
            if(textureView.isAvailable()) {
                //setting up the camera - camera id, preview size , rotation
                camera.setupCamera(textureView.getWidth(), textureView.getHeight(), deviceOrientation, cameraManager);
                textureView = Functions.transformImage(textureView.getWidth(), textureView.getHeight(), deviceOrientation, camera.getPreviewSize(), textureView); //making sure that the camera does'nt reset when moving from landscape and portrait mode
                connectCamera(); //connecting to the camera, getting the camera service, asking for permission
                startPreview(); // starting the preview of the camera
            } else {
                textureView.setSurfaceTextureListener(surfaceTextureListener);
            }
        }

        @Override
        protected void onPause() {
            camera.closeCamera(cameraDevice);
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

    }