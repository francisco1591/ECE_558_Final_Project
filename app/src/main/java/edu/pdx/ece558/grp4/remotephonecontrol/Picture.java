package edu.pdx.ece558.grp4.remotephonecontrol;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Francisco on 6/11/2017.
 * This class adapted from http://www.vogella.com/tutorials/AndroidCamera/article.html
 */

public class Picture {
    private static final String TAG = "PictureClass";
    private Camera camera;
    private Context mContext;
    public String mFilename;
    private SurfaceTexture mSurfTx;
    private int mCamFacing;
    private String mFrontBack;

    // Constructor
    public Picture (Context context, boolean frontCamera) {
        mContext = context;
        mSurfTx = new SurfaceTexture(0);
        mCamFacing = frontCamera ? Camera.CameraInfo.CAMERA_FACING_FRONT
                : Camera.CameraInfo.CAMERA_FACING_BACK;
        mFrontBack = frontCamera ? "front" : "back";
    }

    // Check if camera found and open
    public void openCamera(int cameraID) {
        if (cameraID >=0)
            try {
                releaseCamera();
                camera= Camera.open(cameraID);
            } catch (Exception e) {
                Log.e(TAG,e.getMessage());
                e.printStackTrace();
            }
        else {
            String s = "Camera not found, ID: "+cameraID;
            Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
        }
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    // Take pictures with both front and back cameras
    public void takePic() {
        openCamera(findCamera());
        if (camera != null) {
            try {
                camera.setPreviewTexture(mSurfTx);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Camera.Parameters params = camera.getParameters();
            int quality = params.getJpegQuality();
            String s = mFrontBack + " jpeg quality : " + quality;
            Log.i(TAG,s);
            params.setJpegQuality(100);
            camera.setParameters(params);
            camera.startPreview();
            camera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {
                    if(success){
                        camera.takePicture(null, null, new Camera.PictureCallback() {
                            @Override
                            public void onPictureTaken(byte[] data, Camera camera) {
                                savePicture(data);
                                releaseCamera();
                                ((SMSListener)mContext).replyToSender("You picture request",mFilename);
                            }
                        });                    }
                }
            });

        }
    }

    public void savePicture(byte[] data) {
        File pictureFileDir = getDir();

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
            Log.d(TAG, "Can't create directory to save image.");
            Toast.makeText(mContext, "Can't create directory to save image.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        String photoFile = "picture_" + mFrontBack + ".jpg";
        mFilename = pictureFileDir.getPath() + File.separator + photoFile;
        File pictureFile = new File(mFilename);
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            Toast.makeText(mContext, "New Image saved:" + photoFile,
                    Toast.LENGTH_LONG).show();
        } catch (Exception error) {
            Log.d(TAG, "File" + mFilename + "not saved: " + error.getMessage());
            Toast.makeText(mContext, "Image could not be saved.",Toast.LENGTH_LONG).show();
        }
    }

    private File getDir() {
        File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "SpyOnMe");
    }

    private int findCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
           android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
           Camera.getCameraInfo(i, info);
            if (info.facing == mCamFacing) {
                Log.d(TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }
}
