package ir.occc.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class NewsContentFragment extends Fragment {

	String title = "";
	String pubDate = "";
	String description = "";
	 
	View v = null;
	
	public void refresh() {
		WebView wvContent = (WebView)v.findViewById(R.id.webViewContent);
		//wvContent.getSettings().setJavaScriptEnabled(true);
		wvContent.getSettings().setSupportZoom(false);
		wvContent.getSettings().setUseWideViewPort(false);
		wvContent.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		wvContent.getSettings().setDefaultTextEncodingName("utf-8");
		//Log.d("oCCc", description);
		//wvContent.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
		description = "<html><body dir='rtl'><div style='text-align:justify'>" + description +"</div></body></html>";
		wvContent.loadDataWithBaseURL("about:blank", description, "text/html", "utf-8", null);
		
//		wvContent.loadData("<html><head>><meta HTTP-EQUIV='Content-Type' content='text/html; charset=utf-8' /></head><body dir='rtl'>" + description +"</body></html>", "text/html", "utf-8");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		WebView wvContent = (WebView)v.findViewById(R.id.webViewContent);
		wvContent.destroy();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/** Getting the arguments to the Bundle object */
		Bundle data = getArguments();

		/** Getting integer data of the key current_page from the bundle */
		title = data.getString("title");
		pubDate = data.getString("pubDate");
		description = data.getString("description");
	}
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.fragment_news_content, container, false);

		TextView tvTitle = (TextView ) v.findViewById(R.id.tvNewsTitle);
		tvTitle.setText(title);

		TextView tvPubDate = (TextView ) v.findViewById(R.id.tvNewsPubDate);
		tvPubDate.setText(pubDate);

		refresh();
		
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		WebView wvContent = (WebView)v.findViewById(R.id.webViewContent);
		wvContent.isShown();
		
	}
}
