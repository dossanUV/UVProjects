package com.uncannyvision.apps.imagerecognitiondemo.utils;

import android.content.Context;

public class Utils {
	static public String getAssetsFolderPath(Context context) {
		return context.getPackageResourcePath();
//		+ "/" + "assets" + "/"
	}
}
