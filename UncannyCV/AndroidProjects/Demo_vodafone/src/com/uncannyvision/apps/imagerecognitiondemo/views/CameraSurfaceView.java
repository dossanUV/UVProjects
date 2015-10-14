package com.uncannyvision.apps.imagerecognitiondemo.views;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import com.uncannyvision.apps.imagerecognitiondemo.MainActivity;
import com.uncannyvision.apps.imagerecognitiondemo.utils.CamParamComputer;
import com.uncannyvision.apps.imagerecognitiondemo.utils.CamUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback {
    private Camera mCamera = null;
	private IImageReceiver mFrameReceiver = null;
	private CamParamComputer mCameraParamComp = null;
	private Activity mActivity = null;
	private int mCameraID;
	private int mCameraOrientation;
	private Parameters mCamParams;

    public CameraSurfaceView(Activity activity, 
    		int cameraID, int cameraOrientation, Camera camera, 
    		IImageReceiver frameReceiver) {
        super(activity);
        mActivity = activity;
        mFrameReceiver = frameReceiver;
        
        mCamera = camera;
        mCameraID = cameraID;
        mCameraOrientation = cameraOrientation;
        
        mCameraParamComp = new CamParamComputer(mCamera.getParameters());

        getHolder().addCallback(this); // For surface lifetime callbacks        
        // Deprecated setting, but required on Android versions prior to 3.0
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        Log.d(MainActivity.LOG_TAG, "CameraPreview OK ");
    }

    @Override
	public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            CamUtils.alignCamAndDisplayOrientation(mActivity, mCameraID, mCamera);
        } catch (Exception e) {
            Log.d(MainActivity.LOG_TAG, "Error in surfaceCreated: " + e.getMessage());
        }
    }

    @Override
	public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(MainActivity.LOG_TAG, "surfaceDestroyed");
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
    }

    @Override
	public void surfaceChanged(SurfaceHolder holder, int format, int surfaceWidth, int surfaceHeight) {
        Log.d(MainActivity.LOG_TAG, "surfaceChanged " + " format=" + 
        		CamUtils.formatToName(format) + " w=" + surfaceWidth + " h=" + surfaceHeight);
    	
        if (holder.getSurface() == null){
			Log.d(MainActivity.LOG_TAG, "getSurface NULL");
			return;
        }

        try {
            // stop preview before making changes
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }
       
        
        // start preview with new settings
        try {
            mCamParams = mCamera.getParameters();        
            boolean paramsChanged = mCameraParamComp.computePreferred(mCamParams); 
        	if(paramsChanged)
        		mCamera.setParameters(mCamParams);
        	
			AtomicReference<Integer> x = new AtomicReference<Integer>(0);
			AtomicReference<Integer> y = new AtomicReference<Integer>(0);
			AtomicReference<Integer> h = new AtomicReference<Integer>(mCamParams.getPreviewSize().height);
			AtomicReference<Integer> w = new AtomicReference<Integer>(mCamParams.getPreviewSize().width);
			CamUtils.bestFitCenter(surfaceHeight, surfaceWidth, h, w, x, y);
        	
        	//FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(w.get(), h.get());
			
        	FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        	layoutParams.height = h.get();
        	layoutParams.width = w.get();
        	layoutParams.gravity = android.view.Gravity.CENTER; 
        	//layoutParams.setMargins(x.get(), y.get(), x.get(), y.get());
        	
        	
//        	mCamera.setDisplayOrientation(90);
        	
        	
        	
        	setLayoutParams(layoutParams);
        	//requestLayout();
        	
        	Log.d(MainActivity.LOG_TAG, "startPreview...");
            mCamera.startPreview();
            mCamera.setPreviewCallback(this); // For frame Callbacks            
        } catch (Exception e){
            Log.d(MainActivity.LOG_TAG, "Error starting camera preview: "
            		+ e.getMessage());
        }
    }
//    public void saveImage(String path, String dir, Bitmap image) {
//        try{
//            FileOutputStream fos = new FileOutputStream(path + dir);
//            BufferedOutputStream stream = new BufferedOutputStream(fos);
//            image.compress(CompressFormat.JPEG, 50, stream);
//            stream.flush();
//            stream.close(); 
//        }
//        catch(FileNotFoundException e) { 
//            e.printStackTrace();
//        }
//        catch(IOException e) {
//            e.printStackTrace();
//        }
//    }
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		
		Camera.Parameters parameters = camera.getParameters();

//		bitmap = Bitmap.createBitmap(data, width, height, Bitmap.Config.);
		
		long start = System.currentTimeMillis();
		mFrameReceiver.onImageFrame(data, 
				parameters.getPreviewSize().width, 
				parameters.getPreviewSize().height, 
				parameters.getPreviewFormat(),
				mCameraOrientation);
		
		long end = System.currentTimeMillis();
//		System.out.println("onPreviewFrame time was "+(end-start)+" ms.");
	
		
	}
	
//	public static void setCameraDisplayOrientation(UVCVDemo activity,
//	         int cameraId, android.hardware.Camera camera) {
//	     android.hardware.Camera.CameraInfo info =
//	             new android.hardware.Camera.CameraInfo();
//	     android.hardware.Camera.getCameraInfo(cameraId, info);
////	     int rotation = activity.getWindowManager().getDefaultDisplay()
////	             .getRotation();
//	     int degrees = 0;
////	     switch (rotation) {
////	         case CameraSurfaceView. .ROTATION_0: degrees = 0; break;
////	         case Surface.ROTATION_90: degrees = 90; break;
////	         case Surface.ROTATION_180: degrees = 180; break;
////	         case Surface.ROTATION_270: degrees = 270; break;
////	     }
//
//	     int result;
//	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//	         result = (info.orientation + degrees) % 360;
//	         result = (360 - result) % 360;  // compensate the mirror
//	     } else {  // back-facing
//	         result = (info.orientation - degrees + 360) % 360;
//	     }
//	     camera.setDisplayOrientation(result);
//	 }
}