package edu.pdx.ece558.grp4.remotephonecontrol;

/**
 * Created by Francisco on 6/11/2017.
 * This class adapted from http://www.vogella.com/tutorials/AndroidCamera/article.html
 */

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Picture {
    private static final String TAG = "PictureClass";
    private Camera camera;
    private Context mContext;
    public String mFilename;
    private SurfaceTexture mSurfTx;
    private int mCamFacing;
    private String mFrontBack;
    private boolean mFlash;
    private int mRotation;

    // Constructor
    public Picture (Context context, boolean frontCamera) {
        mContext = context;
        mSurfTx = new SurfaceTexture(0);
        mCamFacing = frontCamera ? Camera.CameraInfo.CAMERA_FACING_FRONT
                : Camera.CameraInfo.CAMERA_FACING_BACK;
        mFrontBack = frontCamera ? "front" : "back";
    }

    // Setter
    public void setFlash(boolean flash) {
        mFlash = flash;
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
        getCameraRotation(cameraID, camera );
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
            params.setPreviewSize(1280, 720);
            params.setPictureSize(1280, 720);
           // mRotation = 270;
            params.setRotation(mRotation);
            List<String> flashModes = params.getSupportedFlashModes();
            if (mFlash && flashModes.contains(android.hardware.Camera.Parameters.FLASH_MODE_ON))
            {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            } else {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
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
                                ((SMSListener)mContext).replyToSender("Your picture request",mFilename);
                            }
                        });                    }
                }
            });

        }
    }

    public void getCameraRotation( int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        WindowManager windowService = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowService.getDefaultDisplay().getRotation();

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (mFrontBack.equals("front")) {
            result = (info.orientation + degrees) % 360;
            //result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        //camera.setDisplayOrientation(0);
        mRotation = result;
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
