package com.marq.plus;


import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MarqPhotoPreview extends Activity {
	
	private Button btnReturnGalleryList, btnFbShare, btnFileDelete;
	private ImageView ivPhotoPreview;
	
	private String strFileName;
	private String strFilePath;
	private String strDirPath;
	
	private int iPhotoId;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_preview);
		
		DebugLog.LOGE("MarqPhotoPreview", "Showed");
		
		Bundle bundle = this.getIntent().getExtras();
		
		strFileName = bundle.getString("FILE_PHOTONAME");
		iPhotoId = bundle.getInt("PHOTO_ID");
		
		DebugLog.LOGE("MarqPhotoPriview::PHOTO_ID", Integer.toString(iPhotoId));
		Agent agent = new Agent(this);
    	String productPrefix=agent.getStringFromMetadata(this, "com.marq.plus.ProductPathPrefix");		
    	String galleryPrefix=agent.getStringFromMetadata(this, "com.marq.plus.GalleryPathPrefix"); 
		//strDirPath = Environment.getExternalStorageDirectory() + "/marq2/gallery/";
		strDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/"+ productPrefix+ "/" + galleryPrefix +"/";
		strFilePath = strDirPath  + strFileName;
		
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+strFilePath)));		
		getViews();
		
		ivPhotoPreview.setImageBitmap( BitmapFactory.decodeFile(strFilePath) );
		
	}
	
	
	
	private void getViews(){
		
		ivPhotoPreview = (ImageView) findViewById(R.id.ivPhotoPreviewPreview);
		btnReturnGalleryList = (Button) findViewById(R.id.btnGalleryBack);
		btnFileDelete = (Button) findViewById(R.id.btnPhotoPreviewFileDelete);
		btnFbShare = (Button) findViewById(R.id.btnPhotoPreviewFbShare);

		RelativeLayout titleLayout = (RelativeLayout)findViewById(R.id.layoutPhotoPreviewTitle);
		RelativeLayout bottomLayout = (RelativeLayout)findViewById(R.id.layoutPhotoPreviewBottom);		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		titleLayout.getLayoutParams().height = dm.heightPixels /10;
		bottomLayout.getLayoutParams().height = (int)((double)dm.heightPixels /11.7647);		
		
			
		RelativeLayout.LayoutParams params_btnReturnGalleryList = (android.widget.RelativeLayout.LayoutParams) btnReturnGalleryList.getLayoutParams();
		params_btnReturnGalleryList.leftMargin=(dm.widthPixels *40/ 1000);
		params_btnReturnGalleryList.topMargin=(dm.heightPixels*48/1600);
		params_btnReturnGalleryList.width=(dm.widthPixels *85/ 1000);
		params_btnReturnGalleryList.height=(dm.heightPixels*75/1600);
		btnReturnGalleryList.setLayoutParams(params_btnReturnGalleryList);
		
		btnReturnGalleryList.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				setResult(android.app.Activity.RESULT_OK, new Intent());

				finish();
			}
			
		});
		
		RelativeLayout.LayoutParams params_btnFileDelete = (android.widget.RelativeLayout.LayoutParams) btnFileDelete.getLayoutParams();
		params_btnFileDelete.rightMargin=(dm.widthPixels *100/ 1000);
		params_btnFileDelete.bottomMargin=(dm.heightPixels*23/1600);
		params_btnFileDelete.width=(dm.widthPixels*95/1000);
		params_btnFileDelete.height=(dm.heightPixels*90/1600);
		btnFileDelete.setLayoutParams(params_btnFileDelete);
		
		btnFileDelete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder builderDialog = new AlertDialog.Builder(MarqPhotoPreview.this);

				builderDialog.setMessage(R.string.msg_photo_delete);
				
				builderDialog.setPositiveButton(R.string.string_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						File file = new File(strFilePath);
						
						file.delete();
						
						addPhotoToGallery(strFilePath);

						Bundle bundle = new Bundle();
						bundle.putInt("PHOTO_ID", iPhotoId);
						
						Intent intent = new Intent();
						intent.putExtras(bundle);
						
						setResult(android.app.Activity.RESULT_CANCELED, intent);
						
						finish();

					}
				});
				
				
				builderDialog.setNegativeButton(R.string.string_cancel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						// do nothing
						
					}
					
				});
				
				builderDialog.create().show();

			}
			
		});
		RelativeLayout.LayoutParams params_btnFbShare = (android.widget.RelativeLayout.LayoutParams) btnFbShare.getLayoutParams();
		params_btnFbShare.leftMargin=(dm.widthPixels *100/ 1000);
		params_btnFbShare.bottomMargin=(dm.heightPixels*23/1600);
		params_btnFbShare.width=(dm.widthPixels *95/ 1000);
		params_btnFbShare.height=(dm.heightPixels*90/1600);
		btnFbShare.setLayoutParams(params_btnFbShare);
		btnFbShare.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				// TODO Auto-generated method stub
				
				//Intent intent = new Intent();
//	    		intent.setClass(MarqPhotoPreview.this, MarqUploadShare.class);
	    		
	    		//DebugLog.LOGE("Upload Photo", "called");
	    		
	    		//Bundle bundle = new Bundle();
	    		//bundle.putString("FILE_PATH", strDirPath);
	    		//bundle.putString("FILE_PHOTONAME", strFileName);
	    		
	    		//intent.putExtras(bundle);
	    		//startActivity(intent);

	    		Intent share_intent = new Intent();
	    	      share_intent.setAction(Intent.ACTION_SEND);
	    	      share_intent.setType("image/png");
	    	      share_intent.putExtra(Intent.EXTRA_STREAM,
	    	         Uri.fromFile(new File(strFilePath)));
	    	      share_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	      //share_intent.putExtra(Intent.EXTRA_SUBJECT,
	    	      //   "share an image");
	    	      //share_intent.putExtra(Intent.EXTRA_TEXT,
	    	      //   "This is an image to share with you");

	    	      // start the intent
	    	      try {
	    	         startActivity(Intent.createChooser(share_intent,
	    	        		 getString(R.string.dialog_shared_type_title)));
	    	      } catch (android.content.ActivityNotFoundException ex) {
	  	    		DebugLog.LOGE("MarqPlus", "Share Failed");	    	    	  
	    	      }	
	    		
			}
			
		});
		
	}
	
	private void addPhotoToGallery(String mCurrentPhotoPath) {
	    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + mCurrentPhotoPath));

	    this.sendBroadcast(intent);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if(keyCode == KeyEvent.KEYCODE_BACK){
			DebugLog.LOGE("MarqPhotoPreview::KeyBack", "Pressed");
			
			setResult(android.app.Activity.RESULT_OK, new Intent());
			
			finish();
		}
		
		return super.onKeyDown(keyCode, event);
	}

}
