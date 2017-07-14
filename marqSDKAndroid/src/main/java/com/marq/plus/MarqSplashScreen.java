package com.marq.plus;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class MarqSplashScreen extends Activity {
	
	private final int MARQ_PLUS_PROCESS = 100;
	
	private Handler handler;
	
	private Agent agent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);


		if(getIntent().getBooleanExtra("EXIT_MARQ", false))
			finish();
		
        // Sets the Splash Screen Layout
        setContentView(R.layout.marq_splashscreen);
        
        handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				
				switch(msg.what){
					case Agent.HTML_CODE_READY:
						//DebugLog.LOGE("MarqSplashScreen::HTML_CODE_READY", msg.obj.toString());
						
						DebugLog.LOGE("MarqSplashScreen::HTML_CODE_READY", msg.obj.toString());
						
						try {
							setConfig(msg.obj.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							new DialogAgent().createDialogMessage(MarqSplashScreen.this, "System Error(54)", "Please Inform Engineer");
						}
						break;
						
					default:
						new DialogAgent().createDialogMessage(MarqSplashScreen.this, "System Error", "Please Re-install or Inform Engineer");
						break;	

				}
			}
    		
    	};
    	
    	agent = new Agent(MarqSplashScreen.this, handler);
    	
    	//agent.getConfig();
    	
    	if(agent.isNetworkConnect())
    		agent.getConfig();
    	else{
//    		agent.setNetworkAccess(false);
    		agent.showSystemNetworkSettingDialog();
    	}

        
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		DebugLog.LOGE("MarqSplashScreen", "onPause");
		
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		
		
		super.onResume();
		
		DebugLog.LOGE("MarqSplashScreen", "onResume");
		
		if(agent.isNetworkConnect()){
			agent.getConfig();
		}
		else{
//			agent.setNetworkAccess(false);
    		agent.showSystemNetworkSettingDialog();
		}
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		android.os.Process.killProcess(android.os.Process.myPid());
		super.onDestroy();
	}

	private void updateProcess(final String updateURL){

		AlertDialog.Builder builder =  new AlertDialog.Builder(MarqSplashScreen.this);
		
		builder.setTitle("Update APP");
		builder.setMessage("Version: " + Agent.VERSION + "\nYou have to update marq+.");
		
		builder.setPositiveButton("Update", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateURL));
				startActivity(intent);
				
				finish();
			}
			
		});
		
		builder.setNegativeButton("Exit APP", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				startActivity(new Agent(MarqSplashScreen.this).exitAPP());
			}
			
		});
		
		builder.create().show();
	}
	
	private void processRunApp(){
		// Generates a Handler to launch the About Screen
        // after 1 seconds
        final Handler handler = new Handler();
    	final String classname=agent.getStringFromMetadata(this, "com.marq.plus.LaunchBySplashScreen");        
        handler.postDelayed(new Runnable()
        {
            public void run()
            {
            	Class<?> c;
                // Starts the About Screen Activity
            	try {
            		c =Class.forName(classname);
            	}
            	catch(LinkageError e ){
            		return;            		
            	}
            	catch(ClassNotFoundException e) {
            		return;            		
            	}            	
            	catch(Exception e) {
            		return;
            	}
                /** Starts the CloudReco main activity */
            	startActivity(new Intent(MarqSplashScreen.this, c));

            }
        }, 1000L);
	}
	
	private void setConfig(String config) throws JSONException{
		JSONObject jsonObject = new JSONObject(config);

		if(jsonObject.getString("error").matches("null")){
			// run app

			agent.setAPIUrl(jsonObject.getString("api"));
			agent.setARMenuUrl(jsonObject.getString("view_menu"));
			agent.setSettingUrl(jsonObject.getString("view_setting"));
			
			//agent.setARMenuUrl("http://pp.cc.qq");
			//agent.setSettingUrl("http://www.yahoo.com.tw");
			
			processRunApp();
		}
		else
			updateProcess(jsonObject.getString("update"));
	}


}