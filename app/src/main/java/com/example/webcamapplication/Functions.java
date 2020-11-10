package com.example.webcamapplication;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.RequiresApi;

public class Functions {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static TextureView transformImage(int width, int height, int rotation, Size mPreviewSize, TextureView textureView){
        if(mPreviewSize == null || textureView == null) {
            return null;
        }
        //creating a new matrix (maritsa)
        Matrix matrix = new Matrix();
        RectF textureRectF = new RectF(0,0,width,height);
        RectF previewRectF = new RectF(0,0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = textureRectF.centerX();
        float centerY = textureRectF.centerY();
        //if switch to landscape mode
        if(rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            previewRectF.offset(centerX - previewRectF.centerX(), centerY - previewRectF.centerY());
            matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float)width / mPreviewSize.getWidth(),
                    (float)height / mPreviewSize.getHeight());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        textureView.setTransform(matrix);
        return textureView;
    }


}
