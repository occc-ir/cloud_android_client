package ir.occc.android;

import ir.occc.android.adapter.RssAdapter;
import ir.occc.android.model.RssItem;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class NewsFragment extends BaseV4Fragment implements OnItemClickListener {

	private ProgressBar progressBar;
	private ListView listView;
	private View view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_news, container, false);
			progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
			listView = (ListView) view.findViewById(R.id.listView);
			listView.setOnItemClickListener(this);
			startService();
		}
		else {
			// If we are returning from a configuration change:
			// "view" is still attached to the previous view hierarchy
			// so we need to remove it and re-attach it to the current one
			ViewGroup parent = (ViewGroup) view.getParent();
			parent.removeView(view);
		}

		return view;
	}

	public void refresh() {
		startService();
	}

	private void startService() {
		startService(RssService.class, null, null);
	}

	@SuppressWarnings("unchecked")
	protected void receivedResult(int resultCode, Bundle resultData) {
		List<RssItem> items = (List<RssItem>) resultData.getSerializable(RssService.ITEMS);
		if (items != null) {
			RssAdapter adapter = new RssAdapter(getActivity(), items);
			listView.setAdapter(adapter);
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
		bundle.putString("description", item.getDescription());

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
