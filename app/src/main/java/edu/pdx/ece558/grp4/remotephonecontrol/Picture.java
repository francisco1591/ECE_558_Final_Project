package edu.pdx.ece558.grp4.remotephonecontrol;

import android.content.Context;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Francisco on 6/11/2017.
 * This class adapted from http://www.vogella.com/tutorials/AndroidCamera/article.html
 */

//    private static final String REQUEST_CAMERA_PERMISSION = 4;
//    private static final String REQUEST_EXT_STORAGE_PERMISSION = 5;
//
//    protected void getCameraPermission(){
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//        != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA_PERMISSION);
//        }
//    }
//
//    protected void getExtStoragePermission(){
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        != PackageManager.PERMISSION_GRANTED) {
//        ActivityCompat.requestPermissions(this,
//        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_EXT_STORAGE_PERMISSION);
//        }
//        }
//
// // add the following to the onRequestPermissionResult method
//        case REQUEST_CAMERA_PERMISSION: {
//            if (grantResults.length > 0
//        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(getApplicationContext(),"Camera permission granted.",
//                Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(getApplicationContext(),
//            "Camera permission required, please try again.", Toast.LENGTH_LONG).show();
//        return;
//        }
//        }
//
//        case REQUEST_EXT_STORAGE_PERMISSION: {
//        if (grantResults.length > 0
//        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//        Toast.makeText(getApplicationContext(),"External storage permission granted.",
//        Toast.LENGTH_LONG).show();
//        } else {
//        Toast.makeText(getApplicationContext(),
//        "External storage permission required, please try again.", Toast.LENGTH_LONG).show();
//        return;
//        }
//        }

public class Picture {
    private static final String TAG = "PictureClass";
    private Camera camera;
    private int FrontCameraId = 0;
    private int BackCameraId = 0;
    private Context mContext;
    public String mFilename;

    // Constructor
    public Picture (Context context) {
        mContext = context;
    }

    // Check if camera found and open
    public void openCamera(int cameraID) {
        if (cameraID >=0)
            camera = Camera.open(cameraID);
        else {
            String s = "Camera not found, ID: "+cameraID;
            Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
        }
    }

    // Take pictures with both front and back cameras
    public void takePics() {
        openCamera(findFrontCamera());
        if (camera != null) {
            camera.startPreview();
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    savePicture(data,"front");
                    camera.release();
                   // ((SMSListener)mContext).sendPicture(mFilename);
                }
            });
        }
        openCamera(findBackCamera());
        if (camera != null) {
            camera.startPreview();
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    savePicture(data,"back");
                    camera.release();
                   // ((SMSListener)mContext).sendPicture(mFilename);
                }
            });
        }
    }

    public void savePicture(byte[] data, String FrontBack) {
        File pictureFileDir = getDir();

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
            Log.d(TAG, "Can't create directory to save image.");
            Toast.makeText(mContext, "Can't create directory to save image.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        String photoFile = "picture_" + FrontBack + ".jpg";
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

    private int findFrontCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
           Camera.CameraInfo info = new Camera.CameraInfo();
           Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(TAG, "Front camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    private int findBackCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = android.hardware.Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
           Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.d(TAG, "Back camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }
}
