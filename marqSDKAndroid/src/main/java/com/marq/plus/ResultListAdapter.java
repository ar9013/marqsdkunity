package com.marq.plus;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultListAdapter extends BaseAdapter {
/*	private final int[] COLOR = new int[]{
			R.color.type1, R.color.type2, R.color.type3, R.color.type4, R.color.type5
	};*/
	
	private Context context;
	private int[] color;
	private String[] items;
	private float[] width;
	private float rate;
	
	public ResultListAdapter(Context _context, int[] _color, String[] _items, float[] _width, float _rate){
		context = _context;
		color = _color;
		items = _items;
		width = _width;
		rate = _rate;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		//int color = position % COLOR.length;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.dialog_result_listrow, null);
		
		TextView tvItem = (TextView) rowView.findViewById(R.id.tvResultItemName);
		ImageView ivResultLine = (ImageView) rowView.findViewById(R.id.ivResultLine);
		
		tvItem.setText(items[position]);
		tvItem.setTextColor(color[position]);

		
		ivResultLine.setBackgroundColor(color[position]);
		
		ivResultLine.getLayoutParams().width = (int) (width[position] * (new Agent(context).getScreenWidth()) * rate);
		
		
		return rowView;
	}


}
