package com.uncannyvision.apps.imagerecognitiondemo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.uncannyvision.apps.imagerecognitiondemo.views.CameraSurfaceView;
import com.uncannyvision.apps.imagerecognitiondemo.views.ProcessedImageSurfaceView;
import com.uncannyvision.apps.demo_vodafone_similarity_and_recognition.R;
import com.uncannyvision.uncannycv.IUncannyCVCallbackReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CameraActivity extends SherlockActivity implements ActionBar.OnNavigationListener,
		IUncannyCVCallbackReceiver, SharedPreferences.OnSharedPreferenceChangeListener
{

	private static int mCameraID = 0;
	private static int mCameraOrientation;
	private Camera mCamera = null;
	private CameraSurfaceView mPreview = null;
	private FrameLayout mPreviewLayout = null;
	private ProcessedImageSurfaceView mProcessedImageView = null;
	private FrameLayout mProcessedImageLayout = null;
	private TextView mAlgoNameTextView = null;
	private String[] mAlgos = null;
	private String[] mAlgoDescriptions = null;
	private int mSelectedAlgoID = 0;
	public static TextView mTimeLabel;

	public static int picFlag = 0;
	public static String[] IMAGENET_CLASSES;
	public AssetManager asmngr = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_layout);

		mPreviewLayout = (FrameLayout) findViewById(R.id.camera_preview);
		mPreviewLayout.setVisibility(View.INVISIBLE);//hide camera preview

		mProcessedImageLayout = (FrameLayout) findViewById(R.id.processed_image_view);
		mProcessedImageLayout.setVisibility(View.INVISIBLE);//hide camera processed view

		mAlgoNameTextView = (TextView) findViewById(R.id.algoName);
		mTimeLabel = (TextView) findViewById(R.id.fpsLabel);
		mTimeLabel.setVisibility(View.VISIBLE);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getSupportActionBar()
				.getThemedContext(), R.array.algos, R.layout.sherlock_spinner_item);
		adapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(adapter, this);

		mAlgos = getResources().getStringArray(R.array.algos);
		mAlgoDescriptions = getResources().getStringArray(R.array.algoDescriptions);

		onAlgoSelected(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		Log.d("CamerActivityOnCreate", "onCreateOptionsMenu");
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId)
	{

		Log.d("CamerActivityOnCreate", "onNavigationItemSelected");
		Log.d("CamerActivityOnCreate", "algo number" + Integer.toString(itemPosition));
		onAlgoSelected(itemPosition);
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		Log.d("CamerActivityOnCreate", "onOptionsItemSelected");
		switch (item.getItemId())
		{
		case android.R.id.home:
			startActivityClearTop(MainActivity.class);
			return true;
		case R.id.aboutUs:
			startActivity(DialogActvity.constructIntent(getApplicationContext(),
					getString(R.string.aboutUsLabel), getString(R.string.aboutUsText)));
			return true;
			/*
			 * case R.id.settingsAction: startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
			 * return true;
			 */
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onBtnClick(View view)
	{

		Log.d("CamerActivityOnCreate", "onBtnClick");
		
//		if (picFlag == 2)
			picFlag = 1;
//		else
//			picFlag++;

//		Toast.makeText(getApplicationContext(), "Picture saved : " + picFlag, Toast.LENGTH_SHORT)
//				.show();
	}
	public void onBtnClick_scenes(View view)
	{
		Log.d("CamerActivityOnCreate", "onBtnClick_scenes");
		picFlag = 2;
	}
	public void onBtnClick_indiancars(View view)
	{

		Log.d("CamerActivityOnCreate", "onBtnClick_indiancars");
		picFlag = 3;
	}
	public void onBtnClick_landmarks(View view)
	{

		Log.d("CamerActivityOnCreate", "onBtnClick_landmarks");
		picFlag = 4;
	}
	

	@Override
	protected void onResume()
	{

		Log.d("CamerActivityOnCreate", "onResume");
		mCamera = getCameraInstance();

		if (mCamera != null)
		{

			// Create our Preview view and set it as the content of our activity.
			mProcessedImageView = new ProcessedImageSurfaceView(this, mSelectedAlgoID, this);
			mPreview = new CameraSurfaceView(this, mCameraID, mCameraOrientation, mCamera,
					mProcessedImageView);

			mPreviewLayout.addView(mPreview);
			mProcessedImageLayout.addView(mProcessedImageView);
		}
		else
		{
			Log.d(MainActivity.LOG_TAG, "! mCamera");
		}
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		pref.registerOnSharedPreferenceChangeListener(this);

		super.onResume();
	}

	@Override
	protected void onPause()
	{

		Log.d("CamerActivityOnCreate", "onPause");
		if (mPreview != null)
		{
			mPreviewLayout.removeView(mPreview);
			mPreview = null;
		}
		if (mProcessedImageView != null)
		{
			mProcessedImageLayout.removeView(mProcessedImageView);
			mProcessedImageView = null;
		}
		if (mCamera != null)
		{
			mCamera.release();
			mCamera = null;
		}
		PreferenceManager.getDefaultSharedPreferences(this)
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}


	private static Camera getCameraInstance()
	{

		Log.d("CamerActivityOnCreate", "getCameraInstance");
		Camera c = null;
		try
		{
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			int noOfCams = Camera.getNumberOfCameras();
			for (int i = 0; i < noOfCams; ++i)
			{
				Camera.getCameraInfo(i, cameraInfo);
				// /***** check for stereo camera is available here, only in the case of disparity app
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
				{
					mCameraID = i; // First back facing camera
					mCameraOrientation = cameraInfo.orientation;
					break; // /**** Just comment the break, so we get last back facing camera. It may(usually cam 2 is
							// stereo) be stereo camera.
				}
			}
			Log.d(MainActivity.LOG_TAG, "Num of Cams:" + noOfCams);
			Log.d(MainActivity.LOG_TAG, "Selected Cam ID:" + mCameraID);
			Log.d(MainActivity.LOG_TAG, "Selected Cam Orientation:" + mCameraOrientation);
			c = Camera.open(mCameraID); // attempt to get a Camera instance
		}
		catch (Exception e)
		{
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	private void onAlgoSelected(int which)
	{

		Log.d("CamerActivityOnCreate", "onAlgoSelected");
		mSelectedAlgoID = which;
		Log.d(MainActivity.LOG_TAG, "Selected:" + mAlgos[which]);
		Log.d("CamerActivityOnCreate", "Selected:" + mAlgos[which]);
		mAlgoNameTextView.setText(mAlgos[which]);
		if (mProcessedImageView != null)
		{
			Log.d("CamerActivityOnCreate", "setAlgoid");
			mProcessedImageView.setAlgoID(mSelectedAlgoID);
			Log.d("CamerActivityOnCreate", "done");
		}
	}

	private void startActivityClearTop(Class<?> cls)
	{

		Log.d("CamerActivityOnCreate", "startActivityClearTop");
		Intent intent = new Intent(getApplicationContext(), cls);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key)
	{

		Log.d("CamerActivityOnCreate", "onSharedPreferenceChanged");
		alignToPreferences(pref, key);
	}

	private void alignToPreferences(SharedPreferences pref, String key)
	{

		Log.d("CamerActivityOnCreate", "alignToPreferences");
		if (key.equals("prefEnableUVCV"))
		{
			if (mProcessedImageView != null)
			{
				mProcessedImageView.setProcessAlgo(pref.getBoolean(key, true));
			}
		}
		else if (key.equals("prefShowFPS"))
		{
			if (mTimeLabel != null)
			{
				mTimeLabel.setVisibility(pref.getBoolean(key, false) ? View.VISIBLE
						: View.INVISIBLE);
			}
		}
	}

	@Override
	public void onLog(String msg)
	{
		Log.d("CamerActivityOnCreate", "onLog");
		// TODO Auto-generated method stub
	}

	@Override
	public void onTimeGap(long timeGapInMs)
	{
		Log.d("CamerActivityOnCreate", "onTimeGap");
		mTimeLabel.setText(getString(R.string.fps) + timeGapInMs + " ms");
	}
}
