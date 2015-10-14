package com.uncannyvision.apps.imagerecognitiondemo.deeplearning;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.nio.charset.Charset;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.uncannyvision.apps.imagerecognitiondemo.CameraActivity;
import com.uncannyvision.apps.imagerecognitiondemo.MainActivity;


public class ImageRecognition {
	public native void enableLog(boolean enabled);
    public native int initTest(String modelPath, String weightsPath, String packagePath);
    private native int runTest(int algoID, byte[] dataIn, int width, int height, int flag,int[] array, int numOfClassesRequired, byte[] detectedClasses,int[] lengthOfClassNamesArr);
	private byte[] classesDetails =null;

    
    public int tagImage(String imagePath,StringBuffer detectedClassesWithProb)
    {
		int result=0;
		Bitmap testImgBitMap=BitmapFactory.decodeFile(imagePath);
		
		if(testImgBitMap!=null)
		{
			int height = testImgBitMap.getHeight();
			int width = testImgBitMap.getWidth();
			int[] pixels;
	
			pixels = new int[height * width];
			
			testImgBitMap.getPixels(pixels, 0, width, 0, 0, width , height );//pixels array gets ARGB data    
	
			ByteBuffer byteBuffer = ByteBuffer.allocate(pixels.length * 4);        
			IntBuffer intBuffer = byteBuffer.asIntBuffer();
			intBuffer.put(pixels);
	
			byte[] byteArray = byteBuffer.array();
			int flag=1;
			classesDetails = new byte[1000000];
			int[] indexOut = new int[100];
			int numOfClassesRequired=5;
			int[] lengthOfClassNamesArr = new int[1];
			
			int algoID = 0;// for scenes and objects 
			result = runTest(algoID,byteArray, width, height, 
					 flag,indexOut, numOfClassesRequired,classesDetails,lengthOfClassNamesArr );// algoID, // Pass the required Algo ID here//28 for DL
			 
			 if(result>=0)
			 {
			 String detectedClassesInJson;
			 try {
				  detectedClassesInJson = new String(classesDetails,"UTF-8");
				  detectedClassesInJson=detectedClassesInJson.substring(0,lengthOfClassNamesArr[0]);//remove junk values after last class
				  detectedClassesWithProb.append(detectedClassesInJson);
				//Log.d(MainActivity.LOG_TAG, "ClassesInJsonString"+ detectedClassesInJson.substring(0,lengthOfClassNamesArr[0]));
				} 
			 catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		}
		else
		{
			result=-99;
		}
    	return result;
    }
    
    public int tagCars(String imagePath,StringBuffer detectedClassesWithProb)
    {
		int result=0;
		Bitmap testImgBitMap=BitmapFactory.decodeFile(imagePath);
		
		if(testImgBitMap!=null)
		{
			int height = testImgBitMap.getHeight();
			int width = testImgBitMap.getWidth();
			int[] pixels;
	
			pixels = new int[height * width];
			
			testImgBitMap.getPixels(pixels, 0, width, 0, 0, width , height );//pixels array gets ARGB data    
	
			ByteBuffer byteBuffer = ByteBuffer.allocate(pixels.length * 4);        
			IntBuffer intBuffer = byteBuffer.asIntBuffer();
			intBuffer.put(pixels);
	
			byte[] byteArray = byteBuffer.array();
			int flag=1;
			classesDetails = new byte[1000000];
			int[] indexOut = new int[100];
			int numOfClassesRequired=5;
			int[] lengthOfClassNamesArr = new int[1];
	
			int algoID = 1;// for indian cars 
			result = runTest(algoID,byteArray, width, height, 
					 flag,indexOut, numOfClassesRequired,classesDetails,lengthOfClassNamesArr );// algoID, // Pass the required Algo ID here//28 for DL
			 
			 if(result>=0)
			 {
			 String detectedClassesInJson;
			 try {
				  detectedClassesInJson = new String(classesDetails,"UTF-8");
				  detectedClassesInJson=detectedClassesInJson.substring(0,lengthOfClassNamesArr[0]);//remove junk values after last class
				  detectedClassesWithProb.append(detectedClassesInJson);
				//Log.d(MainActivity.LOG_TAG, "ClassesInJsonString"+ detectedClassesInJson.substring(0,lengthOfClassNamesArr[0]));
				} 
			 catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		}
		else
		{
			result=-99;
		}
    	return result;
    }
    
    public int tagLandmarks(String imagePath,StringBuffer detectedClassesWithProb)
    {
		int result=0;
		Bitmap testImgBitMap=BitmapFactory.decodeFile(imagePath);
		
		if(testImgBitMap!=null)
		{
			int height = testImgBitMap.getHeight();
			int width = testImgBitMap.getWidth();
			int[] pixels;
	
			pixels = new int[height * width];
			
			testImgBitMap.getPixels(pixels, 0, width, 0, 0, width , height );//pixels array gets ARGB data    
	
			ByteBuffer byteBuffer = ByteBuffer.allocate(pixels.length * 4);        
			IntBuffer intBuffer = byteBuffer.asIntBuffer();
			intBuffer.put(pixels);
	
			byte[] byteArray = byteBuffer.array();
			int flag=1;
			classesDetails = new byte[1000000];
			int[] indexOut = new int[100];
			int numOfClassesRequired=5;
			int[] lengthOfClassNamesArr = new int[1];
	
			int algoID = 2;// for landmarks
			result = runTest(algoID,byteArray, width, height, 
					 flag,indexOut, numOfClassesRequired,classesDetails,lengthOfClassNamesArr );// algoID, // Pass the required Algo ID here//28 for DL
		
			 if(result>=0)
			 {
			 String detectedClassesInJson;
			 try {
				  detectedClassesInJson = new String(classesDetails,"UTF-8");
				  detectedClassesInJson=detectedClassesInJson.substring(0,lengthOfClassNamesArr[0]);//remove junk values after last class
				  detectedClassesWithProb.append(detectedClassesInJson);
				//Log.d(MainActivity.LOG_TAG, "ClassesInJsonString"+ detectedClassesInJson.substring(0,lengthOfClassNamesArr[0]));
				} 
			 catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		}
		else
		{
			result=-99;
		}
    	return result;
    }

}
