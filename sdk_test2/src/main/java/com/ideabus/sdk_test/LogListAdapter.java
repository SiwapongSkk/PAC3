package com.ideabus.sdk_test;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LogListAdapter extends BaseAdapter {

	private List<String> mLogData;
	private LayoutInflater mInflator;
	private Activity mContext;

	public LogListAdapter(Activity c) {
		super();
		mContext = c;
		mLogData = new ArrayList<>();
		mInflator = mContext.getLayoutInflater();
	}

	public void addLog(String message) {
		Log.e("addLog", message);
        mLogData.add(message);
        notifyDataSetChanged();
	}

	public String getDeviceMac(int position) {
		return mLogData.get(position);
	}

	public void clear() {
		mLogData.clear();
		notifyDataSetChanged();
	}

	public void remove(int location){
		if(location < mLogData.size()){
			mLogData.remove(location);
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return mLogData.size();
	}

	@Override
	public Object getItem(int i) {
		return mLogData.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder = null;
		// General ListView optimization code.
		if (view == null) {
			view = mInflator.inflate(R.layout.log_item, null);
			viewHolder = new ViewHolder();
			viewHolder.message = (TextView) view.findViewById(R.id.message);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		String message = mLogData.get(i);
		
		if (message != null ){
			if(message.startsWith("WRITE")){
				viewHolder.message.setTextColor(Color.RED);
			}else if(message.startsWith("NOTIFY")){
				viewHolder.message.setTextColor(Color.BLUE);
			}else{
				viewHolder.message.setTextColor(Color.GREEN);
			}
			viewHolder.message.setText(message);
		}

		return view;
	}

	class ViewHolder {
//		TextView deviceName;
		TextView message;
//		TextView device_rssi;
	}
}
