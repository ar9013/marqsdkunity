package com.marq.plus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.marq.plus.unity.MarqPlus;
/*
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
*/
//import android.webkit.WebView;

public class MarqWebView extends Activity {

//	private Session.StatusCallback statusCallback = new SessionStatusCallback();
    
//    private GraphUser user;
//    private ProfilePictureView profilePictureView;

    private Button btnStartScan, btnFBLogout, btnGallery, btnAboutMarq, btnContactUs, btnMoreApps;
    private ImageView ivTitleSocial, ivHeadPhoto, ivHeadPhotoBackground, ivFBBackground;
    private TextView tvSocialName;
    
    private Drawable draw;
    
    private int maxWidthHeadPhoto, defaultHeightHeadPhotoBackground;
    
    private ToggleButton tglbtnCGM;
    
    private Agent agent;
    
    private Button btnGoBack;
	private WebView webview;
	private TextView tvTitle;
	
	private Handler webCallback;
	
	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.marq_webcontents);
		
		init();
		initWebUIs();
/*		
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }
*/
        //updateView();
	

		WebSettings webSettings = webview.getSettings();
		
		//webSettings.setSupportZoom(true);
		//webSettings.setBuiltInZoomControls(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		
		//webSettings.setRenderPriority(RenderPriority.HIGH);
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		
		webview.addJavascriptInterface(new WebCallAPI(this, webCallback), "android");
		
		webview.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				
				view.loadUrl(url);
				
				setGoBackForward();
				
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				
				//if(view.getTitle() != null && view.getTitle().length() > 0)
				//	tvTitle.setText(view.getTitle());
				//else
				//	tvTitle.setText("");
				
				setGoBackForward();
				
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(view, errorCode, description, failingUrl);
				
				loadWebViewError(view, errorCode);
			}

		});
		
        Bundle bundle = this.getIntent().getExtras();
        
        DebugLog.LOGE("MarqWebContents::onCreate::URL_TARGET", bundle.getString("URL_TARGET"));
		
        //webview.loadUrl("file:///android_asset/test.htm");
        
		if(bundle.containsKey("URL_TARGET") && !bundle.getString("URL_TARGET").isEmpty())
			webview.loadUrl(bundle.getString("URL_TARGET"));
		else
			loadWebViewError(webview, -1);

	}

	

	private void initWebUIs(){
		
		webview = (WebView) findViewById(R.id.wvContentsBrowser);		
		
		//tvTitle = (TextView) findViewById(R.id.tvWebContentsTitle);
		
		//btnGoBack = (Button) findViewById(R.id.btnWebContentsGoBack);		
//		btnGoBack.setVisibility(View.GONE);
//		btnGoBack.setOnClickListener(new OnClickListener() {

//			@Override
//			public void onClick(View v) {
				// TODO Auto-generated method stub
//				webview.goBack();
				//webview.reload();
//				tvTitle.setText(webview.getTitle());
//			}
			
//		});
		
		webCallback = new Handler(){

			@SuppressWarnings("unused")
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				
				switch(msg.what){
				
				case WebCallAPI.WEB_INIT:
					String uiid=Agent.getInstallIdWithContext(MarqWebView.this);	
					String lang = Locale.getDefault().getLanguage();
					if(lang.equals("zh")==true) {
						lang = Locale.getDefault().getCountry().toLowerCase();
					}
					webReturnMessage("{\"source\":\"init\", \"udid\":\""+uiid+"\", \"language\":\""+lang+"\"}");
					updateView();	
					break;
				
				//case WebCallAPI.FB_LOGIN:
				//	onClickFBLogin();
				//	break;
					
				//case WebCallAPI.FB_LOGOUT:
				//	onClickFBLogout();
				//	break;
				//TODO: Implement the System Share						
				//case WebCallAPI.FB_INVITE:
				//	DebugLog.LOGE("FB_INVITE::bundle", msg.obj.toString());
				//	shareToFacebook((Bundle) msg.obj);
				//	break;
				//TODO: Implement the System Share					
				//case WebCallAPI.FB_SHARE_LINK:
				//	shareToFacebook((Bundle) msg.obj);
				//	break;
				//TODO: Implement the System Photo Gallery
				//case WebCallAPI.RECORDS_GALLERY:
					//startActivityForResult(new Intent(MarqWebViewTest.this, MarqGalleryList.class), Agent.START_SCAN);
					//break;
					
				case WebCallAPI.RECORDS_GROUPON:
					DebugLog.LOGE("WebCallAPI.RECORDS_GROUPON", "Called");
					break;
					
				case WebCallAPI.RECORDS_SCAN:
					DebugLog.LOGE("WebCallAPI.RECORDS_SCAN", "Called");
					break;
					
				case WebCallAPI.OPEN_QFUN:
					MarqPlus.OnGoToQFun();
					finish();
					break;
					
				case WebCallAPI.OPEN_URL:
					{
						Map<String,String> argv=((HashMap<String,String>)msg.obj);

						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(argv.get("url")));
						startActivity(intent);
					}
					break;

				case WebCallAPI.OPEN_MAP:
					{
						Map<String,String> argv=(HashMap<String,String>)msg.obj;
						
						String strGeo = "geo:";
				    	
				    	strGeo += argv.get("lat");
				    	strGeo += ",";
				    	strGeo += argv.get("lng");
				    	strGeo += "?q=";
				    	strGeo += argv.get("lat");;
				    	strGeo += ",";
				    	strGeo += argv.get("lng");;
				    	strGeo = strGeo + "(" + argv.get("text") +")";
				    	
						Intent intent_map = new Intent(Intent.ACTION_VIEW, Uri.parse(strGeo));
						startActivity(intent_map);
					}
					break;
					
				case WebCallAPI.OPEN_PHONEDAILER:
					{
						Map<String,String> argv=(HashMap<String,String>)msg.obj;
						
						Intent intent_phone = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + argv.get("text")));
						startActivity(intent_phone);	
					}
					break;
					
				case WebCallAPI.OPEN_MAIL:
					{
						Map<String,String> argv=(HashMap<String,String>)msg.obj;						
						Intent intent_mail = new Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto", argv.get("text"), null));
						startActivity(intent_mail);		
					}
					break;
					
				case WebCallAPI.OPEN_GALLERY:
					{
						Intent intent = new Intent();	
						intent.setClass(MarqWebView.this, MarqGalleryList.class);	
						startActivityForResult(intent, Agent.START_SCAN);	
					}
					break;
				
				case WebCallAPI.OPEN_PHOTOFRAME:
					{
						Map<String,String> argv=(HashMap<String,String>)msg.obj;						
						MarqPlus.OnGoToPhotoFrame( argv.get("target_id"),argv.get("button_id"));
						finish();
					}
				break;
				
				case WebCallAPI.ARSCAN:
					{
						
						MarqPlus.OnGoToScanMode(1);
						finish();
					}
					break;
				case WebCallAPI.CLOSE_WEBVIEW:
					{
						finish();
					}
					break;
				case WebCallAPI.OPEN_3D_MODULE:
				   {
					    Map<String,String> argv=(HashMap<String,String>)msg.obj;
					    MarqPlus.OnGoTo3DModule(argv.get("target_id"),argv.get("button_id"));
					    finish();
				   }
				case WebCallAPI.OPEN_TR_ON:
				   {
					    Map<String,String> argv=(HashMap<String,String>)msg.obj;
					    MarqPlus.OnGoToTryOn(argv.get("target_id"),argv.get("button_id"));
					    finish();
				   }
				case WebCallAPI.SHARE_IMAGE:
					{
						final Map<String,String> argv=(HashMap<String,String>)msg.obj;	


						
				        new Thread(){
						@Override
						public void run() {
								try {
									Agent agent = new Agent(MarqWebView.this);
							    	String productPrefix=agent.getStringFromMetadata(MarqWebView.this, "com.marq.plus.ProductPathPrefix");	
									URL imgUrl = new URL(argv.get("url"));
									if (imgUrl == null) {
										throw new FileNotFoundException("Can not get file form url");
									}
									String strUrl = imgUrl.toString();
									
									String strFileName = URLUtil.guessFileName(strUrl, null, "image/png");
							        
									Bitmap bitMap = BitmapFactory.decodeStream(imgUrl
											.openConnection().getInputStream());
									ByteArrayOutputStream baos = new ByteArrayOutputStream();
									bitMap.compress(Bitmap.CompressFormat.PNG, 100, baos); 
									byte[] data = baos.toByteArray();
			
									String strDirPath = Environment
											.getExternalStoragePublicDirectory(
													Environment.DIRECTORY_DCIM)
											.getAbsolutePath()
											+ "/" + productPrefix + "/";
									
									String strFilePath = strDirPath + strFileName;
									File dataPath = new File(strDirPath);
			
									if (!dataPath.exists()) {
			
										DebugLog.LOGE("ScratchActivity::saveToSDcard()",
												"create directory");
										dataPath.mkdirs();
									}
									File filePathHandler =  new File(strFilePath);
									FileOutputStream outStream = new FileOutputStream(filePathHandler);
									outStream.write(data);
									outStream.close();
									
									String imgUri=Uri.fromFile(filePathHandler).toString();
									
									Map<String, String> argv = new HashMap<String, String>();
									argv.put("url", imgUri);
									
									Message msg = new Message();								
									msg.obj = argv;
									msg.what = WebCallAPI.IMG_DOWNLOADED;								
									webCallback.sendMessage(msg);
									
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									DebugLog.LOGE("MarqPlus",
											"Share Failed: " + e.toString());
								} catch (IOException e) {
									// TODO Auto-generated catch block
									DebugLog.LOGE("MarqPlus",
											"Share Failed: " + e.toString());
								}
							}
				        }.start();

					}
					break;
				case WebCallAPI.IMG_DOWNLOADED:
					{
						final Map<String,String> argv=(HashMap<String,String>)msg.obj;	
						Intent share_intent = new Intent();
					    share_intent.setAction(Intent.ACTION_SEND);
					    share_intent.setType("image/png");
					    share_intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(argv.get("url")));
					    share_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					    try {
					    	startActivity(Intent.createChooser(share_intent,getString(R.string.dialog_shared_type_title)));
					    } catch (android.content.ActivityNotFoundException ex) {
				    		DebugLog.LOGE("MarqPlus", "Share Failed");	    	    	  
					    }							
						finish();						
					}
					break;
				case WebCallAPI.SETTING_PUSH_MESSAGE_ON:
					tglbtnCGM.setChecked(true);
					agent.setGCMReceive(true);
					break;
					
				case WebCallAPI.SETTING_PUSH_MESSAGE_OFF:
					tglbtnCGM.setChecked(false);
					agent.setGCMReceive(false);
					break;
					
				default:
						
				}
			}
			
		};
	}
//TODO::Implement the System Share	
//	private void shareToFacebook(Bundle bundle){
//		Intent intent = new Intent();
//		intent.setClass(MarqWebViewTest.this, MarqUploadShare.class);
//		
//		intent.putExtras(bundle);
//		startActivity(intent);
//	}
	
	private void loadWebViewError(WebView wv, int error){
		
		String htmlCode = "";
		
		try {
			switch(error){
			case -1:
				htmlCode = URLDecoder.decode(
						"<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>Error!!!</title><body><font size=\"7\">Error " + 
							" occue in init. <br>Please Call Engineer.</font></body></html>", "utf-8");
				break;
			case -2:
				htmlCode = URLDecoder.decode(
						"<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>Error!!!</title><body><font size=\"7\">Error " + 
							" occur in internet coonnection. <br>Please check your wifi.</font></body></html>", "utf-8");
				break;
				
			default:
				htmlCode = URLDecoder.decode(
						"<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>Error!!!</title><body><font size=\"7\">Error Code (" + 
							Integer.toString(error) + ") occur. <br>Please Call Engineer.</font></body></html>", "utf-8");
			
			}
			
			wv.loadData(htmlCode, "text/html", "utf-8");
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setGoBackForward(){
		//if(webview.canGoBack())
			//btnGoBack.setVisibility(View.VISIBLE);
		//else
			//btnGoBack.setVisibility(View.GONE);
	}
	
    private void init(){
    	agent = new Agent(MarqWebView.this);
    	
    	DebugLog.LOGE("init()", "called");
//    	tvSocialName = (TextView) findViewById(R.id.tvWebContentsSocialName);

//    	ivTitleSocial = (ImageView) findViewById(R.id.ivWebContentsTitleSocial);
//    	ivHeadPhotoBackground = (ImageView) findViewById(R.id.ivWebContentsHeadPhotoBackground);
    	
//    	profilePictureView = (ProfilePictureView) findViewById(R.id.fbProfilePicture);
//      profilePictureView.setCropped(true);
    	
//    	ivHeadPhoto = (ImageView) findViewById(R.id.ivWebContentsHeadPhoto);
    	//ivHeadPhoto.setOnClickListener(new OnClickListener(){

		//	@Override
		//	public void onClick(View v) {
				// TODO Auto-generated method stub
		//		onClickFBLogin();
		//	}
    		
    	//});
    	
//    	ivFBBackground = (ImageView) findViewById(R.id.ivWebContentsSocialFbBackground);
    	//ivFBBackground.setOnClickListener(new OnClickListener(){

		//	@Override
		//	public void onClick(View v) {
				// TODO Auto-generated method stub
		//		onClickFBLogin();
		//	}
    		
    	//});
    	
//    	btnStartScan = (Button) findViewById(R.id.btnWebContentsStartScan);
//    	btnStartScan.setOnClickListener(new OnClickListener(){

//			@Override
//			public void onClick(View v) {
				// TODO Auto-generated method stub
//				finish();
//			}
    		
//    	});
    	
//    	btnFBLogout = (Button) findViewById(R.id.btnWebContentsSocialFacebookLogout);
    	//btnFBLogout.setOnClickListener(new OnClickListener(){

		//	@Override
		//	public void onClick(View v) {
				// TODO Auto-generated method stub
		//		onClickFBLogout();
		//	}
    		
    	//});
    	
//    	btnGallery = (Button) findViewById(R.id.btnWebContentsMyGallery);
    	//btnGallery.setOnClickListener(new OnClickListener(){

		//	@Override
		//	public void onClick(View v) {
				// TODO Auto-generated method stub
				// TODO Implement the system Photo
				//startActivityForResult(new Intent(MarqWebViewTest.this, MarqGalleryList.class), Agent.START_SCAN);
		//	}
    		
    	//});
    	
//    	btnAboutMarq = (Button) findViewById(R.id.btnWebContentsAboutMarqPlus);
    	//btnAboutMarq.setOnClickListener(new OnClickListener(){

		//	@Override
		//	public void onClick(View v) {
				// TODO Auto-generated method stub
				// About has been implemented in WebView
				//startActivityForResult(new Intent(MarqWebViewTest.this, MarqAboutmarq.class), Agent.START_SCAN);
		//	}
    		
    	//});
    	
//    	btnContactUs = (Button) findViewById(R.id.btnWebContentsContactUs);
    	//btnContactUs.setOnClickListener(new OnClickListener(){

		//	@Override
		//	public void onClick(View v) {
				// TODO Auto-generated method stub
		//		Intent intent = new Intent(Intent.ACTION_SENDTO,
		//				Uri.fromParts("mailto", "service@arplanet.com.tw", null));
		//		startActivity(intent);
		//	}
    		
    	//});
    	
//    	btnMoreApps = (Button) findViewById(R.id.btnWebContentsMoreApps);
    	//btnMoreApps.setOnClickListener(new OnClickListener(){

		//	@Override
		//	public void onClick(View v) {
				// TODO Auto-generated method stub
		//		Intent intent = new Intent();
		//		intent.setClass(MarqWebView.this, MarqWebBrowser.class);
				
		//		Bundle bundle = new Bundle();

		//		bundle.putString("URL_TARGET", "https://play.google.com/store/search?q=arplanet");
				
		//		intent.putExtras(bundle);
				
		//		startActivity(intent);
		//	}
    		
    	//});

//    	tglbtnCGM = (ToggleButton) findViewById(R.id.tglbtnGCM);
    	//tglbtnCGM.setOnCheckedChangeListener(new OnCheckedChangeListener(){

		//	@Override
		//	public void onCheckedChanged(CompoundButton buttonView,
		//			boolean isChecked) {
				// TODO Auto-generated method stub
				
		//		if(isChecked)
		//			agent.setGCMReceive(true);
		//		else
		//			agent.setGCMReceive(false);
				
		//	}
    		
    	//});
    	
    	//if(agent.isGCMReceive())
    	//	tglbtnCGM.setChecked(true);
    	//else
    	//	tglbtnCGM.setChecked(false);
    	
    }


	
	
    @Override
    public void onStart() {
        super.onStart();
//        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        MarqPlus.OnCloseWebView();
//        Session.getActiveSession().removeCallback(statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        
        if(requestCode == Agent.START_SCAN && resultCode == RESULT_OK){
			Bundle bData = data.getExtras();
			
			if(bData != null){
				boolean bExit = bData.getBoolean("SCAN");
				
				if(bExit) {
					MarqPlus.OnGoToScanMode(0);
					finish();
				}
			}
		}
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        Session session = Session.getActiveSession();
//        Session.saveSession(session, outState);
    }
    
/*    
    private void userNewMeRequest(final Session session){
    	// Make an API call to get user data and define a new callback to handle the response
  	
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback(){

			@Override
			public void onCompleted(GraphUser user, Response response) {
				// TODO Auto-generated method stub
				if(user != null){
					MarqWebViewTest.this.user = user;
					
					tvSocialName.setText(user.getName());
					
					profilePictureView.setProfileId(user.getId());
					profilePictureView.setVisibility(View.VISIBLE);
					
					webReturnMessage("{\"source\":\"login_fb\"," +
							"\"id\":\"" + user.getId() + "\"," +
							"\"username\":\"" + user.getName() + "\"," +
							"\"profile_pic\":\"" + "https://graph.facebook.com/" + user.getId() + "/picture?type=square\""+
							"}");
				}
				
				if(response.getError() != null){
					// Error Handle
				}
			}

        	
        });
        
        request.executeAsync();

    }
*/
    
	private void updateView() {
/*		
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
        	
        	btnFBLogout.setVisibility(View.VISIBLE);
    		tvSocialName.setVisibility(View.VISIBLE);
    		ivHeadPhoto.setVisibility(View.GONE);
    		ivFBBackground.setClickable(false);
            
            userNewMeRequest(session);
 
        } 
        else {
        	
        	btnFBLogout.setVisibility(View.GONE);
    		tvSocialName.setVisibility(View.GONE);
    		ivHeadPhoto.setVisibility(View.VISIBLE);
    		ivFBBackground.setClickable(true);
            
            user = null;
            profilePictureView.setProfileId(null);
            profilePictureView.setVisibility(View.GONE);
            
        }
        */
    }

    private void onClickFBLogin() {
/*    	
        Session session = Session.getActiveSession();
        
        if (!session.isOpened() && !session.isClosed()) 
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        else 
            Session.openActiveSession(this, true, statusCallback);
            */
        
    }

    private void onClickFBLogout() {
 /*   	
        Session session = Session.getActiveSession();
        
        if (!session.isClosed()) 
            session.closeAndClearTokenInformation();
        
        
        webReturnMessage("{\"source\":\"logout_fb\"}");
        */
    }
    
    private void webReturnMessage(String message){
    	
    	DebugLog.LOGE("MarqWebViewTest::webReturnMessage()", message);
    	
    	webview.loadUrl("javascript:marq.appMessage(" + message + ")");
    }
/*
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        	if(exception instanceof FacebookOperationCanceledException ||
                    exception instanceof FacebookAuthorizationException) {
                        new AlertDialog.Builder(MarqWebViewTest.this)
                            .setTitle(R.string.fb_exception)
                            .setMessage(R.string.contact_arplanet)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									finish();
								}
                            	
                            })
                            .show();
            }
        	else
        		updateView();
        }
    }
*/    
    
 /*   
	private void process_Qfun(){
		
		new Agent(MarqWebViewTest.this, new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				
				if(msg.what != Agent.HTML_CODE_READY){
					AlertDialog.Builder builder =  new AlertDialog.Builder(MarqWebViewTest.this);
					
					builder.setTitle("Exception!!!");
					builder.setPositiveButton("OK", null);
					
					builder.create().show();
					
					return;
				}
				
				Log.e("msg.obj = ", msg.obj.toString());
				
				try {
					JSONObject jsonCommand = new JSONObject(msg.obj.toString());
					
					actQfun(
							jsonCommand.getBoolean("front_camera"),
							jsonCommand.getString("intro"),
							jsonCommand.getString("photo_masks"), "", "" );
					
//					DebugLog.LOGE("frontCamOn", jsonCommand.getString("front_camera") == "true" ? "true" : "false");
//					DebugLog.LOGE("path_Intro", jsonCommand.getString("intro"));
//					DebugLog.LOGE("path_PhotoMasks", jsonCommand.getString("photo_masks"));
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}).getHtmlByHttpClient(Agent.QFUN_URL, "");

        

	}
*/
/*	
	private void actQfun(
			boolean frontCamOn, 
			String path_Intro,  
			String path_PhotoMasks,
			String fanspage_id,
			String shareText){
		
		DebugLog.LOGE("frontCamOn", frontCamOn ? "True" : "False");
		DebugLog.LOGE("path_Intro", path_Intro);
		DebugLog.LOGE("path_PhotoMasks", path_PhotoMasks);
		DebugLog.LOGE("marq+", "fanspage_id: " + fanspage_id);
		DebugLog.LOGE("marq+", "shareText: " + shareText);
		
		Intent intent = new Intent();
		intent.setClass(MarqWebViewTest.this, QfunLoading.class);
		
		Bundle bundle = new Bundle();
		
		bundle.putBoolean("FRONT_CAM", frontCamOn);
		bundle.putString("INTRO_PATH", path_Intro);
		bundle.putString("PHOTO_MASK_PATH", path_PhotoMasks);
		bundle.putString("FANSPAGE_ID", fanspage_id);//TODO: 2014-01-08
		bundle.putString("SHARE_TEXT", shareText);
		
		intent.putExtras(bundle);
		
		startActivityForResult(intent, Agent.START_SCAN);
	}
*/    
    
    
    
	public class WebCallAPI {
		
		static final int IMG_DOWNLOADED = 1;
		static final int WEB_INIT = 50;
		
		//static final int FB_LOGIN = 101;
		//static final int FB_LOGOUT = 102;
		//static final int FB_INVITE = 103;
		//static final int FB_SHARE_LINK = 104;
		
		
		//static final int TWITTER_LOGIN = 103;
		//static final int TWITTER_LOGOUT = 104;
		
		static final int RECORDS_GALLERY = 201;
		static final int RECORDS_GROUPON = 202;
		static final int RECORDS_SCAN = 203;
		
		static final int OPEN_QFUN = 501;
		
		static final int OPEN_URL = 550;
		static final int OPEN_MAP = 551;
		static final int OPEN_PHONEDAILER = 552;	
		static final int OPEN_MAIL = 553;			
		static final int OPEN_GALLERY = 554;	
		static final int OPEN_PHOTOFRAME = 555;			
		static final int ARSCAN =556;
		static final int CLOSE_WEBVIEW =557;
		static final int SHARE_IMAGE =558;	
		static final int OPEN_3D_MODULE=559;
		static final int OPEN_TR_ON=560;
		static final int SETTING_PUSH_MESSAGE_ON = 901;
		static final int SETTING_PUSH_MESSAGE_OFF = 902;
		
		private Context context;
		private Handler callback;
		
		WebCallAPI(Context c, Handler handler) {
			context = c;
			callback = handler;
		}
		
		@JavascriptInterface
		public void showToast(final String s) {
//			runOnUiThread(new Runnable(){
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					Toast.makeText(mContext, "web called : " + s, Toast.LENGTH_LONG).show();
//				}
//				
//			});
			
			Toast.makeText(context, "web called : " + s, Toast.LENGTH_LONG).show();
			
		}
		
		@JavascriptInterface		
		public void webViewMessage(String data) throws JSONException {
			//Toast.makeText(context, data, Toast.LENGTH_LONG).show();
			DebugLog.LOGE("MarqWebViewTest::WebCallAPI::webViewMessage()", data);
			
			JSONObject jsonCommand = new JSONObject(data.toString());
			
			if(jsonCommand.getString("state").matches("showcase") && jsonCommand.getString("action").matches("goback")){
				Log.e("Web Command = ", "Go Back");


				finish();
			}
			
			if(jsonCommand.getString("action").matches("init")){
				callback_send(WEB_INIT);
			}
			
			
			//if(jsonCommand.getString("action").matches("login_fb")){
			//	callback_send(FB_LOGIN);
			//}

			//if(jsonCommand.getString("action").matches("logout_fb")){
			//	callback_send(FB_LOGOUT);
			//}
			
			//if(jsonCommand.getString("action").matches("invite_fb")){
				//JSONObject jsonData = new JSONObject(jsonCommand.getString("content"));
				
			//	Bundle bundle = new Bundle();

			//	bundle.putBoolean("APP_INVITE", true);
	    	//	bundle.putString("APPINVITE_DESCRIPTION", "Marq+, Life plus.");
	    		
	    	//	callback_send(FB_INVITE, bundle);
			//}
			
			//if(jsonCommand.getString("action").matches("share_fb")){
			//	JSONObject jsonData = new JSONObject(jsonCommand.getString("content"));
				
			//	Bundle bundle = new Bundle();
				
			//	bundle.putBoolean("SHARE_CASE", true);				
			//	bundle.putString("SHARECASE_TITLE", " ");
			//	bundle.putString("SHARECASE_DESCRIPTION", jsonData.getString("description"));
			//	bundle.putString("SHARECASE_URL_LINK", " ");
			//	bundle.putString("SHARECASE_URL_PICTURE", jsonData.getString("image"));
	    		
	    	//	callback_send(FB_SHARE_LINK, bundle);
			//}
			
			//if(jsonCommand.getString("action").matches("album")){
			//	callback_send(RECORDS_GALLERY);
			//}
			
			if(jsonCommand.getString("action").matches("open_qfun")){
				callback_send(OPEN_QFUN);
			}
			
			if(jsonCommand.getString("action").matches("open_url")){
				JSONObject jsonData = new JSONObject(jsonCommand.getString("content"));
				Map<String, String> argv = new HashMap<String, String>();
				argv.put("url", jsonData.getString("url"));
				
	    		callback_send(OPEN_URL, argv);
			}

			if(jsonCommand.getString("action").matches("open_map")){
				JSONObject jsonData = new JSONObject(jsonCommand.getString("content"));

				Map<String, String> argv = new HashMap<String, String>();
				argv.put("lat", jsonData.getString("lat"));
				argv.put("lng", jsonData.getString("lng"));
				argv.put("text", jsonData.getString("text"));					
	    		
	    		callback_send(OPEN_MAP, argv);
			}
			
			if(jsonCommand.getString("action").matches("open_phonedailer")){
				JSONObject jsonData = new JSONObject(jsonCommand.getString("content"));
				
				Map<String, String> argv = new HashMap<String, String>();
				argv.put("text", jsonData.getString("text"));

	    		callback_send(OPEN_PHONEDAILER, argv);
			}

			if(jsonCommand.getString("action").matches("open_mail")){
				JSONObject jsonData = new JSONObject(jsonCommand.getString("content"));
				
				Map<String, String> argv = new HashMap<String, String>();
				argv.put("text", jsonData.getString("text"));
				
	    		callback_send(OPEN_MAIL, argv);
			}
			
			if(jsonCommand.getString("action").matches("open_gallery")){
			
	    		callback_send(OPEN_GALLERY);
			}	
			
			if(jsonCommand.getString("action").matches("open_photoframe")){
				JSONObject jsonData = new JSONObject(jsonCommand.getString("content"));
				
				Map<String, String> argv = new HashMap<String, String>();
				argv.put("target_id", jsonData.getString("target_id"));
				argv.put("button_id", jsonData.getString("button_id"));
	    		callback_send(OPEN_PHOTOFRAME, argv);
			}			
						
			if(jsonCommand.getString("action").matches("arscan")){
	    		callback_send(ARSCAN);
			}
			
			if(jsonCommand.getString("action").matches("close_webview")){
	    		callback_send(CLOSE_WEBVIEW);
			}	
			
			if(jsonCommand.getString("action").matches("share_image")){
				JSONObject jsonData = new JSONObject(jsonCommand.getString("content"));
				
				Map<String, String> argv = new HashMap<String, String>();
				argv.put("url", jsonData.getString("url"));
	    		callback_send(SHARE_IMAGE, argv);
			}	
			if(jsonCommand.getString("action").matches("open_3d_module")){
				JSONObject jsonData = new JSONObject(jsonCommand.getString("content"));
				Map<String, String> argv = new HashMap<String, String>();
				argv.put("target_id", jsonData.getString("target_id"));
				argv.put("button_id", jsonData.getString("button_id"));
				callback_send(OPEN_3D_MODULE,argv);
			}
			if(jsonCommand.getString("action").matches("open_tr_on")){
				JSONObject jsonData = new JSONObject(jsonCommand.getString("content"));
				Map<String, String> argv = new HashMap<String, String>();
				argv.put("target_id", jsonData.getString("target_id"));
				argv.put("button_id", jsonData.getString("button_id"));
				callback_send(OPEN_TR_ON,argv);	
			}
			
		}
		
		private void callback_send(int type){
			Message msg = new Message();
			
			//msg.obj = type;
			msg.what = type;
			
			callback.sendMessage(msg);
		}
		
		private void callback_send(int type, Object object){
			Message msg = new Message();
			
			//DebugLog.LOGE("FB_INVITE::callback_send::bundle", bundle.toString());
			
			msg.obj = object;
			msg.what = type;
		
			callback.sendMessage(msg);
		}
	}
    

}
