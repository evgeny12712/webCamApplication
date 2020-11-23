package com.example.webcamapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Arrays;

public class CameraMainFragment extends Fragment {
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT = 1;
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
            Toast.makeText(getContext(), "CameraConnectionDONE!", Toast.LENGTH_LONG).show();
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

    private boolean isFirstTime;

    private View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        camera = new CameraClass();
        cameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        isFirstTime = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera_main, container, false);
        if (isFirstTime == true) {
            startBackgroundThread();
            textureView = (TextureView) v.findViewById(R.id.textureView);
            deviceOrientation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            if (textureView.isAvailable()) {
                //setting up the camera - camera id, preview size , rotation
                camera.setupCamera(textureView.getWidth(), textureView.getHeight(), deviceOrientation, cameraManager);
                textureView = Functions.transformImage(textureView.getWidth(), textureView.getHeight(), deviceOrientation, camera.getPreviewSize(), textureView); //making sure that the camera does'nt reset when moving from landscape and portrait mode
                connectCamera(); //connecting to the camera, getting the camera service, asking for permission
                startPreview(); // starting the preview of the camera
            } else {
                textureView.setSurfaceTextureListener(surfaceTextureListener);
            }
            isFirstTime = false;
        }

        // Inflate the layout for this fragment
        return v;
    }


    @Override
    public void onPause() {
        camera.closeCamera(cameraDevice);
        stopBackGroundThread();
        super.onPause();
    }

    public CameraDevice getCameraDevice() {
        return cameraDevice;
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
                            Toast.makeText(getContext(), "unable to setup camera preview", Toast.LENGTH_SHORT).show();
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
    protected void connectCamera() {
        try {
            //if our version of android is later version of android then marshmallow so we have to ask for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // check if we already got permission (for earlier activations)
                if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED ) {
                    cameraManager.openCamera(camera.getCameraId(), cameraDeviceStateCallBack, backgroundHandler); //open the connection to the camera
                } else {
                    //check if the user denied permission earlier, if he did so send him a toast
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        Toast.makeText(getActivity().getApplicationContext(), "video app required access to camera", Toast.LENGTH_SHORT).show();
                    }
                    if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                        Toast.makeText(getActivity().getApplicationContext(), "app needs to be able to record audio", Toast.LENGTH_SHORT).show();
                    }
                    if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Toast.makeText(getActivity().getApplicationContext(), "app needs to be able to save videos", Toast.LENGTH_SHORT).show();
                    }

                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO}, REQUEST_CAMERA_PERMISSION_RESULT);
                }
            } //if our version of android is earlier version of android then marshmallow so we can just open camera
            else {
                cameraManager.openCamera(camera.getCameraId(), cameraDeviceStateCallBack, backgroundHandler); //open the connection to the camera
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean shouldOpen = true;
        if (requestCode == REQUEST_CAMERA_PERMISSION_RESULT) {
           if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity().getApplicationContext(), "Application will not run without camera services", Toast.LENGTH_SHORT).show();
                shouldOpen = false;
            }
            if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Application need to be able to save data", Toast.LENGTH_SHORT).show();
                shouldOpen = false;
            }
            if (grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Application need to be able to record audio", Toast.LENGTH_SHORT).show();
                shouldOpen = false;
            }
            if(shouldOpen) {
                try {
                    if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    cameraManager.openCamera(camera.getCameraId(), cameraDeviceStateCallBack, backgroundHandler); //open the connection to the camera
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
        }
        }

    }
}
