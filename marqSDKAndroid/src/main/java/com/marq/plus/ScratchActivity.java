package com.marq.plus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.marq.plus.unity.MarqPlus;

import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ScratchActivity extends Activity {
	private ImageView ivBackgnd;
	private TextView tvText;
	private ScratchView myView;
	private ProgressBar pbLoading;
	private Button btnExit;
	
	private int screenWidth, screenHeight;

	private Bundle bundle;
	
	private Agent agent;
	private Handler handler;
	private String message  = null;
	private String eventId;
	private String targetId;
	private String deviceId;
	private boolean isLiked;
	private int likeCounts = 0;
	
	private Button btnLike;//TODO: 2014-01-07 add save button
	private Button btnAward;//TODO: 2014-01-07 add save button
	private Button btnShare;//TODO: 2014-01-07 add save button
	private String strFileName;//TODO: 2014-01-07 add save button
	private String strFilePath;//TODO: 2014-01-07 add save button
	private TextView tvLikeCounts = null;
	
	private ProgressBar pbLaoding;
	
	private Bitmap bmBack, bmFront;
	private boolean isTouchIdel = false;
	private Timer idealTimer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN );
		
		agent = new Agent(this);
		
		DisplayMetrics dm = new DisplayMetrics();
		
        dm = this.getResources().getDisplayMetrics();
        
        screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		
		DebugLog.LOGE("dm.widthPixels", Integer.toString(dm.widthPixels));
		DebugLog.LOGE("dm.heightPixels", Integer.toString(dm.heightPixels));
			
		bundle = this.getIntent().getExtras();
		strFileName = String.format("marqScratch%1$d.jpg", System.currentTimeMillis());
		//strFileName = bundle.getString("FILE_PHOTONAME");//TODO: 2014-01-07 add save button
//		strFilePath = "/sdcard/marq2/" + strFileName;
		//Agent agent = new Agent(this);
    	String prefix=agent.getStringFromMetadata(this, "com.marq.plus.ProductPathPrefix"); 
		//strDirPath = Environment.getExternalStorageDirectory() + "/marq2/gallery/";
    	strFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/" + prefix+ "/";		
		
		handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				
				switch(msg.what){
				case Agent.HTML_CODE_READY:
					String jsonResult=msg.obj.toString();
           	        try {
						JSONObject resultObj= new JSONObject(jsonResult);
						String callmethod=resultObj.getString("url");
						if(callmethod.equals(MarqPlus.GetInitConfig().get("LIKE_COLLECTED_API"))==true) {
							isLiked=parseLikeCollected(jsonResult);
							setContentView(R.layout.scratch_main);
							setupViews();							
						}
						else if(callmethod.equals(MarqPlus.GetInitConfig().get("INSERT_LIKE_API"))==true) {
							isLiked = (!isLiked);
							if(isLiked == true) {
								likeCounts = likeCounts+1;
					        	btnLike.setBackgroundResource(R.drawable.btn_scratch_like_on);
							} else {
								likeCounts = likeCounts-1;			
					        	btnLike.setBackgroundResource(R.drawable.btn_scratch_like_off);
							}	
							
					        if (tvLikeCounts ==null) 
					        	tvLikeCounts= new TextView(ScratchActivity.this);
					        
					        SpannableString spanString = new SpannableString(String.valueOf(likeCounts));
					        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
					        spanString.setSpan(new TypefaceSpan("arial"), 0, spanString.length(), 0);
					        spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					        tvLikeCounts.setText(spanString);
						}
						else if(callmethod.equals(MarqPlus.GetInitConfig().get("LIKE_COUNT_API"))==true) {
							likeCounts=parseLikeCount(jsonResult);
							
					        if (tvLikeCounts ==null) 
					        	tvLikeCounts= new TextView(ScratchActivity.this);		
					        
					        SpannableString spanString = new SpannableString(String.valueOf(likeCounts));
					        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
					        spanString.setSpan(new TypefaceSpan("arial"), 0, spanString.length(), 0);
					        spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					        tvLikeCounts.setText(spanString);						
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						DebugLog.LOGE("MarqPlus::ScratchActivity::onCreate", e.toString());	
					}
           	        
					break;
				case Agent.TOUCH_IDEL:
					isTouchIdel = true;
					break;
				case Agent.TOUCH_ACTIVE:
					isTouchIdel = false;
					break;					
				}
			}
			
		};
		
		agent.setHandlerCallback(handler);
        eventId = bundle.getString("EVENT_ID");
        targetId = bundle.getString("TARGET_ID");
        message = bundle.getString("MSG");
        deviceId = Agent.getInstallIdWithContext(this);
        
		//setContentView(R.layout.loading_layout);
		bmBack = bitmapResize(BitmapFactory.decodeFile(bundle.getString("BACKGROUND_PATH")));
		bmFront = BitmapFactory.decodeFile(bundle.getString("IMAGE_PATH"));
		
		DebugLog.LOGE("bmBack Width = ", Integer.toString(bmBack.getWidth()));
		DebugLog.LOGE("bmBack Height = ", Integer.toString(bmBack.getHeight()));
		
		
		//pbLaoding = (ProgressBar) findViewById(R.id.pbLoading);
		String likeUrl=MarqPlus.GetInitConfig().get("LIKE_COLLECTED_API");
		Map<String,String> formData = new  HashMap<String,String>();
		 
		formData.put("device_id", deviceId);
		formData.put("event_id", eventId);
		agent.postHtmlByHttpClient(likeUrl, formData);
		

		String cntLikeUrl=MarqPlus.GetInitConfig().get("LIKE_COUNT_API");
		Map<String,String> cntFormData = new  HashMap<String,String>();
		 
		cntFormData.put("event_id", eventId);
		agent.postHtmlByHttpClient(cntLikeUrl, cntFormData);
		
//        new Thread(){

//			@Override
//			public void run() {
				// TODO Auto-generated method stub
//				super.run();
				
//				FileDownloadAgent fdaBack = new FileDownloadAgent(bundle.getString("BACK_PATH"));
//				FileDownloadAgent fdaFront = new FileDownloadAgent(bundle.getString("FRONT_PATH"));
				
//				while(!fdaBack.isFileReady() || !fdaFront.isFileReady()) ;

//				bmBack = bitmapResize(BitmapFactory.decodeFile(bundle.getString("BACKGROUND_PATH")));
//				bmFront = BitmapFactory.decodeFile(bundle.getString("IMAGE_PATH"));
				
//				DebugLog.LOGE("bmBack Width = ", Integer.toString(bmBack.getWidth()));
//				DebugLog.LOGE("bmBack Height = ", Integer.toString(bmBack.getHeight()));

				
//				Message msg = new Message();

//				msg.what = 0;
//				handler.sendMessage(msg);
//			}
        	
//        }.start();



	        
        idealTimer = new Timer();
        idealTimer.schedule(new TimerTask() {          
            @Override
            public void run() {
                TimerMethod();
            }

        }, 0, 3000);
	}
	private boolean parseLikeCollected(String jsonString) {
		JSONObject resultObj;
		try {
			resultObj = new JSONObject(jsonString);
			String status=resultObj.getString("status");
			if(status.equals("success")==true) {
				return true;
			}
			else {
				return false;
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		}
	}
	
	private int parseLikeCount(String jsonString) {

		int result=0;
		try {
			JSONObject resultObj = new JSONObject(jsonString);
			JSONObject data=resultObj.getJSONObject("data");
			JSONObject event=data.getJSONObject("event");
			result=event.getInt("num_collections");
			return result;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return result;
		}
	}
	private void setupViews(){
		
		RelativeLayout titleLayout = (RelativeLayout)findViewById(R.id.scratchTitle);
		RelativeLayout bottomLayout = (RelativeLayout)findViewById(R.id.scratchBottom);	
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		titleLayout.getLayoutParams().height = dm.heightPixels /10;
		bottomLayout.getLayoutParams().height = (int)((double)dm.heightPixels /11.7647);		
		
		ivBackgnd = (ImageView) findViewById(R.id.ivScratchBack);

		ivBackgnd.setImageBitmap(bmBack);
		
		DebugLog.LOGE("ivBackgnd Width = ", Integer.toString(ivBackgnd.getWidth()));
		DebugLog.LOGE("ivBackgnd Height = ", Integer.toString(ivBackgnd.getHeight()));
		
		
        myView = new ScratchView(this, handler, screenWidth, screenHeight, bmFront.getWidth(), bmFront.getHeight(), bmFront, 0, 0);
        
        addContentView(myView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        ImageView ivTitle = new ImageView(this);
        ivTitle.setBackgroundResource(R.drawable.img_scratch_title);
        FrameLayout.LayoutParams fmlpTitle = new FrameLayout.LayoutParams(dm.widthPixels, titleLayout.getLayoutParams().height);
        fmlpTitle.setMargins( 0 , 0 , 0, 0 );
        //titleLayout.addView(ivTitle);
        addContentView(ivTitle, fmlpTitle);
        
        ImageView ivBottom = new ImageView(this);
        ivBottom.setBackgroundResource(R.drawable.img_scratch_bottom);
        FrameLayout.LayoutParams fmlpBottom = new FrameLayout.LayoutParams(dm.widthPixels, bottomLayout.getLayoutParams().height);
        fmlpBottom.setMargins( 0 , dm.heightPixels -  bottomLayout.getLayoutParams().height , 0, 0 );
        //bottomLayout.addView(ivBottom);
        addContentView(ivBottom, fmlpBottom);
        
        Bitmap bmCancel = BitmapFactory.decodeResource(getResources(), R.drawable.btn_scratch_exit);
        
        btnExit = new Button(this);
		//btnExit = (Button) findViewById(R.id.btnScratchExit);
		//btnExit.setEnabled(true);
        btnExit.setBackgroundResource(R.drawable.btn_scratch_exit);
        btnExit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DebugLog.LOGE("Exit", "Clicked");
				finish();
				MarqPlus.OnCloseScratch();
			}
        	
        });

        
        FrameLayout.LayoutParams fmlpClose = new FrameLayout.LayoutParams(screenWidth*46/1000, screenHeight*46/1600);
        fmlpClose.setMargins(screenWidth*914/1000, titleLayout.getLayoutParams().height/2- bmCancel.getHeight()/2, 0, 0 );
        
        DebugLog.LOGE("screenWidth - 101 - bmCancel.getWidth()", Integer.toString(screenWidth - 20 - bmCancel.getWidth()));

        addContentView(btnExit, fmlpClose);
        
        Bitmap bmLike;
        if(this.isLiked == true) {
            bmLike= BitmapFactory.decodeResource(getResources(), R.drawable.btn_scratch_like_on);       	
        }
        else {
            bmLike= BitmapFactory.decodeResource(getResources(), R.drawable.btn_scratch_like_off);      	
        }

        //TODO: 2014-01-07 add save button
        btnLike = new Button(this);
        if(this.isLiked == true) {
        	btnLike.setBackgroundResource(R.drawable.btn_scratch_like_on);
        }
        else {
        	btnLike.setBackgroundResource(R.drawable.btn_scratch_like_off);
        }
        //btnLike.setBackgroundResource(R.drawable.btn_savelocal);
        btnLike.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String insertLikeUrl=MarqPlus.GetInitConfig().get("INSERT_LIKE_API");
				Map<String,String> formData = new  HashMap<String,String>();
				 
				formData.put("device_id", deviceId);
				formData.put("event_id", eventId);
				agent.postHtmlByHttpClient(insertLikeUrl, formData);

			}
			
		});
        

        int textPixel = (int)((double)dm.heightPixels /33.33);
        double btnLike_width=screenWidth*97/1000;
        double btnLike_height=screenHeight*90/1600;
        double scale = (double)((double)bottomLayout.getLayoutParams().height/1.3)/(double)bmLike.getHeight();
        FrameLayout.LayoutParams fmlpLike = new FrameLayout.LayoutParams((int)((double)btnLike_width), (int)((double)btnLike_height));
        int fmlpLikeLeft = (int)((double)screenWidth*100/1000);
        int fmlpLikeTop =  screenHeight - bottomLayout.getLayoutParams().height + (int)(14.0*scale);
        fmlpLike.setMargins( fmlpLikeLeft , fmlpLikeTop , 0, 0 );

        addContentView(btnLike, fmlpLike);
        
        
        tvLikeCounts = new TextView(this);
        
        SpannableString spanString = new SpannableString(String.valueOf(likeCounts));
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        spanString.setSpan(new TypefaceSpan("arial"), 0, spanString.length(), 0);
        spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLikeCounts.setText(spanString);

        
        FrameLayout.LayoutParams fmlpText = new FrameLayout.LayoutParams(textPixel*5, textPixel);
        int fmlpTextLeft = (int)((double)screenWidth / 5.0 *1.0) + bmLike.getWidth() /2 ;
        int fmlpTextTop =  screenHeight - bottomLayout.getLayoutParams().height/2 - (int)textPixel/2;
        
        fmlpText.setMargins( fmlpTextLeft , fmlpTextTop , 0, 0 );
        
        addContentView(tvLikeCounts, fmlpText);       
        
        Bitmap bmAward= BitmapFactory.decodeResource(getResources(), R.drawable.btn_scratch_award);

        //TODO: 2014-01-07 add save button
        btnAward = new Button(this);
        btnAward.setBackgroundResource(R.drawable.btn_scratch_award);

        //btnLike.setBackgroundResource(R.drawable.btn_savelocal);
        btnAward.setOnClickListener(new OnClickListener(){
         
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
//				MarqPlus.OnCloseScratch();
				String couponView=MarqPlus.GetInitConfig().get("COUPON_VIEW");
				MarqPlus.OnGoToWebview(couponView);
			}
			
		});
        
        double btnAward_width=screenWidth*97/1000;
        double btnAward_height=screenHeight*93/1600;
        FrameLayout.LayoutParams fmAward = new FrameLayout.LayoutParams((int)((double)btnAward_width), (int)((double)btnAward_height));
        int fmAwardLeft = (int)((double)screenWidth / 2.0) - (int)((double)btnAward_width/2);
        int fmAwardTop =  screenHeight - bottomLayout.getLayoutParams().height + (int)(14.0*scale);
        fmAward.setMargins( fmAwardLeft , fmAwardTop , 0, 0 );
        
        addContentView(btnAward, fmAward);
        

        Bitmap bmShare= BitmapFactory.decodeResource(getResources(), R.drawable.btn_scratch_share);

        //TODO: 2014-01-07 add save button
        btnShare = new Button(this);
        btnShare.setBackgroundResource(R.drawable.btn_scratch_share);

        //btnLike.setBackgroundResource(R.drawable.btn_savelocal);
        btnShare.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MarqPlus.ShareImage(bundle.getString("BACKGROUND_PATH"));
			}
			
		});
        
        double bmShare_width=screenWidth*90/1000;
        double bmShare_height=screenHeight*90/1600;
        FrameLayout.LayoutParams fmShare = new FrameLayout.LayoutParams((int)bmShare_width, (int)bmShare_height);
        int fmShareLeft = (int)((double)screenWidth*810/1000);
        int fmShareTop =  screenHeight - bottomLayout.getLayoutParams().height + (int)(14.0*scale);
        fmShare.setMargins( fmShareLeft , fmShareTop , 0, 0 );
        addContentView(btnShare, fmShare);  
        
        bmShare.recycle();        
        bmAward.recycle();
        bmLike.recycle();
        bmCancel.recycle();
        
        if(message != null) {
        	new DialogAgent().createDialogMessage(this, "", message);
        }
	}
	
	private Bitmap createBitmapFromARGB(int colorARGB, int width, int height) {
        int[] argb = new int[width * height];

        for (int i = 0; i < argb.length; i++) {

            argb[i] = colorARGB;

        }
        return Bitmap.createBitmap(argb, width, height, Config.ARGB_8888);
    }
	
	private Bitmap bitmapResize(Bitmap src){
		DebugLog.LOGE("src.widthPixels", Integer.toString(src.getWidth()));
		DebugLog.LOGE("src.heightPixels", Integer.toString(src.getHeight()));

		Matrix mx = new Matrix();
	
		float fScale = (float) screenWidth / (float) src.getWidth();
		
		DebugLog.LOGE( "fScale", Float.toString(fScale));
		
		mx.setScale(fScale, fScale);
		
		Bitmap bmResized = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), mx, true);
		

		DebugLog.LOGE( "bmResized.height", Integer.toString(bmResized.getHeight()) );
		DebugLog.LOGE( "bmResized.width", Integer.toString(bmResized.getWidth()) );
	
		return bmResized;

	}
	
	//TODO: 2014-01-07 add save button
	private void saveToSDcard(){
		//File fileSDcard; 

		FileOutputStream outStream = null;
			
		String strPhotoPathToDCIM;
			
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
		bmBack.compress(Bitmap.CompressFormat.PNG, 100, baos); //TODO: for 2014-0107
		
		byte[] data = baos.toByteArray();
			
		if(Environment.getExternalStorageDirectory().equals(Environment.MEDIA_REMOVED)){
			new AlertDialog.Builder(ScratchActivity.this)
			.setTitle("Error Message")
	        .setMessage("SD Card can't be found")
	        .setPositiveButton(R.string.string_ok, null)
	        .show();
			
			return;
		}
		//else
		//	fileSDcard = Environment.getExternalStorageDirectory();
		
		//Agent agent = new Agent(this);
    	String productPrefix=agent.getStringFromMetadata(this, "com.marq.plus.ProductPathPrefix");		
    	String scratchPathPrefix=agent.getStringFromMetadata(this, "com.marq.plus.ScratchPathPrefix"); 
		//strDirPath = Environment.getExternalStorageDirectory() + "/marq2/gallery/";
		String strDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/"+ productPrefix+ "/" + scratchPathPrefix +"/";

		File myDataPath = new File(strDirPath);
		
		if(!myDataPath.exists()) {
			
			DebugLog.LOGE("ScratchActivity::saveToSDcard()", "create directory");
			myDataPath.mkdirs();
			
		}
		
		strPhotoPathToDCIM = strDirPath + strFileName;
		
		DebugLog.LOGE("strPhotoPathToDCIM = ", strPhotoPathToDCIM);
			
			
		try {
			outStream = new FileOutputStream(new File(strPhotoPathToDCIM));
			outStream.write(data);
			outStream.close();
			
			addPhotoToGallery(strPhotoPathToDCIM);
			
			new AlertDialog.Builder(ScratchActivity.this)
			//.setTitle(strFileName)
			.setMessage(R.string.msg_photo_save_success)
		    .setPositiveButton(R.string.string_ok, null)
		    .show();
			
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+strPhotoPathToDCIM)));	
				
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			DebugLog.LOGE("Save Photo", "FileNotFoundException");
			
			new AlertDialog.Builder(ScratchActivity.this)
			.setTitle("Error Message")
	        .setMessage(R.string.msg_photo_save_fail + " - 1.")
	        .setPositiveButton(R.string.string_ok, null)
	        .show();
			
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			DebugLog.LOGE("Save Photo", "IOException");
			
			new AlertDialog.Builder(ScratchActivity.this)
			.setTitle("Error Message")
	        .setMessage(R.string.msg_photo_save_fail + " - 2.")
	        .setPositiveButton(R.string.string_ok, null)
	        .show();
			
		}

	}
		
	//TODO: 2014-01-07 add save button
	private void addPhotoToGallery(String mCurrentPhotoPath) {
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);
	    
	    mediaScanIntent.setData(contentUri);
		    
	    this.sendBroadcast(mediaScanIntent);
	}
	
	private void TimerMethod() {
		// This method is called directly by the timer
		// and runs in the same thread as the timer.

		// We call the method that will work with the UI
		// through the runOnUiThread method.
		runOnUiThread(new Runnable() {
			public void run() {
				// This method runs in the same thread as the UI.
				// Do something to the UI thread here
				
			}
		});
	}

}