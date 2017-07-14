package com.marq.plus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;

public class Agent {
	
	public static final String SHAREDPREFERENCES = "plus.marq.com";
	
	//public static final String VERSION = "20140430";
	public static final String VERSION = "20140903";
	
	public static final String QFUN_URL = "http://www.marq.com.tw/app/marqplus/qfun/";
	
	//private static final String MARQPLUS_URL = "http://www.marq.com.tw/app/cloudreco/marqplus/";
	
	private static final String CONFIG_URL = "http://plus.marq.com.tw/config";
	
	public static final int HTML_CODE_READY = 0;
	public static final int OPEN_URL_DIALOG = 1;	
	public static final int TOUCH_IDEL = 2;
	public static final int TOUCH_ACTIVE = 3;	
	public static final int ERROR_CODE = 999;
	
	
	public static final int START_SCAN = 101;
	
	public static final String SCAN_MODE = "SCAN";
	
	
	
	private Handler handlerCallback;


	private static Context context;
	
	private String strHtmlCode;
	
	public Agent(Context _context){
		context = _context;
		handlerCallback = null;
	}
	
	public Agent(Context _context, Handler callback){
		context = _context;
		handlerCallback = callback;
	}

	public Handler getHandlerCallback() {
		return handlerCallback;
	}

	public void setHandlerCallback(Handler handlerCallback) {
		this.handlerCallback = handlerCallback;
	}
	
	public Intent exitAPP(){
		Intent intent = new Intent(context, MarqSplashScreen.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("EXIT_MARQ", true);
		
		return intent;
	}
	
	public String getAPIUrl(){
		
		SharedPreferences sp = context.getSharedPreferences(SHAREDPREFERENCES, Context.MODE_PRIVATE);
		
		return sp.getString("CLOUDAPI_URL", "");
		
	}
	
	public void setAPIUrl(String url){
		SharedPreferences.Editor editor = context.getSharedPreferences(SHAREDPREFERENCES, Context.MODE_PRIVATE).edit();
		
		editor.putString("CLOUDAPI_URL", url);
		
		DebugLog.LOGE("Agent::CLOUDAPI_URL", url);
		
		editor.commit();
	}
	
	public String getARMenuUrl(){
		
		SharedPreferences sp = context.getSharedPreferences(SHAREDPREFERENCES, Context.MODE_PRIVATE);
		
		return sp.getString("ARMENU_URL", "");
		
	}
	
	public void setARMenuUrl(String url){
		SharedPreferences.Editor editor = context.getSharedPreferences(SHAREDPREFERENCES, Context.MODE_PRIVATE).edit();
		
		editor.putString("ARMENU_URL", url);
		
		DebugLog.LOGE("Agent::ARMENU_URL", url);
		
		editor.commit();
	}
	
	public String getSettingUrl(){
		
		SharedPreferences sp = context.getSharedPreferences(SHAREDPREFERENCES, Context.MODE_PRIVATE);
		
		return sp.getString("SETTING_URL", "");
		
	}
	
	public void setSettingUrl(String url){
		SharedPreferences.Editor editor = context.getSharedPreferences(SHAREDPREFERENCES, Context.MODE_PRIVATE).edit();
		
		editor.putString("SETTING_URL", url);
		
		DebugLog.LOGE("Agent::SETTING_URL", url);
		
		editor.commit();
	}
	
	
	public boolean isExitMarqVideoFullscreen(){
		
		SharedPreferences sp = context.getSharedPreferences(SHAREDPREFERENCES, Context.MODE_PRIVATE);
		
		DebugLog.LOGE("Agent::isExitMarqVideoFullscreen", (sp.getBoolean("EXIT_MARQVIDEOFULLSCREEN", false)) ? "true" : "false");
		
		return sp.getBoolean("EXIT_MARQVIDEOFULLSCREEN", false);
		
	}
	

	public void setExitMarqVideoFullscreen(boolean isExit){
		
		SharedPreferences.Editor editor = context.getSharedPreferences(SHAREDPREFERENCES, Context.MODE_PRIVATE).edit();
		
        editor.putBoolean("EXIT_MARQVIDEOFULLSCREEN", isExit);
		
		DebugLog.LOGE("Agent::EXIT_MARQVIDEOFULLSCREEN", "set " + (isExit ? "true" : "false"));
		
		editor.commit();
		
	}
	

	public boolean isModeScanning(){
		
		SharedPreferences sp = context.getSharedPreferences(SHAREDPREFERENCES, Context.MODE_PRIVATE);
		
		DebugLog.LOGE("Agent::isModeScanning", (sp.getBoolean("MODE_SCANNING", false)) ? "true" : "false");
		
		return sp.getBoolean("MODE_SCANNING", false);
		
	}
	

	public void setModeScanning(boolean scanning){
		
		SharedPreferences.Editor editor = context.getSharedPreferences(SHAREDPREFERENCES, Context.MODE_PRIVATE).edit();
		
        editor.putBoolean("MODE_SCANNING", scanning);
		
		DebugLog.LOGE("Agent::MODE_SCANNING", "set " + (scanning ? "true" : "false"));
		
		editor.commit();
		
	}
	
	public boolean isGCMReceive(){
		
		SharedPreferences sp = context.getSharedPreferences(SHAREDPREFERENCES, Context.MODE_PRIVATE);
		
		DebugLog.LOGE("Agent::isGCMReceive", (sp.getBoolean("MODE_GCMRECEIVE", false)) ? "true" : "false");
		
		return sp.getBoolean("MODE_GCMRECEIVE", false);
		
	}
	

	public void setGCMReceive(boolean isReceive){
		
		SharedPreferences.Editor editor = context.getSharedPreferences(SHAREDPREFERENCES, Context.MODE_PRIVATE).edit();
		
        editor.putBoolean("MODE_GCMRECEIVE", isReceive);
		
		DebugLog.LOGE("Agent::MODE_GCMRECEIVE", "set " + (isReceive ? "true" : "false"));
		
		editor.commit();
		
	}
	
	public int getGCMLastID(){
		
		SharedPreferences sp = context.getSharedPreferences(SHAREDPREFERENCES, Context.MODE_PRIVATE);
		
		DebugLog.LOGE("Agent::getGCMLastID", Integer.toString(sp.getInt("GCM_LAST_ID", 0)));
		
		return sp.getInt("GCM_LAST_ID", 0);
		
	}
	

	public void setGCMLastID(int id){
		
//		//if(remove) id--;
//		
//		if(remove){
//			if(getGCMLastID() > id) return;
//			else id--;
//		}
//		else
//			if(getGCMLastID() > id) return;
		
		SharedPreferences.Editor editor = context.getSharedPreferences(SHAREDPREFERENCES, Context.MODE_PRIVATE).edit();
		
        editor.putInt("GCM_LAST_ID", id);
		
		DebugLog.LOGE("Agent::setGCMLastID", "set GCM_LAST_ID = " + Integer.toString(id));
		
		editor.commit();
		
	}
	

//	public boolean isNetworkAccess(){
//		
//		SharedPreferences sp = context.getSharedPreferences(SHAREDPREFERENCES, Context.MODE_PRIVATE);
//		
//		DebugLog.LOGE("Agent::isNetworkAccess", (sp.getBoolean("NETWORK_ACCESS", true)) ? "true" : "false");
//		
//		return sp.getBoolean("NETWORK_CONNECTED", true);
//		
//	}
//	
//
//	public void setNetworkAccess(boolean isConnected){
//		
//		SharedPreferences.Editor editor = context.getSharedPreferences(SHAREDPREFERENCES, Context.MODE_PRIVATE).edit();
//		
//        editor.putBoolean("NETWORK_ACCESS", isConnected);
//		
//		DebugLog.LOGE("Agent::setNetworkAccess", "set " + (isConnected ? "true" : "false"));
//		
//		editor.commit();
//		
//	}
	
	
	
	
	private void msgCallback(int type){
    	Message msg = new Message();
		msg.what = type;
		handlerCallback.sendMessage(msg);
    }
	
	private void msgCallback(int type, Object object){
		
    	Message msg = new Message();
    	
		msg.what = type;
		msg.obj = object;
		
		handlerCallback.sendMessage(msg);
    }

	public void openUrlDialog(final String url) {	
		new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				msgCallback(OPEN_URL_DIALOG, url);
			}
        }).start();
	}	
	
	public void getHtmlByHttpClient(final String strUrl, final String formId){
    	
    	new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					DebugLog.LOGE("Agent::getHtmlByHttpClient::strUrl", strUrl);
					
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet(strUrl);
					
					get.setHeader("id", formId);
					get.setHeader("auth", getInstallId());
					get.setHeader("android0", getAccount());
					get.setHeader("language", getDeviceLanguage());
					
					HttpResponse response = client.execute(get);
					
					HttpEntity entity = response.getEntity();
					
					if(entity != null)
						strHtmlCode = EntityUtils.toString(entity);
					
					msgCallback(HTML_CODE_READY, strHtmlCode);
					
				} 
				catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					DebugLog.LOGE("Agent::getHtmlByHttpClient", "ClientProtocolException");
				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					DebugLog.LOGE("Agent::getHtmlByHttpClient", "IOException");
					//showSystemNetworkSettingDialog();
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					DebugLog.LOGE("Agent::getHtmlByHttpClient", "Exception");
				}
			}
        	
        }).start();
    	
    }
	
	public void postHtmlByHttpClient(final String strUrl, final Map<String,String> formData){
    	
    	new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub

				try {
					DebugLog.LOGE("Agent::postHtmlByHttpClient::strUrl", strUrl);
					
					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(strUrl);
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(formData.size());
					
					for(Entry<String, String> entry : formData.entrySet()) {
					    String key = entry.getKey();
					    String value = entry.getValue();
					    nameValuePairs.add(new BasicNameValuePair(key,value));
					}

					post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					
					HttpResponse response = client.execute(post);
					
					HttpEntity entity = response.getEntity();
					
					if(entity != null)
						strHtmlCode = EntityUtils.toString(entity);
					
					JSONObject result = new JSONObject(strHtmlCode);
					result.put("url", strUrl);
					
					msgCallback(HTML_CODE_READY, result.toString());
					
				} 
				catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					DebugLog.LOGE("Agent::getHtmlByHttpClient", "ClientProtocolException");
				} 
				catch (JSONException e) {
					// TODO Auto-generated catch block
					DebugLog.LOGE("Agent::postHtmlByHttpClient","JSONException:"+e.toString());	
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					DebugLog.LOGE("Agent::getHtmlByHttpClient", "IOException");
					//showSystemNetworkSettingDialog();
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					DebugLog.LOGE("Agent::getHtmlByHttpClient", "Exception");
				}
			}
        	
        }).start();
    	
    }
	
	private String getHtmlCode(){
		return strHtmlCode;
	}
	
	public void getConfig(){
		if(handlerCallback ==  null)
			return;
		
		//DebugLog.LOGE("getConfig()", "called");
		
		String url = CONFIG_URL + "?os=android&version=" + VERSION;
		
		getHtmlByHttpClient(url, "");
		
	}

	

	public int getScreenWidth(){

		DisplayMetrics dm = new DisplayMetrics();
		
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);

//		int screenWidth = dm.widthPixels;
//		int screenHeight = dm.heightPixels;
	
		return dm.widthPixels;

	}
	

	// get an unique id
	public synchronized static String getInstallId() {
		
		final String MARQPLUS = "MARQPLUS";
		
		String strUID = null;
		
		File file = new File(context.getFilesDir(), MARQPLUS);
			
		try {
			if (!file.exists())
				writeInstallationFile(file);
			
			strUID = readInstallationFile(file);
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		DebugLog.LOGE("UID = ", strUID);
		
		return strUID;
	}
	
	public synchronized static String getInstallIdWithContext(Context ctx) {
		
		final String MARQPLUS = "MARQPLUS";
		
		String strUID = null;
		
		File file = new File(ctx.getFilesDir(), MARQPLUS);
			
		try {
			if (!file.exists())
				writeInstallationFile(file);
			
			strUID = readInstallationFile(file);
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		DebugLog.LOGE("UID = ", strUID);
		
		return strUID;
	}
	
	private static String readInstallationFile(File file) throws IOException {
		RandomAccessFile f = new RandomAccessFile(file, "r");
		
		byte[] bytes = new byte[(int) f.length()];
		
		f.readFully(bytes);
		f.close();
		
		return new String(bytes);
	}

	private static void writeInstallationFile(File file) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		
		String id = UUID.randomUUID().toString();
		
		out.write(id.getBytes());
		out.close();
	}
	
	private String getAccount(){
		//<uses-permission android:name="android.permission.GET_ACCOUNTS"/>
		
		AccountManager mgr = AccountManager.get(context);
		
		//Account[] accountList = mgr.getAccounts();
		Account[] accountList = mgr.getAccountsByType("com.google");
		
		String strAccount = "";

//		for(Account account: accountList){
//			string = string + "Account-Name = " + account.name + "\nAccount-Type = " + account.type + "\n";
//			//string = string + " " + account.type + " ";
//		}
		if(accountList.length > 0){
			strAccount = accountList[0].name;
			DebugLog.LOGE("(strAccount) accountList[0].name = ", accountList[0].name);
		}
		
		return strAccount;
	}
	
	public String getDeviceLanguage(){
		
		if(Locale.getDefault().getISO3Country().contentEquals("TWN"))
			return "TWN";
		
		if(Locale.getDefault().getISO3Country().contentEquals("CHN"))
			return "CHN";
		
		if(Locale.getDefault().getISO3Country().contentEquals("JPN"))
			return "JPN";
		
		return "ENG";
	}
	
	// <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
	public boolean isNetworkConnect(){
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		
		return networkInfo != null && networkInfo.isConnectedOrConnecting();
	}
	
	// <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
	public boolean isMobileWifiConnect() {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo infoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo infoMobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
		if(infoWifi.isAvailable())
			return true;
		
		if(infoMobile.isAvailable())
			return true;
		
		return false;
	}
	
	public void showSystemNetworkSettingDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Network Connect Setting")
		.setIcon(R.drawable.icon_app_marq)
		.setMessage("Show Wireless & Network Settings?")
		.setPositiveButton("OK", new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				
				dialog.dismiss();
				
				context.startActivity(intent);

			}
			
		})
		.setNegativeButton("Exit", new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//((Activity)context).finish();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
			
		})
		.setCancelable(true);
		
		builder.create().show();
	}
	
	public String getStringFromMetadata(Context context,String key) {

        if (context == null) {
            return null;
        }
        
        ApplicationInfo ai = null;

        try {

            ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);

        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }

        if (ai == null || ai.metaData == null) {
            return null;
        }

        return ai.metaData.getString(key);

    }	
}