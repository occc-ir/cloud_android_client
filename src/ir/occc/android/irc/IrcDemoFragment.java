package ir.occc.android.irc;

import ir.occc.android.BaseV4Fragment;
import ir.occc.android.R;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class IrcDemoFragment extends BaseV4Fragment {

	private View rootView = null;
	private WebView webView;
	
	public IrcDemoFragment() {}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_irc_demo, container, false);
		webView = (WebView)rootView.findViewById(R.id.webViewContent);
		webView.getSettings().setJavaScriptEnabled(true);
		
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);

				StringBuilder sbMessage = new StringBuilder();
				sbMessage.append("<html><body dir='rtl'><div style='text-align:center'>");
				sbMessage.append(getString(R.string.page_not_found));
				sbMessage.append("<br>");
				sbMessage.append("Error " + errorCode);
				sbMessage.append("<br>");
				sbMessage.append(description);
				sbMessage.append("</div></body></html>");
				
				webView.loadDataWithBaseURL("about:blank", sbMessage.toString(), "text/html", "utf-8", null);
			}
		});
		
		refresh();
		
		return rootView;
	}

	public void refresh() {
		//webView.loadDataWithBaseURL("about:blank", html, "text/html", "utf-8", null);
		webView.loadUrl("http://webchat.freenode.net/?channels=occc");
	}
}
