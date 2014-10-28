package ir.occc.android.wiki;

import info.bliki.api.Page;
import ir.occc.android.BaseV4Fragment;
import ir.occc.android.R;
import ir.occc.android.adapter.WikiPageAdapter;
import ir.occc.android.common.Common;
import ir.occc.android.common.QueryType;
import ir.occc.android.model.WikiPageItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
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
	private QueryType queryType;
	private String queryText;
	public String wikiTitle;

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

		// Show Main Page of wiki
		wikiTitle = Arrays.asList(getResources().getStringArray(R.array.wiki_link_items)).get(0);
		int idx = wikiTitle.indexOf(',');
		wikiTitle = wikiTitle.substring(idx + 1);
		this.queryType = QueryType.WikiTitle;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_wiki, container, false);
			progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
			webView = (WebView)rootView.findViewById(R.id.webViewWiki);
			mainLayout = ((LinearLayout)webView.getParent());

			search(queryType, wikiTitle);
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

	public void refresh(QueryType type, String query) {
		stopService(WikiService.class);
		search(type, query);
	}

	@Override
	protected void search(QueryType type, String query) {
		this.queryType = type;
		this.queryText = query;

		switch (type) {
		case WikiTitle:
			startService(queryText, WikiService.CMD_WIKI_SEARCH_PAGE);
			break;

		case WikiContent:
			startService(queryText, WikiService.CMD_WIKI_SEARCH_PAGE);
			break;

		case WikiSpecialPage:
			startService(queryText, WikiService.CMD_WIKI_EXACT_PAGE);
			break;
			
		default:
			break;
		}
	}

	private void startService(String title, int cmd) {
		mainLayout.setGravity(Gravity.CENTER);
		progressBar.setVisibility(View.VISIBLE);
		webView.setVisibility(View.GONE);
		if (lvPages != null) {
			lvPages.setVisibility(View.GONE);
		}

		Intent intent = new Intent(getActivity(), WikiService.class);
		//intent.putExtra(Common.COMMAND, WikiService.CMD_WIKI_SEARCH_PAGE);
		intent.putExtra(Common.COMMAND, cmd);
		intent.putExtra(WikiService.WIKI_TITLE, title);

		startService(WikiService.class, intent);
	}

	protected void receivedResult(int resultCode, Bundle resultData) {
		@SuppressWarnings("unchecked")
		List<Page> listOfPages = (List<Page>) resultData.getSerializable(WikiService.ITEMS);

		if (listOfPages == null || listOfPages.size() == 0) {

			if (isDetached() || isRemoving() || isHidden() || !isVisible()) {
				return;
			}

			viewNoContent(getString(R.string.page_not_found));
		} 
		/*else if (listOfPages.size() == 1) {
			for (Page page : listOfPages) {
				viewWikiContent(page.getCurrentContent());
			}	
		}*/ 
		else {
			List<WikiPageItem> items = new ArrayList<WikiPageItem>();
			for (Page page : listOfPages) {
				try {
					String pageTitle = page.getTitle();
					String pageContent = page.getCurrentContent();
					int pageId = Integer.parseInt(page.getPageid());
					//int len = pageContent.length() < 70 ? pageContent.length() : 70;

					WikiPageItem item = new WikiPageItem(pageTitle, pageContent, pageId);
					items.add(item);
				} catch (Exception e) {
					viewNoContent(getString(R.string.query_has_problem));
				}
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
				if (adapter.getCount() == 0) {
					viewNoContent(getString(R.string.query_has_problem));
				} else { 
					viewWikiContent(0, adapter);
				}
			}
		}
	}

	public void viewSpecialPage() {
		Intent intent = new Intent(getActivity(), WikiService.class);
		intent.putExtra(Common.COMMAND, WikiService.CMD_WIKI_EXACT_PAGE);			
		intent.putExtra(WikiService.WIKI_TITLE, " ");

		startService(WikiService.class, intent);
	}

	private void viewNoContent(String text) {

		StringBuilder sbMessage = new StringBuilder();
		if (Common.isRtlRequired(text)) {
			sbMessage.append("<html><body dir='rtl'><div style='text-align:center'>");
		}
		else {
			sbMessage.append("<html><body dir='ltr'><div style='text-align:center'>");
		}
		sbMessage.append(text);
		sbMessage.append("</div></body></html>");

		webView.loadDataWithBaseURL("about:blank", sbMessage.toString(), "text/html", "utf-8", null);
		webView.setVisibility(View.VISIBLE);

		// Handle pages list view on searching again
		if (lvPages != null) {
			lvPages.setVisibility(View.GONE);
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
