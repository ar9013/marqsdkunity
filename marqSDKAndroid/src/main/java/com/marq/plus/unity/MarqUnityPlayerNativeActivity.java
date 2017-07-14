package com.marq.plus.unity;

import com.marq.plus.unity.MarqPlus;
import com.unity3d.player.*;
import android.app.NativeActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class MarqUnityPlayerNativeActivity extends NativeActivity
{
	protected UnityPlayer mUnityPlayer;		// don't change the name of this variable; referenced from native code

	// Setup activity layout
	@Override protected void onCreate (Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		getWindow().takeSurface(null);
		getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia play happy

		mUnityPlayer = new UnityPlayer(this);
		if (mUnityPlayer.getSettings ().getBoolean ("hide_status_bar", true))
			getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN,
			                       WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(mUnityPlayer);
		mUnityPlayer.requestFocus();
		
		Intent intent = getIntent();
		if(intent == null) return;
		Bundle b = intent.getExtras();
		if(b == null) return;
		
		String Command = b.getString("Command");
		if(Command == null)  return;
		String Url =  b.getString("Url");
		if(Url == null)  return;
		
		if(Command.equalsIgnoreCase("OpenWebView")==true){
		//	Log.d("UnityPlayerNativeActivity::OpenWebView", b.getString("Url"));
			if(MarqPlus.GetCurrentActivity() == null) MarqPlus.SetCurrentActivity(this);
			MarqPlus.OpenWebView( b.getString("Url"));	
			intent.removeExtra("Command"); 
			intent.removeExtra("Url"); 
		}
	}

	// Quit Unity
	@Override protected void onDestroy ()
	{
		mUnityPlayer.quit();
		super.onDestroy();
	}

	// Pause Unity
	@Override protected void onPause()
	{
		super.onPause();
		mUnityPlayer.pause();
	}

	// Resume Unity
	@Override protected void onResume()
	{
		super.onResume();
		mUnityPlayer.resume();	
		
		Intent intent = getIntent();
		if(intent == null) return;
		Bundle b = intent.getExtras();
		if(b == null) return;
		
		String Command = b.getString("Command");
		if(Command == null)  return;
		String Url =  b.getString("Url");
		if(Url == null)  return;
		
		if(Command.equalsIgnoreCase("OpenWebView")==true){
		//	Log.d("UnityPlayerNativeActivity::OpenWebView", b.getString("Url"));
			if(MarqPlus.GetCurrentActivity() == null) MarqPlus.SetCurrentActivity(this);
			MarqPlus.OpenWebView( b.getString("Url"));	
			intent.removeExtra("Command"); 
			intent.removeExtra("Url"); 
		}
	}

	// This ensures the layout will be correct.
	@Override public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mUnityPlayer.configurationChanged(newConfig);
	}

	// Notify Unity of the focus change.
	@Override public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		mUnityPlayer.windowFocusChanged(hasFocus);
	}

	// For some reason the multiple keyevent type is not supported by the ndk.
	// Force event injection by overriding dispatchKeyEvent().
	@Override public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
			return mUnityPlayer.injectEvent(event);
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    setIntent(intent);
	    //now getIntent() should always return the last received intent
	}
	
	// Pass any events not handled by (unfocused) views straight to UnityPlayer
	@Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
	@Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
	@Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
	/*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }
}