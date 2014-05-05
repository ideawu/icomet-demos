package com.kyleduo.csclient.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {
	
	private static SharedPreferences sp;
	
	private static final String SP_NAME = "csclient_sp";
	
	private static SharedPreferences getSharedPreferences(Context context) {
		if (sp == null) {
			sp = context.getApplicationContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		}
		return sp;
	}
	
	public static void putString(Context context, String key, String value) {
		getSharedPreferences(context).edit().putString(key, value).commit();
	}
	
	public static String getString(Context context, String key) {
		return getSharedPreferences(context).getString(key, "");
	}
	
	
	
	
}
