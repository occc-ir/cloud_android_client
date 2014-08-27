package ir.occc.android;

import ir.occc.android.model.RssItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class RssService extends IntentService {

	private static final String RSS_LINK = "http://www.zaeimportal.com/_layouts/listfeed.aspx?List=bc9ce8be-8db6-4e10-bc05-fc0f08ff959e";
	public static final String ITEMS = "items";
	public static final String RECEIVER = "receiver";

	public RssService() {
		super("RssService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("oCCc", "Service started");
		List<RssItem> rssItems = null;
		try {
            CustomRssParser parser = new CustomRssParser();
            rssItems = parser.parse(getInputStream(RSS_LINK));
        } catch (XmlPullParserException e) {
            Log.w(e.getMessage(), e);
        } catch (IOException e) {
            Log.w(e.getMessage(), e);
        }
		Bundle bundle = new Bundle();
		bundle.putSerializable(ITEMS, (Serializable) rssItems);
		ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
		receiver.send(0, bundle);
	}

	public InputStream getInputStream(String link) {
		InputStream is = null;
		try {
			URL url = new URL(link);
			URLConnection urlCon = url.openConnection();
			is = urlCon.getInputStream();
			//return is;
		} catch (IOException e) {
			Log.w("oCCc", "Exception while retrieving the input stream", e);
			//return null;
		}
		return is;
	}
}
