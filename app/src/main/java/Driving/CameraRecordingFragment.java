package Driving;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import CameraAndSupport.CameraClass;
import CameraAndSupport.Functions;
import Gallery.General.Item;
import Gallery.General.Items;
import MainWindow.MainActivity;

import com.example.webcamapplication.R;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static Gallery.General.Items.loadFiles;

public class CameraRecordingFragment extends Fragment implements Runnable {
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT = 1;
    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;

    //states for image capture
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAIT_LOCK = 1;

    //to check if its the first time that we coming into "onCreateView" state
    private static boolean isFirstTime;

    private int mCaptureState = STATE_PREVIEW;

    //the surface on which we show the camera preview
    private TextureView textureView;
    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //setting up the camera - camera id, preview size , rotation
            camera.setupCamera(textureView.getWidth(), textureView.getHeight(), deviceOrientation, cameraManager);
            mImageReader = camera.getmImageReader();
            mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, backgroundHandler);

            //making sure that the camera does'nt reset when moving from landscape and portrait mode
            textureView = Functions.transformImage(textureView.getWidth(), textureView.getHeight(), deviceOrientation, camera.getPreviewSize(), textureView);
            mMediaRecorder = camera.setupMediaRecorder();
            Toast.makeText(getContext(), "" + camera.getmTotalRotation(), Toast.LENGTH_SHORT).show();
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
            //making the surface for recording
            startRecord();
            //starting recording
            mMediaRecorder.start();
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

    private static File movieFolder;
    private File imageFolder;

    private HandlerThread backgroundHandlerThread;
    private Handler backgroundHandler;

    private MediaRecorder mMediaRecorder;
    private ImageReader mImageReader;
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new
            ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    backgroundHandler.post(new CameraClass.ImageSaver(reader.acquireLatestImage(), camera));
                }
            };

    private CaptureRequest.Builder mCaptureRequestBuilder;
    private CameraCaptureSession mRecordCaptureSession;
    private CameraCaptureSession.CaptureCallback mRecordCaptureCallback = new
            CameraCaptureSession.CaptureCallback() {
                private void process(CaptureResult captureResult) {
                    switch (mCaptureState) {
                        case STATE_PREVIEW:
                            // Do nothing
                            break;
                        case STATE_WAIT_LOCK:
                            mCaptureState = STATE_PREVIEW;
                            Integer afState = captureResult.get(CaptureResult.CONTROL_AF_STATE);
                            Log.d(TAG, "process: " + afState);
                            if (afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED ||
                                    afState == CaptureRequest.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {
                                Log.d(TAG, "startStillCaptureRequest: JUST_B4_CAPTURE");
                                startStillCaptureRequest();
                            }
                            break;
                    }
                }

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);

                    process(result);
                }
            };

    private CameraManager cameraManager;
    private int deviceOrientation;

    private CameraClass camera;

    private View v;

    private Thread thread;


    // detects if the video was taken on landscape mode
    private Boolean isLandscape;

    NotificationManagerCompat notificationManager;
    private static final String TAG = "CameraRecordingFragment";

    //----LIFE CYCLE----//
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirstTime = true;
        if (getActivity().getIntent().hasExtra("isFirstTime")) {
            isFirstTime = getActivity().getIntent().getBooleanExtra("isFirstTime", true);
        }
        mMediaRecorder = new MediaRecorder();
        cameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        camera = new CameraClass();
        movieFolder = getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        imageFolder = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        isLandscape = getActivity().getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_90
                || getActivity().getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_270;
        notificationManager = NotificationManagerCompat.from(getActivity());
        thread = new Thread(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_recording_camera, container, false);
        Toast.makeText(getActivity().getApplicationContext(), "isFirstTime  " + isFirstTime, Toast.LENGTH_LONG).show();
        if (isFirstTime == true) {
            startBackgroundThread();
            textureView = (TextureView) v.findViewById(R.id.textureView);

            //creating folder to save videos
            camera.createVideoFolder(movieFolder);
            camera.createImageFolder(imageFolder);
            try {
                camera.createVideoFileName();
            } catch (IOException e) {
                e.printStackTrace();
            }
            deviceOrientation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            if (textureView.isAvailable()) {
                //setting up the camera - camera id, preview size , rotation
                camera.setupCamera(textureView.getWidth(), textureView.getHeight(), deviceOrientation, cameraManager);
                //making sure that the camera does'nt reset when moving from landscape and portrait mode
                textureView = Functions.transformImage(textureView.getWidth(), textureView.getHeight(), deviceOrientation, camera.getPreviewSize(), textureView);
                mMediaRecorder = camera.setupMediaRecorder();
                mImageReader = camera.getmImageReader();
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, backgroundHandler);
                //Toast.makeText(getContext(), "" + camera.getmTotalRotation(), Toast.LENGTH_SHORT).show();
                connectCamera();
            } else {
                textureView.setSurfaceTextureListener(surfaceTextureListener);
            }
            isFirstTime = false;
        }

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: \"CameraRecordingFragment : DESTROYED!\"");
        stopBackGroundThread();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // check the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            deviceOrientation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            textureView = Functions.transformImage(textureView.getHeight(), textureView.getWidth(), deviceOrientation, camera.getPreviewSize(), textureView);
            //Log.d("Landscape", "Width : " + textureView.getWidth() +  " Height : " + textureView.getHeight());
            camera.setupCamera(textureView.getWidth(), textureView.getHeight(), deviceOrientation, cameraManager);
            isLandscape = true;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            deviceOrientation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            textureView = Functions.transformImage(textureView.getHeight(), textureView.getWidth(), deviceOrientation, camera.getPreviewSize(), textureView);
            //Log.d("Portrait", "Width : " + textureView.getWidth() +  " Height : " + textureView.getHeight());

            camera.setupCamera(textureView.getWidth(), textureView.getHeight(), deviceOrientation, cameraManager);

            isLandscape = false;
        }
    }


    public void setIsFirstTime(boolean isFirstTime) {
        this.isFirstTime = isFirstTime;
    }

    //----GETTERS----//
    protected MediaRecorder getMediaRecorder() {
        return this.mMediaRecorder;
    }

    protected CameraClass getCamera() {
        return camera;
    }

    protected CameraDevice getCameraDevice() {
        return cameraDevice;
    }

    protected File getMovieFolder() {
        return movieFolder;
    }

    protected void stopMediaRecorder(boolean isFromOnDestroy) {
        mMediaRecorder.stop();
        mMediaRecorder.reset();    // set state to idle
        mMediaRecorder.release();
        mMediaRecorder = null;
        if(isFromOnDestroy == false) {
            loadFiles(getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES), MainActivity.fileTypes[0], getContext());
            Log.d(TAG, " cameraFragment : temporaryFilesSize : " + Items.getTemporaryFiles().size());
        }
    }

    protected void startMediaRecorder() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder = camera.setupMediaRecorder();
        mMediaRecorder.start();
    }

    //connecting to the camera, getting the camera service, asking for permission
    protected void connectCamera() {
        /* checking for permissions and opening camera*/
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) ==
                                PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_GRANTED) {
                    cameraManager.openCamera(camera.getCameraId(), cameraDeviceStateCallBack, backgroundHandler); //open the connection to the camera
                } else {
                    //check if we should show a request for permission
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        Toast.makeText(getContext(), "video app required access to camera", Toast.LENGTH_SHORT).show();
                    }
                    if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                        Toast.makeText(getContext(), "video app required access to record audio", Toast.LENGTH_SHORT).show();
                    }
                    if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Toast.makeText(getContext(), "video app needs to be able to write to storage", Toast.LENGTH_SHORT).show();
                    }
                    // asking for the permission
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO,
                    }, REQUEST_CAMERA_PERMISSION_RESULT);
                }
            } else {
                cameraManager.openCamera(camera.getCameraId(), cameraDeviceStateCallBack, backgroundHandler); //open the connection to the camera
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //give camera permission to preview and save files
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION_RESULT) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Application will not run without camera permission", Toast.LENGTH_SHORT).show();
            }
            if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Application will not have audio on record", Toast.LENGTH_SHORT).show();
            }
            if (grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Application will not run without being able to save to storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void startRecord() {
        if (cameraDevice != null) {
            try {
                //creating the surface on which we gonna display the preview while recording
                SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
                surfaceTexture.setDefaultBufferSize(camera.getPreviewSize().getWidth(), camera.getPreviewSize().getHeight());
                Surface previewSurface = new Surface(surfaceTexture);
                Surface recordSurface = mMediaRecorder.getSurface();
                mCaptureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                //making preview
                mCaptureRequestBuilder.addTarget(previewSurface);
                //recording
                mCaptureRequestBuilder.addTarget(recordSurface);

                cameraDevice.createCaptureSession(Arrays.asList(previewSurface, recordSurface, mImageReader.getSurface()),
                        new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession session) {
                                mRecordCaptureSession = session;
                                try {
                                    mRecordCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(),
                                            null, backgroundHandler);
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
                Toast.makeText(getContext(), "ILLEGAL STATE", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                Toast.makeText(getContext(), "ILLEGAL ARGUMENT", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    //----BACKGROUND THREAD----//
    protected void startBackgroundThread() {
        backgroundHandlerThread = new HandlerThread("DrivingActivity");
        backgroundHandlerThread.start();
        backgroundHandler = new Handler(backgroundHandlerThread.getLooper());
    }

    protected void stopBackGroundThread() {
        backgroundHandlerThread.quitSafely();
        try {
            backgroundHandlerThread.join();
            backgroundHandlerThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //----IMAGE TAKING----//
    protected void startStillCaptureRequest() {
        try {
            mCaptureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_VIDEO_SNAPSHOT);
            mCaptureRequestBuilder.addTarget(mImageReader.getSurface());
            mCaptureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, camera.getmTotalRotation());

            CameraCaptureSession.CaptureCallback stillCaptureCallback = new
                    CameraCaptureSession.CaptureCallback() {
                        @Override
                        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                            super.onCaptureStarted(session, request, timestamp, frameNumber);
                            try {
                                camera.createImageFileName();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
            mRecordCaptureSession.capture(mCaptureRequestBuilder.build(), stillCaptureCallback, null);
            Toast.makeText(this.getActivity().getApplicationContext(), "IMAGE TAKEN!", Toast.LENGTH_SHORT).show();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void lockFocus() {
        mCaptureState = STATE_WAIT_LOCK;
        mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);
        try {
            mRecordCaptureSession.capture(mCaptureRequestBuilder.build(), mRecordCaptureCallback, backgroundHandler);
            Log.d(TAG, "in-LOCK_FOCUS");
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
                Log.d(TAG, "" + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}