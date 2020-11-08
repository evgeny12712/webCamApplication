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
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.webcamapplication.DrivingActivity;
import Settings.SettingsActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Gallery.GalleryActivity;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {
        private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;
        private TextureView textureView;
        private CameraClass camera;
        private CameraManager cameraManager;
        boolean isPermission;
        private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                //setting up the camera - camera id, preview size , rotation
                camera.setupCamera(textureView.getWidth(), textureView.getHeight(), getWindowManager().getDefaultDisplay().getRotation(), cameraManager);
                transformImage(textureView.getWidth(), textureView.getHeight()); //making sure that the camera does'nt reset when moving from landscape and portrait mode
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
        private Size mPreviewSize;
        private CaptureRequest.Builder mCaptureRequestBuilder;

        private ImageButton startBtn;
        private ImageButton galleryBtn;
        private ImageButton settingsBtn;

        private static SparseIntArray ORIENTATIONS = new SparseIntArray(); //all possible orientations on the surface
        static { //converting from 0/1/2/3 to read dagrees
            ORIENTATIONS.append(Surface.ROTATION_0, 0);
            ORIENTATIONS.append(Surface.ROTATION_90, 90);
            ORIENTATIONS.append(Surface.ROTATION_180, 180);
            ORIENTATIONS.append(Surface.ROTATION_270, 270);

        }

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

            if(textureView.isAvailable()) {
                //setting up the camera - camera id, preview size , rotation
                camera.setupCamera(textureView.getWidth(), textureView.getHeight(), getWindowManager().getDefaultDisplay().getRotation(), cameraManager);
                transformImage(textureView.getWidth(), textureView.getHeight()); //making sure that the camera does'nt reset when moving from landscape and portrait mode
                connectCamera(); //connecting to the camera, getting the camera service, asking for permission
                startPreview(); // starting the preview of the camera
            } else {
                textureView.setSurfaceTextureListener(surfaceTextureListener);
            }
        }

        @Override
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

        private void transformImage(int width, int height){
            if(mPreviewSize == null || textureView == null) {
                return;
            }

            Matrix matrix = new Matrix();
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            RectF textureRectF = new RectF(0,0,width,height);
            RectF previewRectF = new RectF(0,0,mPreviewSize.getHeight(), mPreviewSize.getWidth());
            float centerX = textureRectF.centerX();
            float centerY = textureRectF.centerY();
            if(rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
                previewRectF.offset(centerX - previewRectF.centerX(), centerY - previewRectF.centerY());
                matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL);
                float scale = Math.max((float)width / mPreviewSize.getWidth(),
                        (float)height / mPreviewSize.getHeight());
                matrix.postScale(scale, scale, centerX, centerY);
                matrix.postRotate(90 * (rotation - 2), centerX, centerY);
            }
            textureView.setTransform(matrix);
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

        private static class CompareSizeByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs)  {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() /
                    (long) rhs.getWidth() * rhs.getHeight()); //to get area we multiply the both sides width and height and then divide them
        }
    }

//        public void setRequestCameraPermissionResult(int requestCode, String[] permission, int[] grantResults) {
//            super.onRequestPermissionsResult(requestCode, permission, grantResults);
//            if(requestCode == REQUEST_CAMERA_PERMISSION_RESULT) {
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Application will not run without camera permission", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }


    //        private static Size chooseOptimalSize(Size[] choices, int width, int height) {
//            List<Size> bigEnough = new ArrayList<Size>();
//            for(Size option : choices) {
//                if(option.getHeight() == option.getWidth() * height / width &&
//                    option.getWidth() >= width && option.getHeight() >= height) {
//                    bigEnough.add(option);
//                }
//            }
//            if(bigEnough.size() > 0) {// if the list is not empty
//                return Collections.min(bigEnough, new CompareSizeByArea());
//            } else {
//                return choices[0];
//            }
//        }


}