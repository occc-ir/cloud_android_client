package ir.occc.android.adapter;

import ir.occc.android.R;
import ir.occc.android.common.Common;

import java.util.ArrayList;
import java.util.regex.Matcher;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
		
		TextView txtTitle = (TextView) convertView.findViewById(R.id.title);

		String completeLink = rightNavDrawerItems.get(position);
		int idx = completeLink.indexOf(',');
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)txtTitle.getLayoutParams();
		
		Matcher match = Common.RtlPersianPattern.matcher(completeLink.substring(0, 1));
		if (match.find()) {
			//params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		} else {
			//params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		}
		txtTitle.setLayoutParams(params);
		
		txtTitle.setText(completeLink.substring(0, idx));
		txtTitle.setTag(completeLink.substring(idx + 1));

		return convertView;
	}

}
