package com.uncannyvision.apps.imagerecognitiondemo.views;

public interface IImageReceiver {
	void onImageFrame(byte[] data, int width, int height, int format, int mCameraOrientation);
}
