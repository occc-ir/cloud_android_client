package ir.occc.android.rss;

import ir.occc.android.R;
import ir.occc.android.common.Common;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class NewsContentFragment extends Fragment {

	private String title = "";
	private String pubDate = "";
	private String content = "";
	private boolean isRtlRequired = false;

	private View rootView = null;
	private TextView tvPubDate = null;

	@Override
	public void onDestroy() {
		super.onDestroy();
		WebView wvContent = (WebView)rootView.findViewById(R.id.webViewContent);
		wvContent.destroy();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/** Getting the arguments to the Bundle object */
		Bundle data = getArguments();

		/** Getting data of the key current_page from the bundle */
		title = data.getString("title");
		pubDate = data.getString("pubDate");
		content = data.getString("content");
		
		String rawContent = Html.fromHtml(content).toString();		
		while (rawContent.startsWith(" ") || rawContent.substring(0, 1).toCharArray()[0] == 160)
			rawContent = rawContent.substring(1);
		isRtlRequired = Common.isRtlRequired(rawContent);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_news_content, container, false);

		TextView tvTitle = (TextView)rootView.findViewById(R.id.tvTitle);
		tvTitle.setText(title);

		tvPubDate = (TextView)rootView.findViewById(R.id.tvNewsPubDate);
		tvPubDate.setText(pubDate);

		refresh();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

		WebView wvContent = (WebView)rootView.findViewById(R.id.webViewContent);
		wvContent.isShown();

	}

	public void refresh() {
		WebView wvContent = (WebView)rootView.findViewById(R.id.webViewContent);
		//wvContent.getSettings().setJavaScriptEnabled(true);
		wvContent.getSettings().setSupportZoom(false);
		wvContent.getSettings().setUseWideViewPort(false);
		wvContent.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		wvContent.getSettings().setDefaultTextEncodingName("utf-8");
		//wvContent.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

		StringBuilder sbMessage = new StringBuilder();
		if (isRtlRequired) {
			sbMessage.append("<html><body dir='rtl'><div style='text-align:justify'>");
		}
		else {
			sbMessage.append("<html><body dir='ltr'><div style='text-align:justify'>");
		}
		sbMessage.append(content);
		sbMessage.append("</div></body></html>");

		wvContent.loadDataWithBaseURL("about:blank", sbMessage.toString(), "text/html", "utf-8", null);
		//wvContent.loadData("<html><head>><meta HTTP-EQUIV='Content-Type' content='text/html; charset=utf-8' /></head><body dir='rtl'>" + description +"</body></html>", "text/html", "utf-8");
	}
}
