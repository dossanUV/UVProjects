package com.uncannyvision.apps.imagerecognitiondemo.utils;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.uncannyvision.apps.imagerecognitiondemo.MainActivity;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Surface;

public class CamUtils
{

	static public String formatToName(int format)
	{

		switch (format)
		{
		case ImageFormat.RGB_565:
			return "RGB_565";
		case ImageFormat.YV12:
			return "YV12";
		case ImageFormat.NV16:
			return "NV16";
		case ImageFormat.NV21:
			return "NV21";
		case ImageFormat.YUY2:
			return "YUY2";
		case ImageFormat.JPEG:
			return "JPEG";
		case ImageFormat.UNKNOWN:
			return "UNKNOWN";
		}
		return "Unlisted";
	}

	static public void printImageSizes(List<Size> sizes)
	{

		Iterator<Size> iter = sizes.iterator();

		while (iter.hasNext())
		{
			Size ele = iter.next();
			Log.d(MainActivity.LOG_TAG, "Size: w=" + ele.width + " h=" + ele.height);
		}
	}

	static public void printImageFormats(List<Integer> formats)
	{

		Iterator<Integer> iter = formats.iterator();
		while (iter.hasNext())
		{
			Integer ele = iter.next();
			Log.d(MainActivity.LOG_TAG, "Format: " + formatToName(ele));
		}
	}

	public static void alignCamAndDisplayOrientation(android.app.Activity activity, int cameraId,
			android.hardware.Camera camera)
	{

		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation)
		{
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
		{
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		}
		else
		{ // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}

	public static void printFpsRange(List<int[]> supportedPreviewFpsRange)
	{

		Iterator<int[]> iter = supportedPreviewFpsRange.iterator();

		while (iter.hasNext())
		{
			int[] fpsRange = iter.next();
			Log.d(MainActivity.LOG_TAG, "Fps=" + fpsRange[0] + " to " + fpsRange[1]);
		}
	}

	public static void bestFitCenter(int containerH, int containerW, AtomicReference<Integer> h,
			AtomicReference<Integer> w, AtomicReference<Integer> x, AtomicReference<Integer> y)
	{

		int fitW = containerW;
		int fitH = containerH;

		if (h.get() > containerH || w.get() > containerW)
		{
			// Contained is large
			float ratio = (float) w.get() / h.get();

			fitW = containerW;
			fitH = (int) Math.floor(containerW / ratio);

			if (fitH > containerH)
			{
				fitH = containerH;
				fitW = (int) Math.floor(containerH * ratio);
			}
		}

		x.set(Math.abs(fitW - containerW) / 2);
		y.set(Math.abs(fitH - containerH) / 2);

		w.set(fitW);
		h.set(fitH);
	}
}
