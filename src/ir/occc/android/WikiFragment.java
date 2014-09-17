package ir.occc.android;

import info.bliki.api.Page;
import ir.occc.android.adapter.WikiPageAdapter;
import ir.occc.android.model.WikiPageItem;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

/**
 * @author Ahmad
 *
 */
public class WikiFragment extends BaseV4Fragment implements OnItemClickListener {

	private View rootView = null;
	private LinearLayout mainLayout;
	private WebView webView;
	private ProgressBar progressBar;
	private ListView lvPages;

	public WikiFragment(){}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/** Getting the arguments to the Bundle object *//*
		Bundle data = getArguments();

		*//** Getting data of the key current_page from the bundle *//*
		try {
			if (data.getString("content") != null && data.getString("content").length() > 0) {
				viewWikiContent(data.getString("content"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_wiki, container, false);
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
		webView = (WebView)rootView.findViewById(R.id.webViewWiki);
		mainLayout = ((LinearLayout)webView.getParent());

		startService();

		return rootView;
	}

	public void refresh() {
		startService();
	}

	private void startService() {
		mainLayout.setGravity(Gravity.CENTER);
		progressBar.setVisibility(View.VISIBLE);
		webView.setVisibility(View.GONE);
		if (lvPages != null) {
			lvPages.setVisibility(View.GONE);
		}
		startService(WikiService.class, WikiService.CMD_WIKI_SEARCH, new String[] {"cloudsim", "کلادسیم"});
	}

	protected void receivedResult(int resultCode, Bundle resultData) {
		@SuppressWarnings("unchecked")
		List<Page> listOfPages = (List<Page>) resultData.getSerializable(RssService.ITEMS);

		String html = "";

		if (listOfPages.size() == 0) {
			html = "<html><body dir='rtl'><div style='text-align:center'>"+ getString(R.string.page_not_found) +"</div></body></html>";
			webView.loadDataWithBaseURL("about:blank", html, "text/html", "utf-8", null);
			webView.setVisibility(View.VISIBLE);

			// Handle pages list view on searching again
			if (lvPages != null) {
				lvPages.setVisibility(View.GONE);
			}
		/*} else if (listOfPages.size() == 1) {
			for (Page page : listOfPages) {
				viewWikiContent(page.getCurrentContent());
			}	*/
		} else {
			List<WikiPageItem> items = new ArrayList<WikiPageItem>();
			for (Page page : listOfPages) {
				String pageTitle = page.getTitle();
				String pageContent = page.getCurrentContent();
				int pageId = Integer.parseInt(page.getPageid());
				//int len = pageContent.length() < 70 ? pageContent.length() : 70;
				
				WikiPageItem item = new WikiPageItem(pageTitle, pageContent, pageId);
				items.add(item);
			}

			WikiPageAdapter adapter = new WikiPageAdapter(getActivity(), items);
			
			lvPages = null;
			if (lvPages == null) {
				
				lvPages = new ListView(getActivity());
				mainLayout.addView(lvPages);
				mainLayout.setGravity(Gravity.TOP);
			}
			
			lvPages.setAdapter(adapter);
			lvPages.setOnItemClickListener(this);

			webView.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
			
			if (listOfPages.size() == 1) {
				viewWikiContent(0, adapter);
			}
		}
	}

	
	/**
	 * Show the content of wiki page with title
	 * 
	 * @param content the content which want to show
	 */
	/*private void viewWikiContent(String content) {
		String html;
		WikiModel wikiModel = new WikiModel("${image}", "${title}");
		html = wikiModel.render(content);

		//html = "<html><body><div style='text-align:justify'>" + html + "</div></body></html>";
		webView.loadDataWithBaseURL("about:blank", html, "text/html", "utf-8", null);
		webView.setVisibility(View.VISIBLE);

		// Handle pages list view on searching again
		if (lvPages != null) {
			lvPages.setVisibility(View.GONE);
		}
	}*/

	/**
	 * Show the content of wiki page with title
	 * @param position Position of item on adapter
	 * @param adapter An adapter of wiki page
	 */
	private void viewWikiContent(int position, WikiPageAdapter adapter) {
		WikiPageItem item = (WikiPageItem) adapter.getItem(position);
		
		Bundle bundle = new Bundle();
		bundle.putString("title", item.getTitle());
		bundle.putString("pubDate", "");
		bundle.putString("content", item.getContent());

		WikiContentFragment wikiContentFragment = new WikiContentFragment();
		wikiContentFragment.setArguments(bundle);

		FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
		transaction.replace(R.id.frame_container, wikiContentFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		WikiPageAdapter adapter = (WikiPageAdapter) parent.getAdapter();
		viewWikiContent(position, adapter);
	}
}
