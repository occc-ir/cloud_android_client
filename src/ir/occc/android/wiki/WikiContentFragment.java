package ir.occc.android.wiki;

import info.bliki.wiki.model.WikiModel;
import ir.occc.android.R;
import ir.occc.android.common.Common;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class WikiContentFragment extends Fragment {

	private String title = "";
	private String content = "";
	private boolean isRtlRequired = false;

	private View rootView = null;

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
		content = data.getString("content");
		
		isRtlRequired = Common.isRtlRequired(content);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_wiki_content, container, false);

		TextView tvTitle = (TextView)rootView.findViewById(R.id.tvTitle);
		tvTitle.setText(title);
		
		// check RTL and set it on title
		LinearLayout.LayoutParams params = (LayoutParams) tvTitle.getLayoutParams();
		if (Common.isRtlRequired(title)) {
			params.gravity = Gravity.RIGHT;
		} else {
			tvTitle.setGravity(Gravity.LEFT);
		}
		tvTitle.setLayoutParams(params);

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

		//WikiModel wikiModel = new WikiModel("http://wiki.occc.ir/wiki/${image}", "http://wiki.occc.ir/wiki/${title}");
		WikiModel wikiModel = new WikiModel("${image}", "${title}");
		content = wikiModel.render(content);
		
		StringBuilder sbMessage = new StringBuilder();
		if (isRtlRequired) {
			sbMessage.append("<html><body dir='rtl'><div >");
		}
		else {
			sbMessage.append("<html><body dir='ltr'><div >");
		}
		sbMessage.append(content);
		sbMessage.append("</div></body></html>");

		wvContent.loadDataWithBaseURL("about:blank", sbMessage.toString(), "text/html", "utf-8", null);
		//wvContent.loadData("<html><head>><meta HTTP-EQUIV='Content-Type' content='text/html; charset=utf-8' /></head><body dir='rtl'>" + description +"</body></html>", "text/html", "utf-8");
	}
}
