package CameraAndSupport;

import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

//import Driving.DrivingActivity;
//import com.example.webcamapplication.SettingsActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import Gallery.General.Item;
import Gallery.General.Items;
import MainWindow.MainActivity;

import static Gallery.General.Items.loadFiles;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraClass extends AppCompatActivity {
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT = 1;
    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;
    private static final String TAG = "CameraClass";

    private CameraDevice cameraDevice;

    private String mCameraId;
    private Size mPreviewSize;
    private Size mImageSize;
    private ImageReader mImageReader;
    private MediaRecorder mMediaRecorder;
    private static int mTotalRotation;
    private CaptureRequest.Builder mCaptureRequestBuilder;

    private File mVideoFolder; //file path
    private String mVideoFileName; // file name
    private static File mImageFolder; //file path
    private String mImageFileName; // file name
    private File mVideoFile; //video file

    private boolean isLandscape;

    private CameraCharacteristics cameraCharacteristics;
    //surface orientations to real world numbers
    private static SparseIntArray ORIENTATIONS = new SparseIntArray(); //converting surface orientation to real numbers
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);

    }

    private static boolean isSoundEnabled;

    public String getCameraId() {
        return mCameraId;
    }
    public Size getPreviewSize() {
        return mPreviewSize;
    }
    public String getmImageFileName() { return mImageFileName; }
    public static int getmTotalRotation() {return mTotalRotation;}
    public ImageReader getmImageReader() {
        return mImageReader;
    }
    public boolean getIsLandscape() {
        return isLandscape;
    }
    public String getFileName() { return mVideoFileName; }
    public static boolean getIsSound() {
        return isSoundEnabled;
    }
    public File getmVideoFile() { return mVideoFile; }
    private static class CompareSizeByArea implements Comparator<Size> {
        //class to compare different resolutions by the preview
        @Override
        public int compare(Size lhs, Size rhs) {
            //return -1 if the result is negative, 0 if the result is 0, 1 is the result is positive
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() /
                    (long) rhs.getWidth() * rhs.getHeight()); //to get area we multiply the both sides width and height and then divide them
        }
    }

    public void setupCamera(int width, int height, int deviceOrientation, CameraManager cameraManager) {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                // iterating through all the cameras and checking if its facing front, if it is so continue
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                // creating a list of all the all output formats (and sizes respectively for that format) that are supported by a camera device.
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                // getting the rotation of the sensor
                mTotalRotation = sensorToDeviceRotation(cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION), deviceOrientation);
                isLandscape = mTotalRotation == 180 || mTotalRotation == 270 || mTotalRotation == 0; //check is the phone is landscape mode
                int rotatedWidth = width;
                int rotatedHeight = height;
                // if the phone is landscape so switch between height and width
                if (isLandscape) {
                    rotatedWidth = height;
                    rotatedHeight = width;
                }
                Log.d("PreviewSize", "Width : " + rotatedWidth +  " Height : " + rotatedHeight);

                // setup the preview size
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);

                mImageSize = chooseOptimalSize(map.getOutputSizes(ImageFormat.JPEG), rotatedWidth, rotatedHeight);
                mImageReader = ImageReader.newInstance(mImageSize.getWidth(), mImageSize.getHeight(), ImageFormat.JPEG, 1);
                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void closeCamera(CameraDevice cameraDevice) {
        if(cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    //converting the camera sensor orientations to the device orientations
    private static int sensorToDeviceRotation(int cameraSensorOrientation, int deviceOrientation) {
        deviceOrientation = ORIENTATIONS.get(deviceOrientation); // getting the device orientation and converting it to real number.
        return (cameraSensorOrientation + deviceOrientation + 360) % 360;
    }


    private static Size chooseOptimalSize(Size[] choices, int width, int height) {
        /***
         * choices - all the resolutions from the camera sensor
         * width \ height - the width and height of our surface
         *
         * the function will match the width \ height to the surface resolution to get a preview size
         */
        List<Size> bigEnough = new ArrayList<Size>();
        for(Size option : choices) {
            //checking if the the optional surface size is big enough to show the resolution of the
            // camera sensor.
            if(option.getHeight() == option.getWidth() * height / width &&
                    option.getWidth() >= width &&
                    option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        //return the minimum size that matches to the options
        if(bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizeByArea());
        } else {
            return choices[0];
        }
    }

    /////------------RECORDING-FUNCTIONS------------/////

    public void createVideoFolder(File movieFile) {
        mVideoFolder = movieFile;
    }

    public File createVideoFileName() throws IOException {
        File videoFile;
        //creating the time string
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //creating the file name
        String prepend = "VIDEO_" + timestamp + "_";
        //creating the actual file

        videoFile = File.createTempFile(prepend, ".mp4", mVideoFolder);

        //setting the file inside the folder that we created on "createVideoFolder" func
        mVideoFileName = videoFile.getAbsolutePath();
        Log.d(TAG, "createVideoFileName: CREATED FILE!");
        //if we reached the max files that we can save so delete the oldest file
        if(mVideoFolder.listFiles().length > Items.getMaxTempFiles()) {
            Items.deleteOldestItem();
            Log.d(TAG, "createVideoFileName: FILE DELETED!!");
        }
        return videoFile;
    }

    public void createImageFolder(File imageFile) {
        mImageFolder = imageFile;
    }

    public File createImageFileName() throws IOException {
        //creating the time string
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //creating the file name
        String prepend = "IMAGE_" + timestamp + "_";
        //creating the actual file
        File imageFile = File.createTempFile(prepend, ".jpg", mImageFolder);
        //setting the file inside the folder that we created on "createVideoFolder" func
        mImageFileName = imageFile.getAbsolutePath();
        return imageFile;
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
                    mVideoFile = createVideoFileName();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "Permission successfully granted", Toast.LENGTH_SHORT).show();
            }  else {
                Toast.makeText(this, "App needs to save video to run", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public MediaRecorder setupMediaRecorder() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        if(isSoundEnabled) {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        }
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(mVideoFileName);
        Log.d(TAG, "setupMediaRecorder: " + mVideoFileName);
        mMediaRecorder.setVideoEncodingBitRate(3000000);
        mMediaRecorder.setVideoFrameRate(16);
        mMediaRecorder.setVideoSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //mMediaRecorder.setMaxDuration(10*1000);
        if(isSoundEnabled) {
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        }
        mMediaRecorder.setOrientationHint(cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION));
        try {
            mMediaRecorder.prepare();
            Log.d(TAG, "prepared");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "not-prepared");
        }
        return mMediaRecorder;
    }

    public static void setIsSoundEnabled(Boolean isEnabled){
        isSoundEnabled = isEnabled;
    }

    public static class ImageSaver implements Runnable {
        private final Image mImage;
        private static CameraClass camera;

        public ImageSaver(Image image, CameraClass camera) {
            mImage =  image;
            this.camera = camera;
        }

        public static CameraClass getCamera() {
            return camera;
        }

        @Override
        public void run() {
            // getting our image into a ByteBuffer
            ByteBuffer byteBuffer = mImage.getPlanes()[0].getBuffer();
            // making a bytes array at the size of the byteBuffer.
            byte[] bytes = new byte[byteBuffer.remaining()];
            // initializing the bytes array with the bytes of the image
            byteBuffer.get(bytes);

            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(camera.getmImageFileName());
                fileOutputStream.write(bytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();

                if(fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}