package com.uncannyvision.apps.imagerecognitiondemo.views;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

import com.uncannyvision.apps.demo_vodafone_similarity_and_recognition.R;
import com.uncannyvision.apps.imagerecognitiondemo.deeplearning.*;
import com.uncannyvision.apps.imagerecognitiondemo.CameraActivity;
import com.uncannyvision.apps.imagerecognitiondemo.MainActivity;
import com.uncannyvision.apps.imagerecognitiondemo.utils.CamUtils;
//import com.uncannyvision.uncannycv.UncannyCVCaller;
import com.uncannyvision.uncannycv.IUncannyCVCallbackReceiver;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.ConditionVariable;
//import android.os.CountDownTimer;
//import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProcessedImageSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback, IImageReceiver {

	private int width = -1;
	private int height = -1;
	private int[] dataOut = null;
	private int[] indexOut = null;
	private int algoID = 0;
	private DrawerThread drawerThread = null;
	private ConditionVariable mFrameAvailableCondition;
	private SurfaceHolder mHolder;
	private boolean processAlgo = true;
	private String LOG_TAG = MainActivity.LOG_TAG;


	private ImageRecognition imgRecogObj = null;
	private ImageSimilarity imgSim = null;

	public void setProcessAlgo(boolean processAlgo) {

		this.processAlgo = processAlgo;
	}

	public ProcessedImageSurfaceView(Context context, int algoID,
			IUncannyCVCallbackReceiver uncannyCVCallbackReceiver) {

		super(context);
		this.algoID = algoID;
		mHolder = getHolder();
		mHolder.addCallback(this);
		mFrameAvailableCondition = new ConditionVariable();
		drawerThread = new DrawerThread();
	}

	@Override
	public synchronized void onImageFrame(byte[] dataIn, int width, int height,
			int format, int orientation) {

		if (processAlgo) {
			Log.d(LOG_TAG, "Input data len = " + dataIn.length + " w=" + width
					+ " h=" + height + " format=" + format);

			processImage(dataIn, width, height, format, orientation);

			mFrameAvailableCondition.open(); // Tell UI to draw the modified
												// frame

		}
	}

	/*
	 * private byte[] getBytesFromBitmap(Bitmap bitmap) { ByteArrayOutputStream
	 * stream = new ByteArrayOutputStream();
	 * bitmap.compress(CompressFormat.JPEG, 70, stream); return
	 * stream.toByteArray(); }
	 */

	private void processImage(byte[] dataIn, int width, int height, int format,
			int orientation) {
		Toast toastObj;
		// Create the output buffer
		if (dataOut == null || this.width != width || this.height != height) {
			dataOut = new int[width * height];
			this.width = width;
			this.height = height;
			indexOut = new int[100];
		}

		int result = 0;
		if (null == imgSim && CameraActivity.picFlag == 1) {
			System.loadLibrary("uv_deeplearning");

//			imgRecogObj = new ImageRecognition();			
			imgSim = new ImageSimilarity();
			
//				result = imgSim.initTest(protoFile, modelFile, getContext().getPackageResourcePath());
				
				Log.i(LOG_TAG, "Init DONE");

			if (result == -105)
				Toast.makeText(getContext(), "INFO : License expired", Toast.LENGTH_LONG).show();
			else if (result == -106)
				Toast.makeText(getContext(), "INFO : License verification failed", Toast.LENGTH_LONG)
						.show();
			else if (result < 0)
				Toast.makeText(getContext(), "INFO : Error loading data", Toast.LENGTH_LONG).show();
			Log.i(LOG_TAG, "Loaded ");
		}
										
		
		if (null == imgSim) {
			// Error
			Log.e(LOG_TAG, "Failed to construct UncannyCVCaller. Check Arguments");
		} 
		else if(CameraActivity.picFlag == 1)
		{
//			long timeBefore = System.currentTimeMillis();
//
//			//			result = imgRecogObj.tagImage(imagePath, detectedClassesWithProb);
//			long timeGap = System.currentTimeMillis() - timeBefore;
			
			String imagePath = "/storage/sdcard0/ImgSimilarity/testImages/";//input image path

			File folder = new File(imagePath);
			File[] listOfFiles = folder.listFiles();
			try{
				/// Remove filenames from db if they doesnt exist
				imgSim.dbUpdate(imagePath);
				
				/// Create file that outputs the similarity
				File simFile = new File(imgSim.simFileName);
				// if file doesnt exists, then create it
				if (!simFile.exists()) {
					simFile.createNewFile();
				}					
				FileWriter fw_simFile = new FileWriter(simFile.getAbsoluteFile(),true);
				BufferedWriter bw_simFile = new BufferedWriter(fw_simFile);

				/// Create file that tracks the image names
				File imgDbFile = new File(imgSim.imageDbName);
				// if file doesnt exists, then create it
				if (!imgDbFile.exists()) {
					imgDbFile.createNewFile();
				}		
								
				
				for (int varI=0; varI<listOfFiles.length; varI++)
				{
					if(imgSim.checkDbExistence(listOfFiles[varI].toString()))
						continue;

					FileWriter fw_imgDb = new FileWriter(imgDbFile.getAbsoluteFile(), true);	
					BufferedWriter bw_imgDb = new BufferedWriter(fw_imgDb);
					
					StringBuffer detectedClassesWithProb = new StringBuffer("");//json String result
					result = imgSim.similarityImage( listOfFiles[varI].toString(), detectedClassesWithProb);		
					if (result != -1) {			
						bw_simFile.write(detectedClassesWithProb.toString()+"\n");	
//							bw_simFile.append(listOfFiles[varI].toString()+"\n");
						bw_imgDb.write(listOfFiles[varI].toString()+"\n");	
						Log.d(LOG_TAG, "" + detectedClassesWithProb);
						CameraActivity.mTimeLabel.setText(detectedClassesWithProb);
					}		
					bw_imgDb.close();
				}
				bw_simFile.close();
				
			}catch (IOException e) {
				e.printStackTrace();
			}

			Toast.makeText(getContext(), "INFO : Found similarity images",
					Toast.LENGTH_LONG).show();

			CameraActivity.picFlag = 0;

			
			

			if (result == -2)
				CameraActivity.mTimeLabel.setText("");
			else if (result == -105) {
				CameraActivity.mTimeLabel.setText("");
				Toast.makeText(getContext(), "INFO : License expired", Toast.LENGTH_SHORT).show();
			}else if (result == -99) {
				CameraActivity.mTimeLabel.setText("");
				Toast.makeText(getContext(), "INFO : Image not found", Toast.LENGTH_SHORT).show();
			}
			else if (result == -106 || result == -107) {
				CameraActivity.mTimeLabel.setText("");
				Toast.makeText(getContext(), "INFO : License verification failed",
						Toast.LENGTH_SHORT).show();
			} else if (result == -108) {
				CameraActivity.mTimeLabel.setText("");
				Toast.makeText(getContext(), "INFO : Frame limit reached", Toast.LENGTH_SHORT).show();
			} else if (result < -1) {
				CameraActivity.mTimeLabel.setText("");
			} else if (result != -1) {
				CameraActivity.mTimeLabel.setText("Done!");
			}
		}
		
		
		if (null == imgRecogObj && CameraActivity.picFlag != 1) {
			// We have to construct it just once.

			System.loadLibrary("uv_deeplearning");

			String protoFile = "/storage/sdcard0/UVDeeplearning/models/deploy_uv_vodafone.prototxt";// for vodafone image gallery demo	
																									
			String modelFile = "/storage/sdcard0/UVDeeplearning/models/vodafone_objNscene_indiancars_landmarks.caffemodel";// for vodafone image gallery demo
																															

			imgRecogObj = new ImageRecognition();
			File file1 = new File(protoFile);
			File file2 = new File(modelFile);

			if (!(file1.exists() && file2.exists()))// && file3.exists() ))
				Toast.makeText(getContext(),
						"INFO : Model files does not exist", Toast.LENGTH_LONG)
						.show();
			else {
				result = imgRecogObj.initTest(protoFile, modelFile,
						getContext().getPackageResourcePath());
				Log.i(LOG_TAG, "Init DONE");
			}
			if (result == -105)
				Toast.makeText(getContext(), "INFO : License expired",
						Toast.LENGTH_LONG).show();
			else if (result == -106)
				Toast.makeText(getContext(),
						"INFO : License verification failed", Toast.LENGTH_LONG)
						.show();
			else if (result < 0)
				Toast.makeText(getContext(), "INFO : Error loading data",
						Toast.LENGTH_LONG).show();
			Log.i(LOG_TAG, "Loaded ");
		}
		
		
		if (null == imgRecogObj) {
			// Error
			Log.e(LOG_TAG,
					"Failed to construct UncannyCVCaller. Check Arguments");
		} 
		else if(CameraActivity.picFlag == 2||CameraActivity.picFlag == 3||CameraActivity.picFlag == 4)
		{
//			long timeBefore = System.currentTimeMillis();			

			String imagePath = "/storage/sdcard0/UVDeeplearning/test/test.jpg";//input image path
			StringBuffer detectedClassesWithProb = new StringBuffer("");//json String result

			switch(CameraActivity.picFlag)
			{
			
				case 2:
					result = imgRecogObj.tagImage(imagePath, detectedClassesWithProb);
					break;
				
				case 3:
					result = imgRecogObj.tagCars(imagePath, detectedClassesWithProb);
					break;
				
				case 4:
					result = imgRecogObj.tagLandmarks(imagePath, detectedClassesWithProb);
					break;
				
				default:
					break;
				
			}
			
//			long timeGap = System.currentTimeMillis() - timeBefore;

			CameraActivity.picFlag = 0;


			if (result == -2)
				CameraActivity.mTimeLabel.setText("");
			else if (result == -105) {
				CameraActivity.mTimeLabel.setText("");
				Toast.makeText(getContext(), "INFO : License expired",
						Toast.LENGTH_SHORT).show();
			}else if (result == -99) {
				CameraActivity.mTimeLabel.setText("");
				Toast.makeText(getContext(), "INFO : Image not found",
						Toast.LENGTH_SHORT).show();
			}
			else if (result == -106 || result == -107) {
				CameraActivity.mTimeLabel.setText("");
				Toast.makeText(getContext(),
						"INFO : License verification failed",
						Toast.LENGTH_SHORT).show();
			} else if (result == -108) {
				CameraActivity.mTimeLabel.setText("");
				Toast.makeText(getContext(), "INFO : Frame limit reached",
						Toast.LENGTH_SHORT).show();
			} else if (result < -1) {
				CameraActivity.mTimeLabel.setText("");
			} else if (result != -1) {
				
					Log.d(LOG_TAG, "" + detectedClassesWithProb);
					CameraActivity.mTimeLabel.setText(detectedClassesWithProb+"\n\nDone!");
	
			}
		}

	}

	protected synchronized void customDraw(Canvas canvas) {

		if (dataOut != null && canvas != null) {
			Log.d(MainActivity.LOG_TAG, "drawBitmap..." + " Canvas: H="
					+ canvas.getHeight() + " W=" + canvas.getWidth()
					+ " Image : H=" + height + " W=" + width);
			Bitmap bitmap = null;
			AtomicReference<Integer> x = new AtomicReference<Integer>(0);
			AtomicReference<Integer> y = new AtomicReference<Integer>(0);
			AtomicReference<Integer> h = new AtomicReference<Integer>(height);
			AtomicReference<Integer> w = new AtomicReference<Integer>(width);
			CamUtils.bestFitCenter(canvas.getHeight(), canvas.getWidth(), h, w,
					x, y);

			int canvasWidth = canvas.getWidth();
			int canvasHeight = canvas.getHeight();
			try {
				try {
					// bitmap.copyPixelsFromBuffer(IntBuffer.wrap(dataOut));
					bitmap = Bitmap.createBitmap(dataOut, width, height,
							Bitmap.Config.ARGB_8888);
				} catch (Exception ee) {
					System.out.println("Create Bitmap error");
				}
				Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
				Rect canv = new Rect((int) x.get(), (int) y.get(), canvasWidth,
						canvasHeight);
				canvas.drawBitmap(bitmap, src, canv, null);
				// canvas.drawBitmap(dataOut, 0, width,
				// x.get(), y.get(), w.get(), h.get(), false, null);

				// bitmap.recycle();
			} catch (Exception e) {
				System.out.println(e + "");
			}
			Log.d(MainActivity.LOG_TAG, "drawBitmap OK");
		}
	}

	public void setAlgoID(int algoID) {

		this.algoID = algoID;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		drawerThread.setRunning(true);
		drawerThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		boolean retry = true;
		drawerThread.setRunning(false);
		mFrameAvailableCondition.open(); // So that the thread doesn't wait
											// indefinitely
		while (retry) {
			try {
				drawerThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	class DrawerThread extends Thread {
		private boolean mRunning = false;

		public DrawerThread() {

		}

		public void setRunning(boolean running) {

			mRunning = running;
		}

		@Override
		public void run() {

			Canvas c;
			while (mRunning) {
				mFrameAvailableCondition.block();
				mFrameAvailableCondition.close();
				c = null;
				try {
					c = mHolder.lockCanvas(null);
					synchronized (mHolder) {
						customDraw(c);
					}
				} finally {
					if (c != null) {
						mHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
	}
}
