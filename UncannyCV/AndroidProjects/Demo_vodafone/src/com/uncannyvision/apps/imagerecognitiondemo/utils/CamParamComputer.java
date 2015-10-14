package com.uncannyvision.apps.imagerecognitiondemo.utils;

import java.util.Iterator;
import java.util.List;
import android.hardware.Camera;

import com.uncannyvision.apps.imagerecognitiondemo.MainActivity;

import android.graphics.ImageFormat;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;

public class CamParamComputer
{
	private static final int PREFERRED_FORMAT = ImageFormat.NV21;
	private static final int PREFERRED_WIDTH = 640;
	private static final int PREFERRED_HEIGHT = 480;

	private List<Size> mSizes = null;
	private List<Integer> mFormats = null;
	private List<int[]> mFpsRanges = null;

	public CamParamComputer(Parameters params)
	{

		super();
		mSizes = params.getSupportedPreviewSizes();
		CamUtils.printImageSizes(mSizes);
		mFormats = params.getSupportedPreviewFormats();
		CamUtils.printImageFormats(mFormats);
		mFpsRanges = params.getSupportedPreviewFpsRange();
		CamUtils.printFpsRange(mFpsRanges);
//		if(params.isAutoExposureLockSupported())
//		if(params.getAutoExposureLock ())
//		{
//			Log.d(MainActivity.LOG_TAG, "Set auto exposue off" );
//			params.setAutoExposureLock(true);
//		}
	}

	public boolean computePreferred(Parameters parameters)
	{

		boolean changed = false;
		// Set if requires size available
		Iterator<Size> iter = mSizes.iterator();
		while (iter.hasNext())
		{
			Size ele = iter.next();
			if (ele.width == PREFERRED_WIDTH && ele.height == PREFERRED_HEIGHT)
			{
				Log.d(MainActivity.LOG_TAG, "setPreviewSize: w=" + ele.width + " h=" + ele.height);

				parameters.setPreviewSize(ele.width, ele.height);

				changed = true;
				break;
			}
		}
		// Set if required format available
		Iterator<Integer> iterFormat = mFormats.iterator();
		while (iterFormat.hasNext())
		{
			Integer ele = iterFormat.next();
			if (ele == PREFERRED_FORMAT)
			{
				Log.d(MainActivity.LOG_TAG, "setPreviewFormat: " + CamUtils.formatToName(ele));
				parameters.setPreviewFormat(ele);
				changed = true;
				break;
			}
		}

		int[] selectedFpsRange = mFpsRanges.get(mFpsRanges.size() - 1); // select Max.
		int[] originalRange = new int[2];
		parameters.getPreviewFpsRange(originalRange);
		if ((originalRange[0] != selectedFpsRange[0]) || (originalRange[1] != selectedFpsRange[1]))
		{
			Log.d(MainActivity.LOG_TAG, "setPreviewFpsRange: " + selectedFpsRange[0] + " to " + selectedFpsRange[1]);
			parameters.setPreviewFpsRange(selectedFpsRange[0], selectedFpsRange[1]);
			changed = true;
		}

		return changed;
	}
}
