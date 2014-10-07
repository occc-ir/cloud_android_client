package ir.occc.android.wiki;

import info.bliki.api.Page;
import info.bliki.api.User;
import ir.occc.android.R;
import ir.occc.android.common.Common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.os.StrictMode;

public class WikiService extends IntentService {

	public static final String ITEMS = "items";
	public static final String WIKI_TITLES = "titles";
	public static final int CMD_WIKI_SEARCH_PAGE = 1;
	public static final int CMD_WIKI_SEARCH_CONTENT = 2;
	public static final int CMD_WIKI_QUERY = 3;

	User user = null;

	public WikiService() {
		super("oCCc Wiki Service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(tp);
		}

		if (user == null) {
			SharedPreferences sharedPreferences = getSharedPreferences("oCCc", MODE_PRIVATE);
			
			boolean isTest;
			try {
				isTest = sharedPreferences.getBoolean("isTest", false);
			} catch (Exception e) {
				isTest = false;
			}
			
			user = new User("", "", isTest ? getString(R.string.wiki_api_url_test) : getString(R.string.wiki_api_url));
			user.login();
		}
		readByQuery(intent);
	}

	private void readByQuery(Intent intent) {
		int cmd = intent.getExtras().getInt(Common.COMMAND);
		List<Page> listOfPages = new ArrayList<Page>();
		
		switch (cmd) {
		case CMD_WIKI_SEARCH_PAGE:
			String[] listOfTitleStrings = intent.getExtras().getStringArray(WIKI_TITLES);
			//String[] listOfTitleStrings = { "Cloudsim" };
			//listOfTitleStrings.add("Main Page");
			//listOfTitleStrings.add("API");

			listOfPages = user.queryContent(listOfTitleStrings);
			break;

		default:
			break;
		}

		/*String html = "";
		for (Page page : listOfPages) {
			WikiModel wikiModel = new WikiModel("${image}", "${title}");
			html = wikiModel.render(page.getCurrentContent().toString());

			html = "<html><body><div style='text-align:justify'>" + html + "</div></body></html>";
		}*/
		
		Bundle bundle = new Bundle();
		bundle.putSerializable(ITEMS, (Serializable) listOfPages);
		ResultReceiver receiver = intent.getParcelableExtra(Common.RECEIVER);
		receiver.send(0, bundle);
	}
}
