package com.marq.plus;

import com.marq.plus.unity.MarqPlus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MarqWebBrowser extends Activity {


	private static final String DEFAULT_URL = "http://news.google.com.tw";
	
	private Button btnExit, btnPrePage, btnNextPage, btnBrowser, btnRefresh;
	private WebView webview;
	private TextView tvTitle;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.marq_webbrowser);
		
		initUI();
		
		Bundle bundle = this.getIntent().getExtras();
		
		if(bundle.containsKey("URL_TARGET") && !bundle.getString("URL_TARGET").isEmpty())
			webview.loadUrl(bundle.getString("URL_TARGET"));
		else
			webview.loadUrl(DEFAULT_URL);
		
		
	}
	
	private void initUI(){
		
		webview = (WebView) findViewById(R.id.wvBrowser);		
		
		tvTitle = (TextView) findViewById(R.id.tvWebTitle);		
	    RelativeLayout titleLayout = (RelativeLayout)findViewById(R.id.layoutWebTitle);
		RelativeLayout bottomLayout = (RelativeLayout)findViewById(R.id.layoutWebBottom);		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		titleLayout.getLayoutParams().height = dm.heightPixels /10;
		bottomLayout.getLayoutParams().height = (int)((double)dm.heightPixels /11.7647);
		
		btnExit = (Button) findViewById(R.id.btnWebExit);
		btnPrePage = (Button) findViewById(R.id.btnWebPrePage);
		btnNextPage = (Button) findViewById(R.id.btnWebNextPage);
		btnBrowser = (Button) findViewById(R.id.btnWebBrowser);
		btnRefresh = (Button) findViewById(R.id.btnWebRefresh);
		
		btnPrePage.setEnabled(false);
		btnNextPage.setEnabled(false);
		btnRefresh.setEnabled(true);
		
		RelativeLayout.LayoutParams params_btnExit = (android.widget.RelativeLayout.LayoutParams) btnExit.getLayoutParams();
		params_btnExit.leftMargin=(dm.widthPixels *40/ 1000);
		params_btnExit.topMargin=(dm.heightPixels*48/1600);
		params_btnExit.width=(dm.widthPixels *85/ 1000);
		params_btnExit.height=(dm.heightPixels*75/1600);
		btnExit.setLayoutParams(params_btnExit);
//		expandTouchArea(webbrowser,btnExit,100);
		btnExit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}

			
		});
		RelativeLayout.LayoutParams params_btnPrePage = (android.widget.RelativeLayout.LayoutParams) btnPrePage.getLayoutParams();
		params_btnPrePage.leftMargin=(dm.widthPixels *100/ 1000);
		params_btnPrePage.bottomMargin=(dm.heightPixels*33/1600);
		params_btnPrePage.width=(dm.widthPixels *43/ 1000);
		params_btnPrePage.height=(dm.heightPixels*70/1600);
		btnPrePage.setLayoutParams(params_btnPrePage);
//		expandTouchArea(bottomLayout,btnPrePage,30);
		
		
		btnPrePage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				webview.goBack();

			}
			
		});
		RelativeLayout.LayoutParams params_btnNextPage = (android.widget.RelativeLayout.LayoutParams) btnNextPage.getLayoutParams();
		params_btnNextPage.leftMargin=(dm.widthPixels *280/ 1000);
		params_btnNextPage.bottomMargin=(dm.heightPixels*33/1600);
		params_btnNextPage.width=(dm.widthPixels *43/ 1000);
		params_btnNextPage.height=(dm.heightPixels*70/1600);
		btnNextPage.setLayoutParams(params_btnNextPage);
//		expandTouchArea(bottomLayout, btnNextPage, 30);
		btnNextPage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				webview.goForward();
				//setGoBackForward();
				//tvTitle.setText(webview.getTitle());
			}
			
		});
		RelativeLayout.LayoutParams params_btnBrowser = (android.widget.RelativeLayout.LayoutParams) btnBrowser.getLayoutParams();
		params_btnBrowser.rightMargin=(dm.widthPixels *40/ 1000);
		params_btnBrowser.topMargin=(dm.heightPixels*38/1600);
		params_btnBrowser.width=(dm.widthPixels *85/ 1000);
		params_btnBrowser.height=(dm.heightPixels*85/1600);
		btnBrowser.setLayoutParams(params_btnBrowser);
//		expandTouchArea(titleLayout,btnBrowser,30);
		btnBrowser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webview.getUrl()));
				startActivity(intent);
			}
			
		});
		
		RelativeLayout.LayoutParams params_btnRefresh = (android.widget.RelativeLayout.LayoutParams) btnRefresh.getLayoutParams();
		int left_margin=(dm.widthPixels/2)-(dm.widthPixels*86/2000);
		params_btnRefresh.leftMargin=(left_margin);
		params_btnRefresh.bottomMargin=(dm.heightPixels*34/1600);
		params_btnRefresh.width=(dm.widthPixels*86/1000);
		params_btnRefresh.height=(dm.heightPixels*68/1600);
		btnRefresh.setLayoutParams(params_btnRefresh);
//		expandTouchArea(bottomLayout, btnRefresh, 30);
		
		btnRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				webview.reload();
			}
			
		});
       

		WebSettings webSettings = webview.getSettings();
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		
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
				
				tvTitle.setText(webview.getTitle());
				setGoBackForward();
				
			}

		});
		
	}
	public static void expandTouchArea(final View bigView, final View smallView, final int extraPadding) {
	    bigView.post(new Runnable() {
	        @Override
	        public void run() {
	            Rect rect = new Rect();
	            smallView.getHitRect(rect);
	            rect.top-=extraPadding;
	        	rect.left-=extraPadding;
	            rect.right+=extraPadding;
	            rect.bottom+=extraPadding;
	            TouchDelegate touchDelegate = new TouchDelegate(rect,smallView);
     
//	            bigView.setTouchDelegate(new TouchDelegate(rect,smallView));
	            if (View.class.isInstance(smallView.getParent())) {
                    ((View) smallView.getParent()).setTouchDelegate(touchDelegate);
                }
	        }
	   });
	}
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        MarqPlus.OnCloseWebBrowser();
    }	
	private void setGoBackForward(){
		if(webview.canGoBack())
			btnPrePage.setEnabled(true);
		else
			btnPrePage.setEnabled(false);
		
		if(webview.canGoForward())
			btnNextPage.setEnabled(true);
		else
			btnNextPage.setEnabled(false);
	}
	

}
