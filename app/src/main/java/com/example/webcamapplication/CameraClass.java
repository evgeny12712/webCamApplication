package com.example.webcamapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
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
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//import com.example.webcamapplication.DrivingActivity;
//import com.example.webcamapplication.SettingsActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import Gallery.GalleryActivity;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraClass extends AppCompatActivity {
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT = 1;
    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;

    private CameraDevice cameraDevice;
    private CameraDevice.StateCallback cameraDeviceStateCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            Toast.makeText(getApplicationContext(), "CAMERA FILMING", Toast.LENGTH_SHORT).show();
            checkWriteStoragePermission();
        }


        public void onDisconnected(CameraDevice camera) {
            camera.close();
            cameraDevice = null;
        }

        public void onError(CameraDevice camera, int error) {
            camera.close();
            cameraDevice = null;

        }
    };

    private HandlerThread backgroundHandlerThread;
    private Handler backgroundHandler;
    private String mCameraId;
    private Size mPreviewSize;
    private Size mVideoSize;
    private MediaRecorder mMediaRecorder;
    private int mTotalRotation;
    private CaptureRequest.Builder mCaptureRequestBuilder;

    private File mVideoFolder; //file path
    private String mVideoFileName; // file name

    private static SparseIntArray ORIENTATIONS = new SparseIntArray(); //converting surface orientation to real numbers

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);

    }

    public String getCameraId() {
        return mCameraId;
    }

    public Size getPreviewSize() {
        return mPreviewSize;
    }

    public Size getVideoSize() {
        return mVideoSize;
    }

    public int getTotaoRotation() {
        return mTotalRotation;
    }

    public int getNum() {
        return 11231231;
    }
    private static class CompareSizeByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() /
                    (long) rhs.getWidth() * rhs.getHeight()); //to get area we multiply the both sides width and height and then divide them
        }
    }

    public void setRequestCameraPermissionResult(int requestCode, String[] permission, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION_RESULT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Application will not run without camera permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setupCamera(int width, int height,int deviceOrientation, CameraManager cameraManager) {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                // iterating through all the cameras and checking if its facing front, if it is so continue
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                // creating a list of all the all output formats (and sizes respectively for that format) that are supported by a camera device.
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                // getting the rotation of the sensor
                mTotalRotation = sensorToDeviceRotation(cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION), deviceOrientation);
                boolean swapRotation = mTotalRotation == 90 || mTotalRotation == 270; //check is the phone is landscape mode
                int rotatedWidth = width;
                int rotatedHeight = height;
                // if the phone is landscape so switch between height and width
                if (swapRotation) {
                    rotatedWidth = height;
                    rotatedHeight = width;
                }
                // setup the preview size
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);

                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void connectCamera(CameraManager cameraManager, boolean isPermission) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isPermission) {
                    cameraManager.openCamera(mCameraId, cameraDeviceStateCallBack, backgroundHandler);

                }
                else {
                    //check if we should show a request for permission
                    if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        Toast.makeText(this, "video app required access to camera", Toast.LENGTH_SHORT).show();
                    }
                    // asking for the permission
                    requestPermissions(new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_RESULT);
                }
            } else {
                cameraManager.openCamera(mCameraId, cameraDeviceStateCallBack, backgroundHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

//    public void startPreview() {
//        //first convert texture view into surface view that the camera can understand.
//        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
//        //surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
//        Surface previewSurface = new Surface(surfaceTexture);
//
//        try {
//            mCaptureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//            mCaptureRequestBuilder.addTarget(previewSurface);
//
//            cameraDevice.createCaptureSession(Arrays.asList(previewSurface),
//                    new CameraCaptureSession.StateCallback() {
//                        public void onConfigured(CameraCaptureSession session) {
//                            try {
//                                session.setRepeatingRequest(mCaptureRequestBuilder.build(), null, backgroundHandler);
//                            } catch (CameraAccessException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
//                            Toast.makeText(getApplicationContext(), "unable to setup camera preview", Toast.LENGTH_SHORT).show();
//                        }
//                    }, null);
//
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }

    public void closeCamera() {
        if(cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    // ABOUT TO REMOVE
//    public void startBackgroundThread() {
//        backgroundHandlerThread = new HandlerThread("MainActivity");
//        backgroundHandlerThread.start();
//        backgroundHandler = new Handler(backgroundHandlerThread.getLooper());
//    }
//
//    private void stopBackGroundThread() {
//        backgroundHandlerThread.quitSafely();
//        try {
//            backgroundHandlerThread.join();
//            backgroundHandlerThread = null;
//            backgroundHandler = null;
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    //converting the camera sensor orientations to the device orientations
    private static int sensorToDeviceRotation(int cameraSensorOrientation, int deviceOrientation) {
        deviceOrientation = ORIENTATIONS.get(deviceOrientation); // getting the device orientation and converting it to real number.
        Log.i("orientations", "sensorOrientation " + cameraSensorOrientation);
        Log.i("device orientation", deviceOrientation + " deviceOrientation");
        Log.i("Both", (cameraSensorOrientation + deviceOrientation + 360) % 360 + " Both");
        return (cameraSensorOrientation + deviceOrientation + 360) % 360;
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
                try {
                    createVideoFileName();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "Permission successfully granted", Toast.LENGTH_SHORT).show();
            }  else {
                Toast.makeText(this, "App needs to save video to run", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void startRecord(TextureView textureView) {
        try {

            setupMediaRecorder(mMediaRecorder);
            //creating the surface on which we gonna display the preview while recording
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
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
        }
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height) {
        List<Size> bigEnough = new ArrayList<Size>();
        for(Size option : choices) {
            if(option.getHeight() == option.getWidth() * height / width &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        if(bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizeByArea());
        } else {
            return choices[0];
        }
    }



    /////------------RECORDING-FUNCTIONS------------/////

    private void createVideoFolder() {
        //getting the directory in which we will create the folder for our files
        File movieFile = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        //creating the folder that we want to save into
        Log.d("ASD", "createVideoFolder: " + movieFile);
        mVideoFolder = new File(movieFile, "webCamVideos");
        //checking if we don't have the folder yet
        if(!mVideoFolder.exists()) {
            //creating the folder
            mVideoFolder.mkdirs();
        }
    }

    private File createVideoFileName() throws IOException {
        //creating the time string
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //creating the file name
        String prepend = "VIDEO_" + timestamp + "_";
        //creating the actual file
        File videoFile = File.createTempFile(prepend, ".mp4", mVideoFolder);
        //setting the file inside the folder that we created on "createVideoFolder" func
        mVideoFileName = videoFile.getAbsolutePath();
        return videoFile;
    }

    private void checkWriteStoragePermission() {
        //checking if our version is greater then marshmallow
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //checking if we already got permission
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                try {
                    //create file to save video
                    createVideoFileName();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // startPreview();
//                startRecord();
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
            try {
                createVideoFileName();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            startRecord();
            mMediaRecorder.start();
//            mChronometer.setBase(SystemClock.elapsedRealtime());
//            mChronometer.setVisibility(View.VISIBLE);
//            mChronometer.start();
        }
    }

    public void setupMediaRecorder(MediaRecorder mMediaRecorder) {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(mVideoFileName);
        mMediaRecorder.setVideoEncodingBitRate(1000000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setOrientationHint(mTotalRotation);
        try {
            mMediaRecorder.prepare();
        } catch (IOException e | IllegalStateException) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "EXCAPTION IN SETUPMEDIA", Toast.LENGTH_SHORT).show();
        }
    }


}