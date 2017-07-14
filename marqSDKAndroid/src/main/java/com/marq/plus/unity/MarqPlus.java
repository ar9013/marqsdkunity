package com.marq.plus.unity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.marq.plus.Agent;
import com.marq.plus.AndroidBridge;
import com.marq.plus.DebugLog;
import com.marq.plus.DialogAgent;
import com.marq.plus.GcmToken;
import com.marq.plus.MarqGalleryList;
import com.marq.plus.MarqSharePreferences;
import com.marq.plus.MarqWebBrowser;
import com.marq.plus.MarqWebView;
import com.marq.plus.R;
import com.marq.plus.ScratchActivity;

public class MarqPlus {
	private static MarqPlus instance = null;	
	private static Activity currentActivity = null;
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private static Map<String,String> configs;
    
	public static void Init(Activity activity) {
		currentActivity = activity;
		instance=GetInstance();
		UnitySendMessage("OnInitComplete","");
	}
	
	public static MarqPlus GetInstance() {
		if(instance == null) {
			instance = new MarqPlus();			
		}
		return instance;
	}
	
	public static void SetCurrentActivity(Activity activity) {
		currentActivity=activity;
	}
	
	public static Activity GetCurrentActivity() {
		return currentActivity;
	}
	
	public static void UnitySendMessage(String callbackMethodName,String message) {
		//UnityPlayer.UnitySendMessage("AndroidBridge", "MessageFromAndroid", message);			
		try {
			Class<?> params[] = {String.class,String.class,String.class};
		    Class<?> c = Class.forName("com.unity3d.player.UnityPlayer");
		    //Object t = c.newInstance();		
		    Method unitySendMessageMethod = c.getDeclaredMethod("UnitySendMessage", params);
		    unitySendMessageMethod.invoke(null, "MarqPlus", callbackMethodName, message);
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
	public static void SetInitConfig(String config) {
		/*
 		{
		    "status": "success",
		    "comment": "fetch success",
		    "data": {
		        "api": {
		            "GetLikeCollected": "http://test.api.plus.marq.com.tw/collection/event/fetch",
		            "InsertLike": "http://test.api.plus.marq.com.tw/collection/event/insert",
		            "GetLikeCount": "http://test.api.plus.marq.com.tw/event/fetch",
		            "GetTargetContent": "http://test.api.plus.marq.com.tw/target/log/insert"
		        },
		        "webview": {
		            "FirstTip": "http://my.marq.com.tw/webview/intro/#!/",
		            "HowToUse": "http://my.marq.com.tw/webview/qa/#!/",
		            "Menu": "http://my.marq.com.tw/webview/#!/"
		        }
		    }
		}
		*/
		configs = new HashMap<String,String>();
		try {
			JSONObject jsonConfig = new JSONObject(config);
			JSONObject jsonData= jsonConfig.getJSONObject("data");
			JSONObject jsonApi= jsonData.getJSONObject("api");	
			JSONObject jsonWebview= jsonData.getJSONObject("webview");
			
			configs.put("LIKE_COLLECTED_API", jsonApi.getString("GetLikeCollected"));
			configs.put("INSERT_LIKE_API", jsonApi.getString("InsertLike"));
			configs.put("LIKE_COUNT_API", jsonApi.getString("GetLikeCount"));
			configs.put("TARGET_CONTENT_API", jsonApi.getString("GetTargetContent"));	
			
			configs.put("FIRST_TIP_VIEW", jsonWebview.getString("FirstTip"));
			configs.put("HOW_TO_USE_VIEW", jsonWebview.getString("HowToUse"));				
			configs.put("MENU_VIEW", jsonWebview.getString("Menu"));
			configs.put("COUPON_VIEW", jsonWebview.getString("Gain"));			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
    		DebugLog.LOGE("MarqPlus::SetInitConfig", e.toString());	 
		}
		

		return;
	}
	
	public static Map<String,String> GetInitConfig() {
		return configs;
	}
	
	public static String GetSystemLanguage() {
		if(Locale.getDefault().getISO3Country().contentEquals("TWN"))
			return "ChineseTraditional";
		
		if(Locale.getDefault().getISO3Country().contentEquals("CHN"))
			return "ChineseSimplified";
		
		if(Locale.getDefault().getISO3Country().contentEquals("JPN"))
			return "Japanese";
		
		return "English";
	}
	
	public static  String GetDeviceUniqueIdentifier() {
		return Agent.getInstallIdWithContext(currentActivity);	
	}

	public static void OpenPhoneDialer(String number) {
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
		currentActivity.startActivity(intent);		
	}
	
	public static void OpenMap(String addressLat, String addressLng, String addressText) {
		String strGeo = "geo:";
    	
    	strGeo += addressLat;
    	strGeo += ",";
    	strGeo += addressLng;
    	strGeo += "?q=";
    	strGeo += addressLat;
    	strGeo += ",";
    	strGeo += addressLng;
    	strGeo = strGeo + "(" + addressText +")";
    	
    	
    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strGeo));
		currentActivity.startActivity(intent);		
	}
	public static void OpenYoutube(String youtubeUrl) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
		currentActivity.startActivity(intent);		
	}
	public static void SendEmail(String mailto) {
		Intent intent = new Intent(Intent.ACTION_SENDTO,
				Uri.fromParts("mailto", mailto, null));
		currentActivity.startActivity(intent);		
	}
	
	public static void OpenUrlDialog(String url) {
		
		final String title = currentActivity.getString(R.string.dialog_title_qr_msg);
		final String cancelText = currentActivity.getString(R.string.dialog_button_cancel);		
		final String openUrl = currentActivity.getString(R.string.dialog_button_qr_open_url);	
		final String copyUrl = currentActivity.getString(R.string.dialog_button_qr_copy_url);
		final String urlText = url;
		
		List<String> strings = new ArrayList<String>();
		strings.add(openUrl);
		strings.add(copyUrl);

		final CharSequence[] items = strings.toArray(new String[strings.size()]);		
		currentActivity.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				DialogAgent da=new DialogAgent();
				da.createDialogList(currentActivity, title, cancelText, items, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						//Toast.makeText(context, items[which] + " selected", Toast.LENGTH_SHORT).show();
						if(items[which].toString().equals(openUrl)) 
						{
							OpenWebBrowser(urlText);
						}
						else if(items[which].toString().equals(copyUrl))
						{
			                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) currentActivity.getSystemService(currentActivity.CLIPBOARD_SERVICE);
			                android.content.ClipData clip = android.content.ClipData.newPlainText(urlText,urlText);
			                clipboard.setPrimaryClip(clip);					
						}
						OnCloseUrlDialog();
					}
				}, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {						
						dialog.dismiss();
						OnCloseUrlDialog();
					}
				});	
			}
		});			
	}
	
	public static void OnCloseUrlDialog() {
		UnitySendMessage("OnOpenUrlDialogCallback","{ \"Command\": \"CloseUrlDialog\"}");		
	}
	
	public static void OpenWebView(String url) {
		Intent intent = new Intent();		
		intent.setClass(currentActivity, MarqWebView.class);			
		Bundle bundle = new Bundle();
		bundle.putString("URL_TARGET", url);
		intent.putExtras(bundle);
		currentActivity.startActivity(intent);			
	}
	
	public static void OnCloseWebView() {
		
		UnitySendMessage("OnOpenWebViewCallback","{ \"Command\": \"CloseWebView\"}");	
	}
	
	public static void OnGoToScanMode(int mode) {
		UnitySendMessage("OnOpenWebViewCallback","{ \"Command\": \"GoToScanMode\",\"Mode\":"+mode+"}");			
	}


	public static void OnGoToPhotoFrame(String targetId,String buttonId) {
		UnitySendMessage("OnOpenWebViewCallback","{ \"Command\": \"GoToPhotoFrame\",\"TargetID\":\""+targetId+"\",\"ButtonID\":\""+buttonId+"\"}");			
	}

	public static void OnGoToQFun() {
		UnitySendMessage("OnOpenWebViewCallback","{ \"Command\": \"GoToQFun\"}");			
	}

	public static void OnGoTo3DModule(String targetId,String buttonId){
		UnitySendMessage("OnOpenWebViewCallback","{\"Command\":\"GoTo3DModule\",\"TargetID\":\""+targetId+"\",\"ButtonID\":\""+buttonId+"\"}");
	}
	
	public static void OnGoToTryOn(String targetId,String buttonId){
		UnitySendMessage("OnOpenWebViewCallback","{\"Command\":\"GoToTryOn\",\"TargetID\":\""+targetId+"\",\"ButtonID\":\""+buttonId+"\"}");
	}
	
	public static void OpenWebBrowser(String url) {
		Intent intent = new Intent();		
		intent.setClass(currentActivity, MarqWebBrowser.class);			
		Bundle bundle = new Bundle();
		bundle.putString("URL_TARGET", url);
		intent.putExtras(bundle);
		currentActivity.startActivity(intent);			
	}
	
	public static void OnCloseWebBrowser() {
		
		UnitySendMessage("OnOpenWebBrowserCallback","{ \"Command\": \"CloseWebBrowser\"}");	
	}
	
	
	public static void OpenScratch(String eventId, String targetId, String msg,String fgPath, String bgPath) {
		Intent intent = new Intent();		
		intent.setClass(currentActivity, ScratchActivity.class);			
		Bundle bundle = new Bundle();
		bundle.putString("EVENT_ID", eventId);
		bundle.putString("TARGET_ID", targetId);
		bundle.putString("MSG", msg);		
		bundle.putString("IMAGE_PATH", fgPath);
		bundle.putString("BACKGROUND_PATH", bgPath);	
		intent.putExtras(bundle);
		currentActivity.startActivity(intent);			
	}
	
	public static void OnCloseScratch() {
		UnitySendMessage("OnOpenScratchCallback","{ \"Command\": \"CloseScratch\"}");		
	}
    
	public static void OnGoToWebview(String Url){
		UnitySendMessage("OnOpenScratchCallback","{ \"Command\": \"GoToWebView\",\"Url\": \""+Url+"\"}");
	}	
	
	public static void OpenPhotoGallery() {
		Intent intent = new Intent();	
		intent.setClass(currentActivity, MarqGalleryList.class);	
		currentActivity.startActivity(intent);			
	}
	
	public static void ShareImage(String path) {
		
		Intent share_intent = new Intent();
	      share_intent.setAction(Intent.ACTION_SEND);
	      share_intent.setType("image/png");
	      share_intent.putExtra(Intent.EXTRA_STREAM,
	         Uri.fromFile(new File(path)));
	      share_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	      //share_intent.putExtra(Intent.EXTRA_SUBJECT,
	      //   "share an image");
	      //share_intent.putExtra(Intent.EXTRA_TEXT,
	      //   "This is an image to share with you");

	      // start the intent
	      try {
	    	  currentActivity.startActivity(Intent.createChooser(share_intent,
	        		 currentActivity.getString(R.string.dialog_shared_type_title)));
	      } catch (android.content.ActivityNotFoundException ex) {
    		DebugLog.LOGE("MarqPlus", "Share Failed");	    	    	  
	      }			
	}
	
public static void RegisterPush() {
		
        //GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        //int resultCode = apiAvailability.isGooglePlayServicesAvailable(currentActivity);
        //if (resultCode != ConnectionResult.SUCCESS) {
    	//	DebugLog.LOGE("MarqPlus", "RegisterPush Failed, Google not Availabile");	 
        //    return;
        //}	
      
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
    	        SharedPreferences sharedPreferences =
    	                PreferenceManager.getDefaultSharedPreferences(currentActivity);
                GcmToken gcmToken = new GcmToken();
    	        String token = null;
                try {
                	String deviceUid =  sharedPreferences.getString(MarqSharePreferences.DEIVCE_UID, "");        	        
        	        if(deviceUid.equals(MarqPlus.GetDeviceUniqueIdentifier())==true) {
            	        String jsonStr =  sharedPreferences.getString(MarqSharePreferences.GCM_TOKEN, "");
            	        JSONObject tokenObj= new JSONObject(jsonStr);        
            	        gcmToken=GcmToken.fromJson(tokenObj); 
            	        token=gcmToken.token;
        	        }
        	        else {
        	        	token=null;
        	        	sharedPreferences.edit().putString(MarqSharePreferences.DEIVCE_UID, MarqPlus.GetDeviceUniqueIdentifier()).apply();
        	        }
                }
	    	    catch (JSONException e) {
	    	    	DebugLog.LOGE("MarqPlus", "RegisterPush Failed Json String JSONException: "+e.toString());	       	
	    	    }
	    	    catch(Exception e) {
	    	    	DebugLog.LOGE("MarqPlus", "RegisterPush Failed Json String Exception"+e.toString());	
	    	    	
	    	    }
                
                if(token == null) {
					try {
						Agent agent=new Agent(currentActivity);
				    	String sendID=agent.getStringFromMetadata(currentActivity, "com.marq.plus.GcmSendId");     
		                InstanceID instanceID = InstanceID.getInstance(currentActivity);	
		              
						token = instanceID.getToken(sendID,
						        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
		               
		                DebugLog.LOGI("GCM Registration Token: " + token);			
		                gcmToken.createdAt = System.currentTimeMillis();  
		                gcmToken.token = token;
		                sharedPreferences.edit().putString(MarqSharePreferences.GCM_TOKEN, gcmToken.toJson().toString()).apply(); 						
					} catch (IOException e) {
						// TODO Auto-generated catch block
		    	    	DebugLog.LOGE("MarqPlus", "RegisterPush Failed I/O Exception"+e.toString());
						//e.printStackTrace();
		    	    	
					}
	                // [END get_token]
					catch (JSONException e) 
					{
		    	    	DebugLog.LOGE("MarqPlus", "RegisterPush Failed Json String Exception"+e.toString());
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					
                }
                if(token!=null){
    	        GcmPubSub pubSub = GcmPubSub.getInstance(currentActivity);
    	        for (String topic : TOPICS) {
    	            try {
    	            	
						pubSub.subscribe(token, "/topics/" + topic, null);
						
					} catch (IOException e) {
			    		DebugLog.LOGE("MarqPlus", "RegisterPush Failed subscribe: "+e.toString());	
			    		
					}
    	            catch(Exception ex){
    	            	DebugLog.LOGE("MarqPlus", "RegisterPush Failed Token=null: "+ex.toString());	
    	            }
    	        }    
    	       
    	        OnPushRegistrationComplete(token);
                }
              
            	  
                return null;
            }
        }.execute();
        
        //LocalBroadcastManager.getInstance(currentActivity).registerReceiver(registrationBroadcastReceiver,
        //        new IntentFilter(MarqSharePreferences.REGISTRATION_COMPLETE));
        

	}
	
	public static void OnPushRegistrationComplete(String token) {
		UnitySendMessage("OnRegisterPushCallback","{ \"Command\": \"PushRegistrationComplete\", ,\"Token\":\""+token+"\"}");		
	}

    public static   void SaveToGallery(String Path){
      	Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(Path);

        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        MarqPlus.GetCurrentActivity().sendBroadcast(mediaScanIntent);   
        OnSaveToGalleryComplete(); 			
    }

    public static void OnSaveToGalleryComplete() {
		UnitySendMessage("OnSaveToGalleryCallback","{ \"Command\": \"SaveToGalleryComplete\"}");		
    }
	
}
 