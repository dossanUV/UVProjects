package com.uncannyvision.apps.imagerecognitiondemo.deeplearning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.uncannyvision.apps.imagerecognitiondemo.MainActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageSimilarity
{
	public String imageDbName = "/storage/sdcard0/ImgSimilarity/.imgDb.txt";
	public String simFileName = "/storage/sdcard0/ImgSimilarity/similarity.log.txt";
	public native void enableLog(boolean enabled);
    public native int initTest(String modelPath, String weightsPath, String packagePath);
    private native int delImg(int algoID, String imageName);
    private native int addImg(int algoID, String imagePath, byte[] dataIn, int width, int height, int flag,int[] array, int numOfClassesRequired, byte[] detectedClasses,int[] lengthOfClassNamesArr);
	private byte[] classesDetails =null;

	public int dbUpdate(String imagePath)
	{
		try {
			/// Get the names of all files in the folder
			File imgFolder = new File(imagePath);
			File[] listOfImgs = imgFolder.listFiles();

			/// Open the file that tracks the image names
			File imgDbFile = new File(imageDbName);
			// if file doesnt exists, then create it
			if (!imgDbFile.exists()) {
				return 1;
			}	

			/// file that outputs the similarity
			File simFile = new File(simFileName);
			// if file doesnt exists, then create it
			if (!simFile.exists()) {
				return 1;
			}					

			FileReader fr_imgDb = new FileReader(imgDbFile.getAbsoluteFile());
			BufferedReader br_imgDb = new BufferedReader(fr_imgDb);
			
			//Create temp file to dump data
			File tempDbFile = new File(imageDbName+".txt");
			// if file doesnt exists, then create it
			if (!tempDbFile.exists()) {
				tempDbFile.createNewFile();
			}
			FileWriter fw_tempDb = new FileWriter(tempDbFile.getAbsoluteFile());
			BufferedWriter bw_tempDb = new BufferedWriter(fw_tempDb);
			File tempSimFile = new File(simFileName+".txt");
			
			String searchInLog = "\"InputFileName\":"; 
			String imgInDb;
			while ((imgInDb = br_imgDb.readLine()) != null)
			{
				int varI;
				for(varI=0; varI<listOfImgs.length; varI++)
				{
					if (imgInDb.contains(listOfImgs[varI].toString()))
					{	///Image is still present in the device
						bw_tempDb.append(listOfImgs[varI].toString()+"\n");	
						break;
					}					
				}
				/// give to delete the descriptor
				if(varI==listOfImgs.length)
				{
					FileReader fr_simFile = new FileReader(simFile.getAbsoluteFile());
					BufferedReader br_simFile = new BufferedReader(fr_simFile);

					// if file doesnt exists, then create it
					if (!tempSimFile.exists()) {
						tempSimFile.createNewFile();
					}
					FileWriter fw_tempSim = new FileWriter(tempSimFile.getAbsoluteFile(),true);
					BufferedWriter bw_tempSim = new BufferedWriter(fw_tempSim);
					searchInLog += imgInDb;
					String strSim;
					while ((strSim = br_simFile.readLine()) != null)
					{
						if (!strSim.contains(searchInLog))
						{
							bw_tempSim.append(strSim+"\n");	
						}	
					}
					delImg(28, imgInDb);
					bw_tempSim.close();
					br_simFile.close();
					tempSimFile.renameTo(simFile);
				}
			}
			bw_tempDb.close();
			br_imgDb.close();
			tempDbFile.renameTo(imgDbFile);
//			File fw_imgDb = new FileWriter(imgDbFile.getAbsoluteFile());
//			BufferedWriter bw_imgDb = new BufferedWriter(fw_imgDb);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
    
    public int similarityImage(String imagePath,StringBuffer detectedClassesWithProb)
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
	
			 result = addImg(28, imagePath, byteArray, width, height, 
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
    
    public boolean checkDbExistence (String imgName)
    {
    	try{
			File imgDbFile = new File(imageDbName);
			// if file doesnt exists, then create it
			if (!imgDbFile.exists()) {
				imgDbFile.createNewFile();
				}
			
			FileReader fr_imgDb = new FileReader(imgDbFile.getAbsoluteFile());
			BufferedReader br_imgDb = new BufferedReader(fr_imgDb);
		//	FileUtils asdf;
//			bw_imgDb.
			String imgInDb;
			while ((imgInDb = br_imgDb.readLine()) != null)
			{
					if (imgInDb.equals(imgName))
					{	///Image is still present in the device
						br_imgDb.close();
						return true;
					}	
			}			
			br_imgDb.close();
			}
		catch (IOException e) {
				e.printStackTrace();
			}
    	
    	return false;
    }

}
