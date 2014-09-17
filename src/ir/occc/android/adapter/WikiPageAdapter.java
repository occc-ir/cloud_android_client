package ir.occc.android.adapter;

import ir.occc.android.R;
import ir.occc.android.model.WikiPageItem;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WikiPageAdapter extends BaseAdapter {

	private final List<WikiPageItem> items;
	private final Context context;

	public WikiPageAdapter(Context context, List<WikiPageItem> items) {
		this.items = items;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return (long)position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PageViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.list_item_wiki_page, null);
			holder = new PageViewHolder();
			holder.itemTitle = (TextView) convertView.findViewById(R.id.postTitleLabel);
			holder.itemContent = (TextView) convertView.findViewById(R.id.postContentLabel);
			convertView.setTag(holder);
		} else {
			holder = (PageViewHolder) convertView.getTag();
		}
		holder.itemTitle.setText(items.get(position).getTitle());
		holder.itemContent.setText(items.get(position).getContent());

		Animation anim = AnimationUtils.loadAnimation(parent.getContext(), R.anim.push_up_in);
		convertView.startAnimation(anim);
		anim = null;
		
		return convertView;
	}

	static class PageViewHolder {
		TextView itemTitle;
		TextView itemContent;
	}
}
