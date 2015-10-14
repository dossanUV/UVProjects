package com.uncannyvision.apps.imagerecognitiondemo;

import java.io.File;
import java.io.FileFilter;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.uncannyvision.apps.demo_vodafone_similarity_and_recognition.R;

//import org.opencv.android.BaseLoaderCallback;
//import org.opencv.android.LoaderCallbackInterface;
//import org.opencv.android.OpenCVLoader;
//import org.opencv.android.Utils;
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.core.Point;
//import org.opencv.core.Scalar;
//import org.opencv.core.CvType;
//import org.opencv.core.Size;
//import org.opencv.highgui.Highgui;
//import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.SurfaceHolder;
import android.widget.Toast;

public class MainActivity extends SherlockActivity
{
	public static final String LOG_TAG = "CvAlgosTester";

	public static String basepath = "/storage/sdcard0/uvvision";
	// public static String basepath = "/storage/emulated/0/uvvision/";

	public static String cameradirectory = "/storage/sdcard0/DCIM/Camera"; // S3
	// public static String cameradirectory = "/storage/emulated/0/DCIM/Camera";
	// //Nexus 4
	// public static String cameradirectory =
	// "/storage/external_SD/DCIM/100LGDSC"; //Dossan LG

	// public static String filename="/uvvision/grayimage.bmp";
	public static String filename = "/grayimage.bmp";

	public static String grayimagepath="";

	public boolean isExternalStorageWritable()
	{

		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state))
		{
			return true;
		}
		return false;
	};
	public void myToast(String msg) {
	    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
//	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this)
//	{
//		@Override
//		public void onManagerConnected(int status)
//		{
//
//			switch (status)
//			{
//			case LoaderCallbackInterface.SUCCESS: {
//
//				String externalStoragePath, DCIMPath, camDirectoryPath;
//
//				// // **** another code for to find last modified image; not tested
//				// String[] projection = new String[]{
//				// MediaStore.Images.ImageColumns._ID,MediaStore.Images.ImageColumns.DATA,
//				// MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
//				// MediaStore.Images.ImageColumns.DATE_TAKEN,
//				// MediaStore.Images.ImageColumns.MIME_TYPE
//				// };
//				// final Cursor cursor = managedQuery(
//				// MediaStore.Images.Media.EXTERNAL_CONTENT_URI,projection, null, null,
//				// MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
//				// //***** you will find the last taken picture here according to Bojan
//				// Radivojevic Bomber comment do not close the cursor (he is right ^^)
//				// if(cursor != null){ cursor.moveToFirst();
//				// //cursor.close();
//				// }
//
//				// ***** following code is working one, that will take last
//				// modified file from the default memory(that user can set, in
//				// by default it will be like sdcard0 or storage0 something)
//
//				// There is only one thing designated "external storage" per device.
//				// What it is (SD card, on-board flash, 3.5" floppy disk, wafer tape/stringy floppy/micro drive,
//				// lots and lots of tiny punch cards, whatever) is up to the manufacturer.
//				// From the SDK's standpoint, "the sd card and the device built-in external storage" are the same thing, for
//				// devices that use the SD card as external storage. For devices that do not, from the
//				// SDK's standpoint "the sd card" does not exist.
//
//				if (isExternalStorageWritable())
//				{
//					externalStoragePath = Environment.getExternalStorageDirectory().toString();
//					DCIMPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
//					File DCIMdir = new File(DCIMPath);
//					File lastModifiedDir;
//					File[] DCIMFilesAndFolders = DCIMdir.listFiles();
//					int noOfFilesAndFolder = DCIMFilesAndFolders.length;
//					Log.i(LOG_TAG, "Total No of Folders in DCIM Directory: " + String.valueOf(noOfFilesAndFolder));
//
//					// if folder list ends with . folders, remove them
//					while (DCIMFilesAndFolders[noOfFilesAndFolder - 1].getAbsolutePath().startsWith(
//							DCIMdir.getAbsolutePath() + "/."))
//					{
//						noOfFilesAndFolder--;
//					}
//
//					// Finds Camera Directory inside DCIM. This code will detect this only
//					// if last modified subdirectory of DCIM is camera directory. So before testing
//					// take a picture. Sometimes you might not be saving pictures in your primary
//					// external storage, then you will have to change camera storage setting.
//
//					lastModifiedDir = DCIMFilesAndFolders[noOfFilesAndFolder - 1];
//					for (int i = 0; i < noOfFilesAndFolder - 1; i++)
//					{
//						Log.i(LOG_TAG, "Path[" + String.valueOf(i) + "] : " + DCIMFilesAndFolders[i].getAbsolutePath());
//						Log.i(LOG_TAG,
//								"Last modified[" + String.valueOf(i) + "]: "
//										+ String.valueOf(DCIMFilesAndFolders[i].lastModified()));
//
//						if (lastModifiedDir.lastModified() < DCIMFilesAndFolders[i].lastModified()
//								&& !DCIMFilesAndFolders[i].getAbsolutePath().startsWith(DCIMdir.getAbsolutePath() + "/.")
//								&& DCIMFilesAndFolders[i].isDirectory() )
//						{
//							lastModifiedDir = DCIMFilesAndFolders[i];
//						}
//					}
//					camDirectoryPath = lastModifiedDir.getAbsolutePath();
//					Log.i(LOG_TAG, "Last modified DIR in DCIM: " + camDirectoryPath);
//
//					// grayimage path
//					String filepath = new StringBuilder().append(Environment.getExternalStorageDirectory().toString())
//							.append(filename).toString();
//					grayimagepath = filepath;
//					// If there is already a gray image, delete it. We will be generating new one.
//					File file = new File(filepath);
//					if (file.exists())
//					{
//						Log.i(LOG_TAG, "Deleting Existing grayimage");
//						file.delete();
//					}
//
//					// Find Last Modified File from camera directory
//					// File dir = new File(cameradirectory);
//					File dir = lastModifiedDir;
//					File lastModifiedFile;
//					File[] files = dir.listFiles();
//					int lengthx = files.length;
//					Log.i(LOG_TAG, "Total No of Files in Cam Directory: " + String.valueOf(lengthx));
//					lastModifiedFile = files[files.length - 1];
//					for (int i = 0; i < files.length - 1; i++)
//					{
//						if (lastModifiedFile.lastModified() <= files[i].lastModified()
//							&& (files[i].getName().contains(".jpg") || files[i].getName().contains(".jpeg")
//							|| files[i].getName().contains(".bmp")) )
//						{
//							Log.i(LOG_TAG, "Path[" + String.valueOf(i) + "] : " + files[i].getAbsolutePath());
//							Log.i(LOG_TAG,
//									"Last modified[" + String.valueOf(i) + "]: "
//											+ String.valueOf(files[i].lastModified()));
//							lastModifiedFile = files[i];
//						}
//					}
//					Log.i(LOG_TAG, "Last photo path: " + lastModifiedFile.getAbsolutePath());
//
//					// Generate gray file from Last Modified Image
//					Mat image = null;
//					Mat gray = new Mat(480, 640, CvType.CV_8UC1);
//					try
//					{
//						image = Highgui.imread(lastModifiedFile.getAbsolutePath(), 0);
//						// Check whether gray image generated or not
//						if (image == null)
//						{
//							AlertDialog ad = new AlertDialog.Builder(MainActivity.this).create();
//							ad.setMessage("Fatal error: can't open last modified image!");
//						}
//					}
//					catch (Exception e)
//					{
//						// Display error if it fails reading last modified image
//						System.err.print("Error in Image read");
//						Log.i(LOG_TAG, "Error in imread");
//					}
//					Log.i(LOG_TAG, "Gray Image Created");
//
//					org.opencv.imgproc.Imgproc.resize(image, gray, gray.size());
//					Log.i(LOG_TAG, "Finished Resizing Gray Image");
//
//					Highgui.imwrite(filepath, gray);
//					Log.i(LOG_TAG, "Finished writing image back to disk Image");
//					Log.i(LOG_TAG, "Gray Image Location: " + filepath);
//
//					gray.release();
//					image.release();
//				}
//				else
//				{
//					AlertDialog ad = new AlertDialog.Builder(MainActivity.this).create();
//					ad.setMessage("Fatal error: can't access primary external storage");
//				}
//
//			}
//				break;
//			default: {
//				super.onManagerConnected(status);
//			}
//				break;
//			}
//		}
//	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		Log.i(LOG_TAG, "java 1");
	}

	@Override
	public void onResume()
	{

		super.onResume();
//		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{

		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		Log.i(LOG_TAG, "Action Bar Inflate");
		return true;
	}

	public void onBtnClick(View view)
	{

		switch (view.getId())
		{
		case R.id.homeBtnConsoleRun:
			myToast("Function not supported in this release");
			//startActivityClearTop(ConsoleActivity.class);
			break;
		case R.id.homeBtnCamRun:
			startActivityClearTop(CameraActivity.class);
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
		{
		case android.R.id.home:
			startActivityClearTop(MainActivity.class);
			return true;
		case R.id.aboutUs:
			startActivity(DialogActvity.constructIntent(getApplicationContext(), getString(R.string.aboutUsLabel),
					getString(R.string.aboutUsText)));
			return true;
			/*
			 * case R.id.settingsAction: startActivity(new Intent(getApplicationContext(), SettingsActivity.class)); return true;
			 */
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void startActivityClearTop(Class<?> cls)
	{

		Intent intent = new Intent(getApplicationContext(), cls);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
