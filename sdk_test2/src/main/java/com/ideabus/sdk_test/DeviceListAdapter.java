package com.ideabus.sdk_test;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceListAdapter extends BaseAdapter {
	
	private List<Map<String,String>> mBleData;
	private LayoutInflater mInflator;
	private Activity mContext;

	public DeviceListAdapter(Activity c) {
		super();
		mContext = c;
		mBleData = new ArrayList<>();
		mInflator = mContext.getLayoutInflater();
	}

	public void addDevice(String mac, String name, String rssi) {
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("mac", mac);
		map.put("name", name);
		map.put("rssi", rssi);
		mBleData.add(map);
		notifyDataSetChanged();
	}

	public String getDeviceMac(int position) {
		return mBleData.get(position).get("mac");
	}

	public void clear() {
		mBleData.clear();
		notifyDataSetChanged();
	}

	public void remove(int location){
		if(location < mBleData.size()){
			mBleData.remove(location);
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return mBleData.size();
	}

	@Override
	public Object getItem(int i) {
		return mBleData.get(i);
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
			view = mInflator.inflate(R.layout.item_device, null);
			viewHolder = new ViewHolder();
			viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
			viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
			viewHolder.device_rssi = (TextView) view.findViewById(R.id.device_rssi);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		Map<String,String> message = mBleData.get(i);
		
		if (message != null && message.size() > 0){
			viewHolder.deviceName.setText(message.get("name"));
			viewHolder.deviceAddress.setText(message.get("mac"));
			viewHolder.device_rssi.setText(message.get("rssi"));
		}
		return view;
	}

	class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
		TextView device_rssi;
	}
}
