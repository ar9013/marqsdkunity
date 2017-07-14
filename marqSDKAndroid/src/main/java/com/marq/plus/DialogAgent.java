package com.marq.plus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

public class DialogAgent {

	private final int TYPE_RADIO = 0;
	private final int TYPE_CHECKBOX = 1;
	
	private String strId;
	private String strAction;
	private String strAuth;
	private String strSections;
	
	private String dialogTitle;
	private int optionType;
	private String optionId;
	private String[] optionItems;
	private String[] optionValues;
	
	private int optionSelectMax;
	private int optionSelectMin;
	
	private ArrayList selectedItems;
	
	public DialogAgent(){
		
	}

	public DialogAgent(String source){
		optionSelectMax = -1;
		optionSelectMin = -1;
		
		try {
			initDialogContent(source);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void showDialog(Context context){
		switch(optionType){
			case TYPE_RADIO:
				createDialogRadio(context, dialogTitle, optionItems);	
				break;
				
			case TYPE_CHECKBOX:
				createDialogCheckBox(context, dialogTitle, optionItems);
				break;
				
			default:
				createDialogMessage(context, "Error Message", "InitError.");
		}
		
	}
	
	private List<JSONObject> getJSONObject(String src, ArrayList<JSONObject> list) throws JSONException{
		Object object = new JSONTokener(src).nextValue();
		
		if(object instanceof JSONObject){
			list.add(new JSONObject(src));
		}
		
		if(object instanceof JSONArray){
			JSONArray array = new JSONArray(src);
			
			for(int i = 0; i < array.length(); i++)
				getJSONObject(array.get(i).toString(), list);
		}
		
		return list;
		
	}
	
	private void initDialogContent(String src) throws JSONException{
		
		JSONObject jsonSrc = new JSONObject(src.toString());
		
		String msg = "";

		strId = jsonSrc.getString("id");
		strAction = jsonSrc.getString("action");
		//strAuth = jsonSrc.getString("auth");
		strAuth = "Android";
		strSections = jsonSrc.getString("sections");
		
		ArrayList<JSONObject> result = new ArrayList<JSONObject>();
		
		getJSONObject(strSections, result);
		
		msg = "number of JSONObject = " + Integer.toString(result.size());
		
		for(JSONObject json : result){
			msg = msg + "\n" + json.toString();
			
			if(!json.has("type")) continue;
			
			if(json.getString("type").matches("header")){
				
				dialogTitle = json.getString("text");
			
			}
			
			if(json.getString("type").matches("radio")){
				
				optionType = TYPE_RADIO;
				optionId = json.getString("name");
				
				initOptions(new JSONObject(json.getString("options")));
			
			}
			
			if(json.getString("type").matches("checkbox")){
				
				optionType = TYPE_CHECKBOX;
				optionId = json.getString("name");
				optionSelectMax = Integer.valueOf(json.getString("maxCheck"));
				optionSelectMin = Integer.valueOf(json.getString("minCheck"));
				
				initOptions(new JSONObject(json.getString("options")));
			
			}
		}

	}
	
	private void initOptionSpace(int num){
		
		optionItems = new String[num];
		optionValues = new String[num];
		
	}
	
	private void initOptions(JSONObject data) throws JSONException{
		
		initOptionSpace(data.length());
		
		Iterator<String> iterator = data.keys();
		
		int i = 0;
		
		while(iterator.hasNext()){
			String item = iterator.next();
			String value = data.getString(item);
			
			optionItems[i] = item;
			optionValues[i] = value;
			
			i++;
		}
		
		
	}
	
	private String initActionReturn(){
		String strSelect = "";
		
		for(int i = 0; i < selectedItems.size(); i++){
			if(i > 0) strSelect += ",";
			
			strSelect += selectedItems.get(i).toString();
		}
		
		//return strAction + "?id=" + strId + "&auth=" + strAuth + "&" + optionId + "=" + strSelect;
		return strAction + "?" + optionId + "=" + strSelect;
	}
	
	public void createDialogMessage(Context context, String title, String message){
		
		AlertDialog.Builder builder =  new AlertDialog.Builder(context);
		
		builder.setTitle(title);
		builder.setIcon(0);
		builder.setMessage(message);
		
		builder.setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
			
		});
		
		builder.create().show();
	}
	
	public void createDialogList(final Context context,
			final String title,final String cancelText, final CharSequence[] items,
			final OnClickListener clickListener,final OnClickListener cancelListener) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(title);
		builder.setIcon(0);
		builder.setItems(items, clickListener);


		builder.setNegativeButton(cancelText, cancelListener);
		
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
		alertDialog.setCancelable(false);
	}
	
	
	private void createDialogList(final Context context, String title, final CharSequence[] items){
		AlertDialog.Builder builder =  new AlertDialog.Builder(context);
		
		builder.setTitle(title);
		builder.setIcon(0);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//Toast.makeText(context, items[which] + " selected", Toast.LENGTH_SHORT).show();
			}
		});
		
		AlertDialog alertDialog = builder.create();
		
		alertDialog.show();
		
	}
	
	private void createDialogRadio(final Context context, String title, final CharSequence[] items){
		selectedItems = new ArrayList();
		
		AlertDialog.Builder builder =  new AlertDialog.Builder(context);
		
		builder.setTitle(title);
		builder.setIcon(0);
		//builder.setIcon(R.drawable.ic_launcher);
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//Toast.makeText(context, items[which] + " selected", Toast.LENGTH_SHORT).show();
				
				if(!selectedItems.isEmpty())
					selectedItems.clear();
				
				selectedItems.add(optionValues[which]);
			}
		});
		
		builder.setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				if(selectedItems.isEmpty()){
					createDialogMessage(context, "Try Again", "You haven't chosen any option.");
					
					return;
				}

				createDialogResult(context, "Action Result - Radio");
			}
			
		});
		
		builder.setNegativeButton(R.string.string_cancel, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//Toast.makeText(context, "User Cancel", Toast.LENGTH_SHORT).show();
			}

			
		});
		
		builder.create().show();
		
	}
	
	
	private void createDialogCheckBox(final Context context, String title, final CharSequence[] items){
		selectedItems = new ArrayList();
		
		AlertDialog.Builder builder =  new AlertDialog.Builder(context);
		
		builder.setTitle(title);
		builder.setIcon(0);
		builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					selectedItems.add(optionValues[which]);
				}
				else if(selectedItems.contains(optionValues[which])){
					selectedItems.remove(optionValues[which]);
				}
			}
		});
		
		builder.setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(optionSelectMin > -1 && selectedItems.size() < optionSelectMin){
					createDialogMessage(context, "Try Again", "the min choise is " + Integer.toString(optionSelectMin));
					
					return;
				}
				
				if(optionSelectMax > -1 && selectedItems.size() > optionSelectMax){
					createDialogMessage(context, "Try Again", "the max choise is " + Integer.toString(optionSelectMax));
					
					return;
				}

				createDialogResult(context, "Action Result - CheckBox");
			}
			
		});
		
		builder.setNegativeButton(R.string.string_cancel, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//Toast.makeText(context, "User Cancel", Toast.LENGTH_SHORT).show();
			}

			
		});
		
		builder.create().show();
	}

	private void createDialogResult(final Context context, final String title){
		
		String targetUrl = initActionReturn();
		
		new Agent(context, new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				
				if(msg.what != Agent.HTML_CODE_READY){
					AlertDialog.Builder builder =  new AlertDialog.Builder(context);
					
					builder.setTitle("Exception!!!");
					builder.setPositiveButton(R.string.string_ok, null);
					
					builder.create().show();
					
					return;
				}
				
				DebugLog.LOGE("msg.obj = ", msg.obj.toString());
				
				try {
					JSONObject jsonObject = new JSONObject(new JSONObject(msg.obj.toString()).getString("data"));
					
					ArrayList<JSONObject> result = new ArrayList<JSONObject>();
					
					getJSONObject(jsonObject.getString("result"), result);
					
					int i = 0;
					float[] resultCount = new float[result.size()];
					String[] resultData = new String[result.size()];
					int[] resultColor = new int[result.size()];
					
					for(JSONObject json : result){
						resultCount[i] = Float.valueOf( json.getString("count") );
						resultData[i] = json.getString("datavalue");
						resultColor[i] = Color.parseColor("#" + json.getString("color"));
						
						
						i++;
					}
	
					float max_width_rate = 0.9f;

					ResultListAdapter adapter = new ResultListAdapter(context, resultColor, resultData, resultCount, max_width_rate);
					
					ListView listView = new ListView(context);
					listView.setAdapter(adapter);
					
					AlertDialog.Builder builder =  new AlertDialog.Builder(context);
					
					builder.setTitle(R.string.dialog_from_result_title);
					builder.setIcon(0);
					builder.setView(listView);
					builder.setPositiveButton("OK", null);
					
					AlertDialog dialog = builder.create();
					
					dialog.show();
					dialog.getWindow().setLayout((int) (new Agent(context).getScreenWidth() * max_width_rate), LayoutParams.WRAP_CONTENT);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}).getHtmlByHttpClient(targetUrl, strId);
		
		
	}
    
	
}
