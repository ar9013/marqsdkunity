package com.marq.plus;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

public class MarqHowTo extends Activity {
	/** Called when the activity is first created. */
	
	private static final int HOWTO = 0;
	private static final int USECASE = 1;
	
	private Gallery g;
	
	private Button btnModeSwitch, btnStartScan;
	
	private ImageAdapter iaHowTo, iaUseCase;
	
	//private ImageView ivTitle;
	
	private int iShowMode;
	
	
	private boolean bSwitched = false;
	private boolean bRunningSwitch = false;
	private Handler handlerSwitch;
	private Thread threadSwitch;
	private int iPosition = 0;
	
	private TextView tvTitle, tvDescrip;
	
	private int[] step_howto = {
			R.string.index_howtostep01,
			R.string.index_howtostep02,
			R.string.index_howtostep03
	};
	
	private int[] step_usecase = {
			R.string.index_usecase01,
			R.string.index_usecase02,
			R.string.index_usecase03,
			R.string.index_usecase04,
			R.string.index_usecase05,
			R.string.index_usecase06,
			R.string.index_usecase07
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.marq_howto);
	    
	    DisplayMetrics dm = new DisplayMetrics();
		
		getWindowManager().getDefaultDisplay().getMetrics(dm);
	    
	    init((int) dm.widthPixels / 300);  //300 is the default width define in class, ImageAdapter
	    
	    initTemp();

	    g.setAdapter(getMode(iShowMode));
	    
	    g.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				iPosition = position;
				bSwitched = true;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
	    	
	    });
	    
	    handlerSwitch = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				
				switch(msg.what){
				case 0:
					if(bSwitched){
						bSwitched = false;
					}
					else{
						if(++iPosition >= g.getCount()){
							iPosition = 0;
							g.setSelection(iPosition, true);
						}
						else
							g.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
						
						
						setDescripText(iPosition);
					}
				}
			}
			
		};
		
		switchStart();
	}
	
	private void setDescripText(int position){
		if(iShowMode == HOWTO)
			tvDescrip.setText(step_howto[position]);
		else
			tvDescrip.setText(step_usecase[position]);
	}
	
	private void init(int rate){
		iShowMode = HOWTO;
		
		//ivTitle = (ImageView) findViewById(R.id.ivIndexTitle);
		tvTitle = (TextView) findViewById(R.id.tvIndexTitle);
		tvTitle.setText(R.string.index_title_howto);
		//tvTitle.setTextSize((float) (8 * rate));
		
		tvDescrip = (TextView) findViewById(R.id.tvDescrip);
		tvDescrip.setText(step_howto[0]);
		//tvDescrip.setTextSize((float) (8 * rate));

		g = (Gallery) findViewById(R.id.galleryHowto);
		
//		g.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                    int position, long id) {
//            	setDescripText(iPosition);
//            }
//
//
//        });
		
		
		Integer[] howtoIds = {
	    		R.drawable.img_index_howtostep01,
		        R.drawable.img_index_howtostep02,
		        R.drawable.img_index_howtostep03 
		};
		
		Integer[] usecaseIds = {
	    		R.drawable.img_index_usecase01,
	    		R.drawable.img_index_usecase02,
	    		R.drawable.img_index_usecase03,
	    		R.drawable.img_index_usecase04,
	    		R.drawable.img_index_usecase05,
	    		R.drawable.img_index_usecase06,
	    		R.drawable.img_index_usecase07
		};
		
		iaHowTo = new ImageAdapter(this, howtoIds, rate);
		iaUseCase = new ImageAdapter(this, usecaseIds, rate);
		
		btnModeSwitch = (Button) findViewById(R.id.btnModeSwitch);
		btnModeSwitch.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(iShowMode == HOWTO){
					iShowMode = USECASE;
					tvTitle.setText(R.string.index_title_usecase);
					
					btnModeSwitch.setBackgroundResource(R.drawable.btn_index_howto);
					btnModeSwitch.setTextColor(Color.parseColor("#20B2FF"));
					btnModeSwitch.setText(R.string.index_btn_howto);
					
					//ivTitle.setImageResource(R.drawable.img_index_title_usecase);
					tvDescrip.setText(step_usecase[0]);
				}
				else{
					iShowMode = HOWTO;
					tvTitle.setText(R.string.index_title_howto);
					
					btnModeSwitch.setBackgroundResource(R.drawable.btn_index_usecases);
					btnModeSwitch.setTextColor(Color.parseColor("#737373"));
					btnModeSwitch.setText(R.string.index_btn_usecase);
					
					//ivTitle.setImageResource(R.drawable.img_index_title_howto);
					tvDescrip.setText(step_howto[0]);
				}
				
				bSwitched = true;
				g.setAdapter(getMode(iShowMode));
				iPosition = 0;
			}
			
		});
		
		btnStartScan = (Button) findViewById(R.id.btnStartScan);
		btnStartScan.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//startActivity(new Intent(MarqHowTo.this, VideoPlayback.class));
			}
			
		});
		
		
	}
	
	private void switchStart(){
		DebugLog.LOGE("switchStop()", "called");
		
		bRunningSwitch = true;
		
		threadSwitch = new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(bRunningSwitch){
					try {
						
						Thread.currentThread().sleep(3000);
						
						Message msg = new Message();
						
						msg.what = 0;
					
						handlerSwitch.sendMessage(msg);
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
			
		});
		
		threadSwitch.start();
	}
	
	private void switchStop(){
		DebugLog.LOGE("switchStop()", "called");
		
		bRunningSwitch = false;
		threadSwitch.interrupted();
		threadSwitch= null;
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		switchStop();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		switchStart();
	}

	private ImageAdapter getMode(int mode){
		if(mode == HOWTO){
			return iaHowTo;
		}
		else
			return iaUseCase;
	}

	private void initTemp(){
		File sdPath = null;
		
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED)){
			//Toast.makeText(QfunCam.this, "SD Card is not exist.", Toast.LENGTH_LONG).show();
			return;
		}
		else
			sdPath = Environment.getExternalStorageDirectory();
		
		String strTempPath = sdPath.getAbsolutePath() + "/marq2/temp/";
		
		File myDataPath = new File(strTempPath);

		Process ps;
		
		if(myDataPath.exists()){
			try {
				ps = Runtime.getRuntime().exec("/system/bin/rm -rf " + strTempPath + ";mkdir -p " + strTempPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		myDataPath.mkdirs();
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if(keyCode == KeyEvent.KEYCODE_BACK){

			Builder builderDialog = new AlertDialog.Builder(MarqHowTo.this);

			builderDialog.setMessage(R.string.dialog_message_exit);
			
			builderDialog.setPositiveButton(R.string.dialog_button_yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

//					Intent intent = new Intent(MarqHowTo.this, MarqSplashScreen.class);
//					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					intent.putExtra("EXIT_MARQ", true);
//					startActivity(intent);

					startActivity(new Agent(MarqHowTo.this).exitAPP());
//					Intent intent = new Intent(Intent.ACTION_MAIN);
//					intent.addCategory(Intent.CATEGORY_HOME);
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					startActivity(intent);
					
				}
			});
			
			builderDialog.setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					// do nothing
					
				}
				
			});
			
			builderDialog.create().show();
		}
		
		return super.onKeyDown(keyCode, event);
	}



	public class ImageAdapter extends BaseAdapter {

	    //int mGalleryItemBackground;
	    private Context mContext;
	    private int _rate;
	    
	    private Integer[] myImageIds = {
	    		R.drawable.img_index_howtostep01,
		        R.drawable.img_index_howtostep01,
		        R.drawable.img_index_howtostep01 
		};

	    
	    public ImageAdapter(Context c) {
	    	mContext = c;
	    }
	    
	    public ImageAdapter(Context c, Integer[] src, int rate) {
	    	mContext = c;
	    	myImageIds = src;
	    	_rate = rate;
	    }

	    public int getCount() {
	    	return myImageIds.length;
	    }
	    

	    public Object getItem(int position) {
	    	return position;
	    }

	    public long getItemId(int position) {
	    	return position;
	    }


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView i = new ImageView(mContext);
			
			i.setImageResource(myImageIds[position]);

			i.setScaleType(ImageView.ScaleType.FIT_CENTER);
			
			i.setLayoutParams(new Gallery.LayoutParams(300 * _rate, 400 * _rate));

			return i;
		}
	} 
}
