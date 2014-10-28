package ir.occc.android.rss;

import ir.occc.android.common.Common;
import ir.occc.android.model.RssItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class RssService extends IntentService {

//	private static final String RSS_LINK =;
	public static final String ITEMS = "items";
	
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
            
            /*SharedPreferences sharedPreferences = getSharedPreferences("oCCc", MODE_PRIVATE);
			
			boolean isTest;
			try {
				isTest = sharedPreferences.getBoolean("isTest", false);
			} catch (Exception e) {
				isTest = false;
			}*/
			
			String rssLink = "";
			try {
				rssLink = intent.getStringExtra(Common.RSS_LINK);
			} catch (Exception e) {
				
			}
            
            //InputStream urlIs = getInputStream(isTest ? getString(R.string.rss_link_test) : getString(R.string.rss_link));
			InputStream urlIs = getInputStream(rssLink);
            rssItems = urlIs == null ? new ArrayList<RssItem>() : parser.parse(urlIs);
        } catch (XmlPullParserException e) {
            Log.w(e.getMessage(), e);
        } catch (IOException e) {
            Log.w(e.getMessage(), e);
        }
		Bundle bundle = new Bundle();
		bundle.putSerializable(ITEMS, (Serializable) rssItems);
		ResultReceiver receiver = intent.getParcelableExtra(Common.RECEIVER);
		receiver.send(0, bundle);
	}

	public InputStream getInputStream(String link) {
		InputStream is = null;
		try {
			URL url = new URL(link);
			HttpURLConnection urlCon = (HttpURLConnection)url.openConnection();
			int httpCode = urlCon.getResponseCode();
			urlCon.setConnectTimeout(10000);
			urlCon.setReadTimeout(10000);
			
			if (httpCode / 100 != 2)
			{
				//Log.d("oCCc", "Something bad happend");
			}
			else {
				//Log.d("oCCc", "get Input stream!!!");
				is = urlCon.getInputStream();
			}
			
			//return is;
		} catch (IOException e) {
			Log.w("oCCc", "Exception while retrieving the input stream", e);
			//return null;
		}
		return is;
	}
}
