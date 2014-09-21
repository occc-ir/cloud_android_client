package ir.occc.android;

import ir.occc.android.model.RssItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class RssService extends IntentService {

//	private static final String RSS_LINK =;
	public static final String ITEMS = "items";
	public static final String RECEIVER = "receiver";
	
	public RssService() {
		super("oCCc Rss Service");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		getRss(intent);
	}

	private void getRss(Intent intent) {
		List<RssItem> rssItems = null;
		try {
            CustomRssParser parser = new CustomRssParser();
            
            SharedPreferences sharedPreferences = getSharedPreferences("oCCc", MODE_PRIVATE);
			
			boolean isTest;
			try {
				Log.d("oCCc", "getting isTest");
				isTest = sharedPreferences.getBoolean("isTest", false);
				Log.d("oCCc", "get isTest: " + String.valueOf(isTest));
			} catch (Exception e) {
				isTest = false;
			}
            
            InputStream urlIs = getInputStream(isTest ? getString(R.string.rss_link_test) : getString(R.string.rss_link));
            rssItems = urlIs == null ? new ArrayList<RssItem>() : parser.parse(urlIs);
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
			urlCon.setReadTimeout(20000);
			is = urlCon.getInputStream();
			//return is;
		} catch (IOException e) {
			Log.w("oCCc", "Exception while retrieving the input stream", e);
			//return null;
		}
		return is;
	}
}
