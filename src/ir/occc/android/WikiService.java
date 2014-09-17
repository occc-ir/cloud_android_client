package ir.occc.android;

import info.bliki.api.Page;
import info.bliki.api.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.os.StrictMode;

public class WikiService extends IntentService {

	public static final String ITEMS = "items";
	public static final String RECEIVER = "receiver";
	public static final String COMMAND = "command";
	public static final String WIKI_TITLES = "titles";
	public static final int CMD_WIKI_SEARCH = 1;	

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
			user = new User("", "", getString(R.string.wiki_api_url_test));
			user.login();
		}
		readByQuery(intent);
	}

	private void readByQuery(Intent intent) {
		int cmd = intent.getExtras().getInt(COMMAND);
		List<Page> listOfPages = new ArrayList<Page>();
		
		switch (cmd) {
		case CMD_WIKI_SEARCH:
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
		ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
		receiver.send(0, bundle);
	}
}
