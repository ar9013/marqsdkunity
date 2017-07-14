package com.marq.plus;


import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class MarqGridViewAdapter extends BaseAdapter 
{

	private Context _con;
	private ArrayList<String> _items;
	private String _path;
	private Bitmap _bm;
	BitmapFactory.Options options;
	
	@SuppressLint("NewApi")
	public MarqGridViewAdapter(Context con, ArrayList<String> items)
	{
		DebugLog.LOGE("MarqGridViewAdapter", "created");
		
		_con = con;
		_items = items;
		
		Agent agent = new Agent(con);
    	String prefix=agent.getStringFromMetadata(con, "com.marq.plus.ProductPathPrefix"); 		
		
    	_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() +  "/" + prefix + "/";
		
		options = new BitmapFactory.Options();
		options.inMutable = true;
		options.inSampleSize = 4;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		
		//options.inJustDecodeBounds = true;
		
		for(int i = 0; i < items.size(); i++)
			DebugLog.LOGE("item " + Integer.toString(i), items.get(i).toString());
	}

	@SuppressLint("NewApi")
	public MarqGridViewAdapter(Context con, ArrayList<String> items, String srcFolder)
	{
		DebugLog.LOGE("MarqGridViewAdapter", "created");
		
		_con = con;
		_items = items;
		
		Agent agent = new Agent(con);
    	String prefix=agent.getStringFromMetadata(con, "com.marq.plus.ProductPathPrefix"); 
    	
		_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/" + prefix + "/" + srcFolder + "/";
		
		options = new BitmapFactory.Options();
		options.inMutable = true;
		options.inSampleSize = 4;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
	}

	public int getCount(){
		return _items.size();
	}
	
	public Object getItem(int arg0){
		return _items.get(arg0);
    }
	
	public long getItemId(int position){
		return position;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		DebugLog.LOGE("getView()", "called");

		ImageView iv = new ImageView(_con);
        iv.setLayoutParams(new GridView.LayoutParams(240, 240));
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

		_bm = BitmapFactory.decodeFile(_path + _items.get(position), options);
		
		iv.setImageBitmap(_bm);
		
		DebugLog.LOGE("filePath " + Integer.toString(position), _path + _items.get(position));
		
		return iv;
	} 
}