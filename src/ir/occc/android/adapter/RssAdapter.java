package ir.occc.android.adapter;

import ir.occc.android.R;
import ir.occc.android.model.RssItem;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RssAdapter extends BaseAdapter {

	private final List<RssItem> items;
	private final Context context;

	public RssAdapter(Context context, List<RssItem> items) {
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
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.list_item_news_rss, null);
			holder = new ViewHolder();
			holder.itemTitle = (TextView) convertView.findViewById(R.id.postTitleLabel);
			holder.itemPubDate = (TextView) convertView.findViewById(R.id.postPubDateLabel);
			holder.itemThumb = (ImageView) convertView.findViewById(R.id.postThumb);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.itemTitle.setText(items.get(position).getTitle());
		holder.itemPubDate.setText(items.get(position).getDate());
		if (items.get(position).getThumbUrl() == null) {
			holder.itemThumb.setImageResource(R.drawable.ic_launcher);
		}

		/*View rowView;
		rowView = convertView;
		ImageView thumbImageView = (ImageView) rowView.findViewById(R.id.postThumb);
		if (items.get(position).getThumbUrl() == null) {
			thumbImageView.setImageResource(R.drawable.ic_launcher);
		}
		
		TextView postTitleView = (TextView) rowView.findViewById(R.id.postTitleLabel);
		postTitleView.setText(items.get(position).getTitle());
		TextView postDateView = (TextView) rowView.findViewById(R.id.postDateLabel);
		postDateView.setText(items.get(position).getDate());*/

		return convertView;
	}

	static class ViewHolder {
		TextView itemTitle;
		TextView itemPubDate;
		ImageView itemThumb;
	}

}
