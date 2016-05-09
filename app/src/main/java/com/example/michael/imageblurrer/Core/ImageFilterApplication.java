package com.example.michael.imageblurrer.Core;

import android.app.Application;
import android.content.Context;

public class ImageFilterApplication extends Application {

	private static ImageFilterApplication appInstance;

	public static ImageFilterApplication getAppInstance() {
		return appInstance;
	}

	public Context getAppContext() {
		return appInstance.getApplicationContext();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		appInstance = this;
	}
}
