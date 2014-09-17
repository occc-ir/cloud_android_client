package ir.occc.android.adapter;

import ir.occc.android.R;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class RightNavDrawerListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<String> rightNavDrawerItems;

	public RightNavDrawerListAdapter(Context context, ArrayList<String> rightNavDrawerItems){
		this.context = context;
		this.rightNavDrawerItems = rightNavDrawerItems;
	}

	public int getCount() {
		return rightNavDrawerItems.size();
	}

	public Object getItem(int position) {       
		return rightNavDrawerItems.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater)
					context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.list_item_navigator_search, null);
		}

		return convertView;
	}

}
