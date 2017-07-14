package com.marq.plus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
//import com.unity3d.player.UnityPlayer;


public class AndroidBridge {
	private Handler handler;
	private Agent agent;
	private static AndroidBridge instance = null;	
	private static Activity currentActivity = null;
	
	AndroidBridge() {
	}
	
	public static void Init(Activity activity) {
		currentActivity = activity;
		instance=GetInstance();
	}
	
	public static AndroidBridge GetInstance() {
		if(instance == null) {
			instance = new AndroidBridge();			
		}
		return instance;
	}
	
	public static void SendMessageToAndroid(String message) {
		DebugLog.LOGI(message);
		String commandStr=null;
		JSONObject jObj;
        try {
            jObj = new JSONObject(message);
            commandStr=jObj.getString("Command");

	        if (commandStr.compareTo("OpenWebView")==0) {
	        	String urlStr=jObj.getString("URL");
	        	instance.OpenWebView(urlStr);
	        }
	        else if(commandStr.compareTo("OpenScratch")==0) {
	        	String backGroundPath=jObj.getString("BackGroundPath");
	        	String imagePath=jObj.getString("ImagePath");
	        	instance.OpenScratch(imagePath,backGroundPath);	        	
	        }
	        else if(commandStr.compareTo("")==0) {
	        	
	        }      
        } catch (JSONException e) {
        	e.printStackTrace();
			DebugLog.LOGE("AndroidBridge::SendMessageToAndroid", e.toString());
        }        
		//MessageFromAndroid(message);
	}
	
	public static void MessageFromAndroid(String message) {
		//UnityPlayer.UnitySendMessage("AndroidBridge", "MessageFromAndroid", message);			
		try {
			Class<?> params[] = {String.class,String.class,String.class};
		    Class<?> c = Class.forName("com.unity3d.player.UnityPlayer");
		    //Object t = c.newInstance();		
		    Method unitySendMessageMethod = c.getDeclaredMethod("UnitySendMessage", params);
		    unitySendMessageMethod.invoke(null, "AndroidBridge", "MessageFromAndroid", message);
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
			DebugLog.LOGE("AndroidBridge::MessageFromAndroid", e.toString());		    
		} catch (NoSuchMethodException e) {
			e.printStackTrace();	 
			DebugLog.LOGE("AndroidBridge::MessageFromAndroid", e.toString());					
		} catch (IllegalAccessException e) {
		    e.printStackTrace();
			DebugLog.LOGE("AndroidBridge::MessageFromAndroid", e.toString());			    
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			DebugLog.LOGE("AndroidBridge::MessageFromAndroid", e.toString());				
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			DebugLog.LOGE("AndroidBridge::MessageFromAndroid", e.toString());				
		}		
	}
	
	public void OpenWebView(String Url) {
		Intent intent = new Intent();		
		intent.setClass(currentActivity, MarqWebBrowser.class);			
		Bundle bundle = new Bundle();
		bundle.putString("URL_TARGET", Url);
		intent.putExtras(bundle);
		currentActivity.startActivity(intent);		
	}
	
	public void OpenScratch(String ImagePath,String BackGroundPath) {
		Intent intent = new Intent();		
		intent.setClass(currentActivity, ScratchActivity.class);			
		Bundle bundle = new Bundle();
		bundle.putString("IMAGE_PATH", ImagePath);
		bundle.putString("BACKGROUND_PATH", BackGroundPath);	
		intent.putExtras(bundle);
		currentActivity.startActivity(intent);		
	}	
}

