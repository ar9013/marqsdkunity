package com.marq.plus;

import java.io.File;
import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;

public class MarqGalleryList extends Activity {
	
	private final int GALLERY_PRIVIEW = 102;

	private MarqGridViewAdapter adapter;
	
	private GridView gvGallery;
	private ArrayList<String> listFilename;
	private String strFolder;
	
	private Button btnBack, btnStartScan;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.marq_gallery);
		
		listFilename = new ArrayList();
		strFolder = "gallery";
		
		loadFilename(strFolder);
		
		if(listFilename.isEmpty()){
			showEmptyDialog();
		}
		
		initViews();
		
		adapter = new MarqGridViewAdapter(this, listFilename, strFolder);
		gvGallery.setAdapter(adapter);
			
	}

	
	private void initViews(){

		gvGallery = (GridView) findViewById(R.id.gvMarqGallery);
		RelativeLayout titleLayout = (RelativeLayout)findViewById(R.id.layoutGalleryTitle);
		
		DisplayMetrics dm = new DisplayMetrics();
		
		getWindowManager().getDefaultDisplay().getMetrics(dm);
	
		gvGallery.setNumColumns(dm.widthPixels / 240);
		titleLayout.getLayoutParams().height = dm.heightPixels /10;
		
		
		gvGallery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DebugLog.LOGE("position = ", Integer.toString(position));
                DebugLog.LOGE("file name = ", listFilename.get(position).toString());
                
                Intent intent = new Intent();
	    		intent.setClass(MarqGalleryList.this, MarqPhotoPreview.class);

	    		Bundle bundle = new Bundle();
	    		bundle.putString("FILE_PHOTONAME", listFilename.get(position).toString());
	    		bundle.putInt("PHOTO_ID", position);
	    		
	    		intent.putExtras(bundle);
	    		
	    		startActivityForResult(intent, GALLERY_PRIVIEW);
            }


        });
		
        
		btnBack = (Button) findViewById(R.id.btnGalleryBack);
		RelativeLayout.LayoutParams params_btnBack = (android.widget.RelativeLayout.LayoutParams) btnBack.getLayoutParams();
		params_btnBack.width=(dm.widthPixels *85/ 1000);
		params_btnBack.height=(dm.heightPixels*75/1600);
		params_btnBack.leftMargin=(dm.widthPixels *40/ 1000);
		params_btnBack.topMargin=(dm.heightPixels*48/1600);
		btnBack.setLayoutParams(params_btnBack);
		
		RelativeLayout.LayoutParams textParams = 
	                new RelativeLayout.LayoutParams(
	                    RelativeLayout.LayoutParams.WRAP_CONTENT,   
	                    RelativeLayout.LayoutParams.WRAP_CONTENT);		 
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		
		btnStartScan = (Button) findViewById(R.id.btnGalleryStartScan);
		RelativeLayout.LayoutParams params_btnStartScan = (android.widget.RelativeLayout.LayoutParams) btnStartScan.getLayoutParams();
		params_btnStartScan.rightMargin=(dm.widthPixels*40/1000);
		params_btnStartScan.topMargin=(dm.heightPixels*37/1600);
		params_btnStartScan.width=(dm.widthPixels *70/ 1000);
		params_btnStartScan.height=(dm.heightPixels*88/1600);
		btnStartScan.setLayoutParams(params_btnStartScan);
		
		btnStartScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putBoolean("SCAN", true);
				
				Intent intent = new Intent();
				intent.putExtras(bundle);
				
				setResult(android.app.Activity.RESULT_OK, intent);
				
				finish();
			}
			
		});
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == GALLERY_PRIVIEW && resultCode == android.app.Activity.RESULT_CANCELED){
			Bundle bData = data.getExtras();
			
			if(bData != null){
				listFilename.remove( bData.getInt("PHOTO_ID") );
				DebugLog.LOGE("Remove MarqGalleryList::PHOTO_ID", Integer.toString(bData.getInt("PHOTO_ID")));
				
				if(listFilename.isEmpty()){
					showEmptyDialog();
				}
			}
			
		}
		
		adapter.notifyDataSetChanged();
		
	}

	
	private void showEmptyDialog(){
		new AlertDialog.Builder(MarqGalleryList.this)
		.setTitle("Message")
        .setMessage("There is no Photo in the marq+.")
        .setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
        	
        }).show();
	}

	private void loadFilename(String folder){
		//super.onResume();

        //File sdCardDir = Environment.getExternalStorageDirectory();

        //File dirPath = new File(sdCardDir, "/marq2/" + folder);
		//strDirPath = Environment.getExternalStorageDirectory() + "/marq2/gallery/";
		
		Agent agent = new Agent(this);
    	String productPrefix=agent.getStringFromMetadata(this, "com.marq.plus.ProductPathPrefix");		

		String strDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/"+ productPrefix+ "/";
		
		File dirPath = new File(strDirPath, folder);
		if(!dirPath.exists()) {
			
			DebugLog.LOGE("MarqGalleryList::loadFilename()", "create directory");
			dirPath.mkdirs();	
		}
		
        File[] list = dirPath.listFiles();
        
        StringBuilder sb = new StringBuilder();
        
        this.listFiles(list, sb, "");
        
        //for(File f: list) {
        //	sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+f.getAbsolutePath())));
        //}
	}
	
	private void listFiles(File[] list, StringBuilder sb, String space) {
		
        if (list == null) return;
        
        for (File f : list) {
            
            if (!f.isDirectory()) 
            	listFilename.add(f.getName());
            
        }
    }
	
	
}
