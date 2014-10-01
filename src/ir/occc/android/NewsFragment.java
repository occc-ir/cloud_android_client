package ir.occc.android;

import ir.occc.android.adapter.RssAdapter;
import ir.occc.android.model.RssItem;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class NewsFragment extends BaseV4Fragment implements OnItemClickListener {

	private View rootView;
	private ListView listView;
	private ProgressBar progressBar;
	private TextView tv;
	
	private String rssLink = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		/** Getting the arguments to the Bundle object */
		Bundle data = getArguments();

		/** Getting data of the key current_page from the bundle */
		try {
			if (data.getString(Common.RSS_LINK) != null && data.getString(Common.RSS_LINK).length() > 0) {
				rssLink = data.getString(Common.RSS_LINK);
			}
		} catch (Exception e) {
			
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_news, container, false);
			progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
			listView = (ListView) rootView.findViewById(R.id.listView);
			listView.setOnItemClickListener(this);
			startService();
		}
		else {
			// If we are returning from a configuration change:
			// "view" is still attached to the previous view hierarchy
			// so we need to remove it and re-attach it to the current one
			ViewGroup parent = (ViewGroup) rootView.getParent();
			parent.removeView(rootView);
		}

		return rootView;
	}

	public void refresh(String rssLink) {
		this.rssLink = rssLink;		
		startService();
	}

	private void startService() {
		// clear
		listView.setAdapter(null);
		if (tv != null)
			tv.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		
		startService(RssService.class, null, rssLink);
	}

	@SuppressWarnings("unchecked")
	protected void receivedResult(int resultCode, Bundle resultData) {
		List<RssItem> items = (List<RssItem>) resultData.getSerializable(RssService.ITEMS);
		if (items != null) {
			if (items.size() == 0) {
				if (isDetached() || isRemoving() || isHidden() || !isVisible()) {
					return;
				}

				LinearLayout parent = (LinearLayout)rootView.findViewById(R.id.progressBar1).getParent();
				
				tv = (tv == null) ? new TextView(getActivity()) : tv;
				tv.setGravity(Gravity.CENTER);
				tv.setText(getString(R.string.rss_not_found));
				tv.setVisibility(View.VISIBLE);
				if (tv.getParent() == null)
					parent.addView(tv);
				
				// clear
				listView.setAdapter(null);
			} else {
				RssAdapter adapter = new RssAdapter(getActivity(), items);
				listView.setAdapter(adapter);
			}
		} else {
			Toast.makeText(getActivity(), "An error occured while downloading the rss feed.",
					Toast.LENGTH_LONG).show();
		}
		progressBar.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		RssAdapter adapter = (RssAdapter) parent.getAdapter();
		RssItem item = (RssItem) adapter.getItem(position);

		Bundle bundle = new Bundle();
		bundle.putString("title", item.getTitle());
		bundle.putString("pubDate", item.getDate());
		bundle.putString("content", item.getDescription());

		NewsContentFragment newsContentFragment = new NewsContentFragment();
		newsContentFragment.setArguments(bundle);

		FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
		transaction.replace(R.id.frame_container, newsContentFragment);
		transaction.addToBackStack(null);
		transaction.commit();

		/*Uri uri = Uri.parse(item.getLink());
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);*/
	}
}
