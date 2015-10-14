package com.uncannyvision.uncannycv;

import com.uncannyvision.apps.imagerecognitiondemo.MainActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.ImageFormat;
import android.os.AsyncTask;
import android.util.Log;

public class UncannyCVCaller {
	
	private String logTag = "com.uncannyvision.uncannycv";
	private IUncannyCVCallbackReceiver cbReceiver = null;
	private Context context = null;

	private int ERROR = 1;
	private int SUCCESS = 0;
	private MyAsynchTask asynchTask = null;
	private enum Action {
		UNDEFINED,
		RUNALL_ASYNCH,
	};
	private static UncannyCVCaller me = null;
	private long timeSum = 0;
	private int frameCount = 0; 
	
	
	public static UncannyCVCaller getInstance(
			Context context,
			String logTag, 
			IUncannyCVCallbackReceiver cbReceiver) {
		if(null == context) {
			return null;
		}
		if(null == me)
			me = new UncannyCVCaller(context, logTag, cbReceiver);
		me.initializeArgs(context, logTag, cbReceiver);
		return me;
	}
	
	private UncannyCVCaller(Context context, String logTag, IUncannyCVCallbackReceiver cbReceiver) {
		initializeArgs(context, logTag, cbReceiver);
        System.loadLibrary("UncannyCVTester");
		UVInitialize(getAssetsFolderPath(), getAssetManager(), MainActivity.grayimagepath);
	}

	synchronized
	public void deinitialize() {
		UVDeInitialize();		
	}
	
	synchronized
	public int runAlgo(int algoID, byte[] dataIn, int width, int height, int format, int orientation, int[] dataOut) {
		long timeBefore = System.currentTimeMillis();
		cancelAsynchIfAny();
		if(dataIn == null) {
			sendLog("DataIn is NULL");
			return ERROR;
		}
		if(format != ImageFormat.NV21) {
			sendLog("Input Data has to be in ImageFormat.NV21 format");
			return ERROR;
		}
		UVRunAlgo(algoID, dataIn, width, height, format, orientation, dataOut);
		if(cbReceiver != null) {
			long timeGap = System.currentTimeMillis() - timeBefore;
			frameCount ++;
			timeSum += timeGap;
			if(frameCount == 10) {				
				cbReceiver.onTimeGap(timeSum/10);
				timeSum = 0;
				frameCount = 0;
			}
		}
		return SUCCESS;
	}
	
	synchronized
	public int startRunAllAlgosAsynch() {
		cancelAsynchIfAny();
		asynchTask = new MyAsynchTask(Action.RUNALL_ASYNCH);
		asynchTask.execute();
		return SUCCESS;
	}
	
	synchronized
	public void cancelAsynchIfAny() {
		if(null != asynchTask)
			asynchTask.cancel(true);
	}
	
	void initializeArgs(Context context, String logTag,
			IUncannyCVCallbackReceiver cbReceiver) {
		if(null != logTag) 
			setLogTag(logTag);
		setCbReceiver(cbReceiver);
		setContext(context);
	}

	public void logIt(String logMsg) {
		if(null != asynchTask) {
			// This will Come in Non UI Thread Context
			// So post it back to UI thread
			asynchTask.logIt(logMsg);
		}
		else {
			sendLog(logMsg);
		}
    }

	private void setLogTag(String logTag) {
		this.logTag = logTag;
	}

	private void setCbReceiver(IUncannyCVCallbackReceiver cbReceiver) {
		this.cbReceiver = cbReceiver;
	}

	private void setContext(Context context) {
		this.context = context;
	}

	private void sendLog(String logMsg) {
		String ndkLog = "UncannyCV:" + logMsg;
		Log.d(logTag, ndkLog);
		if(cbReceiver != null)
			cbReceiver.onLog(ndkLog);
	}
	
	private String getAssetsFolderPath() {
		return context.getPackageResourcePath();
//		+ "/" + "assets" + "/"
	}

	private AssetManager getAssetManager() {
		return context.getResources().getAssets();
	}

	private class MyAsynchTask extends AsyncTask<String, String, String> {
		private Action action = Action.UNDEFINED;

		public MyAsynchTask(Action action) {
			this.action = action;
		}

		@Override
		protected String doInBackground(String... arg0) {
			if(action == Action.RUNALL_ASYNCH)
				UVRunAll();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			sendLog("@@@@@@@@@ Asynch Task POST\n"); // Now we are on UI Thread
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			sendLog("@@@@@@@@@ Asynch Task PRE\n"); // Now we are on UI Thread
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			sendLog(values[0]); // Now we are on UI Thread
		}
		public void logIt(String logMsg) {
			publishProgress(logMsg);
		}
	}

	//****** Calls to NDK
	public native synchronized void UVInitialize(String packageResourcePath, AssetManager mgr, String path);
	public native synchronized void UVDeInitialize();
	public native synchronized void UVRunAll();
	public native synchronized void UVRunAlgo(int algoID, byte[] dataIn, int width, int height, int format, int orientation, int[] dataOut);
}
