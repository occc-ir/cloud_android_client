package ir.occc.android;

import info.bliki.api.Page;
import info.bliki.wiki.model.WikiModel;
import ir.occc.android.adapter.WikiPageAdapter;
import ir.occc.android.model.WikiPageItem;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class WikiFragment extends BaseV4Fragment {

	View rootView = null;


	public WikiFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_wiki, container, false);

		startService();

		return rootView;
	}

	public void refresh() {
		startService();
	}

	private void startService() {
		startService(WikiService.class, WikiService.CMD_WIKI_SEARCH, new String[] {"کلادسیم"});
	}

	protected void receivedResult(int resultCode, Bundle resultData) {
		@SuppressWarnings("unchecked")
		List<Page> listOfPages = (List<Page>) resultData.getSerializable(RssService.ITEMS);

		String html = "";
		
		if (listOfPages.size() == 0) {
			html = "<html><body dir='rtl'><div style='text-align:center'>این صفحه پیدا نشده است</div></body></html>";
		}
		if (listOfPages.size() == 1) {
			for (Page page : listOfPages) {
				WikiModel wikiModel = new WikiModel("${image}", "${title}");
				html = wikiModel.render(page.getCurrentContent().toString());

				//html = "<html><body><div style='text-align:justify'>" + html + "</div></body></html>";
			}	
		} else {
			List<WikiPageItem> items = new ArrayList<WikiPageItem>();
			for (Page page : listOfPages) {
				WikiPageItem item = new WikiPageItem(page.getTitle(), page.getCurrentContent().substring(0, 70));
				items.add(item);
			}	

			WikiPageAdapter adapter = new WikiPageAdapter(getActivity(), items);
			ListView lvPages = new ListView(getActivity());
			lvPages.setAdapter(adapter);
			
			WebView wv = (WebView)rootView.findViewById(R.id.webViewWiki);
			RelativeLayout parent = ((RelativeLayout)wv.getParent());
			parent.removeView(wv);
			parent.addView(lvPages);
			
			return;
		}

		WebView wv = (WebView)rootView.findViewById(R.id.webViewWiki);
		wv.loadDataWithBaseURL("about:blank", html, "text/html", "utf-8", null);
	};
}
