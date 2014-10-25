package ir.occc.android.wiki;

import info.bliki.api.Connector;
import info.bliki.api.Page;
import info.bliki.api.SearchResult;
import info.bliki.api.User;
import info.bliki.api.XMLSearchParser;
import ir.occc.android.R;
import ir.occc.android.common.Common;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.os.StrictMode;

public class WikiService extends IntentService {

	public static final String ITEMS = "items";
	public static final String WIKI_TITLE = "title";
	//public static final String WIKI_TITLES = "titles";
	public static final int CMD_WIKI_SEARCH_PAGE = 01;
	public static final int CMD_WIKI_SEARCH_CONTENT = 02;
	public static final int CMD_WIKI_QUERY = 03;
	public static final int CMD_WIKI_EXACT_PAGE = 04;

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
			//String[] listOfTitleStrings = intent.getExtras().getStringArray(WIKI_TITLES);
			//String[] listOfTitleStrings = { "Cloudsim" };
			//listOfTitleStrings.add("Main Page");
			//listOfTitleStrings.add("API");

			//listOfPages = user.queryContent(listOfTitleStrings);
			String title = intent.getExtras().getString(WIKI_TITLE);
			listOfPages = querySearchResults(title);
			break;
		case CMD_WIKI_EXACT_PAGE:
			//Query.create().list("").apfrom("Java").aplimit(20).format("json");
			//openSearch();
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

	public List<Page> querySearchResults(String title) {
        /*User user = new User("", "", "http://en.wikipedia.org/w/api.php");
        user.login();*/
        // search for all pages which contain "forrest gump"
		
		if (title.indexOf('*') < 0) {
			title += "*";
		}
		
        String[] valuePairs = { "list", "search", "srsearch", title };
        String[] valuePairsContinue = new String[6];
        String srOffset = "0";
        for (int i = 0; i < valuePairs.length; i++) {
            valuePairsContinue[i] = valuePairs[i];
        }
        valuePairsContinue[4] = "sroffset"; // index of 4
        valuePairsContinue[5] = ""; // index of 5
        Connector connector = new Connector();
        List<SearchResult> resultSearchResults = new ArrayList<SearchResult>(1024);
        ArrayList<Page> allPageResult = new ArrayList<Page>();
        XMLSearchParser parser;
        try {
            // get all search results
            String responseBody = connector.queryXML(user, valuePairs);
            while (responseBody != null) {
                parser = new XMLSearchParser(responseBody);
                parser.parse();
                srOffset = parser.getSrOffset();
                //System.out.println(">>>>> " + srOffset);
                List<SearchResult> listOfSearchResults = parser.getSearchResultList();
                resultSearchResults.addAll(listOfSearchResults);
                /*for (SearchResult searchResult : listOfSearchResults) {
                    // print search result information
                    System.out.println("sr: " + searchResult.toString());
                }*/
                if (srOffset.length() > 0) {
                    // use the sroffset from the last query to get the next block of
                    // search results
                    valuePairsContinue[5] = srOffset;
                    responseBody = connector.queryXML(user, valuePairsContinue);
                } else {
                    break;
                }
            }
            // get the content of the category members with namespace==0
            int count = 0;
            List<String> strList = new ArrayList<String>();
            for (SearchResult searchResult : resultSearchResults) {
                if (searchResult.getNs().equals("0")) {
                    // namespace "0" - all titles without a namespace prefix
                    strList.add(searchResult.getTitle());
                    if (++count == 10) {
                        List<Page> listOfPages = user.queryContent(strList);
                        for (Page page : listOfPages) {
                            //System.out.println("p1: "+page.getTitle());
                            // print the raw content of the wiki page:
                            // System.out.println(page.getCurrentContent());
                        	allPageResult.add(page);
                        }
                        count = 0;
                        strList = new ArrayList<String>();
                    }
                }
            }
            if (count != 0) {
                List<Page> listOfPages = user.queryContent(strList);
                for (Page page : listOfPages) {
                    //System.out.println("p2: "+page.getTitle());
                    // print the raw content of the wiki page:
                    // System.out.println(page.getCurrentContent());

                	allPageResult.add(page);
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
			e.printStackTrace();
		}
		return allPageResult;
    }
	
	public List<Page> recentChanges() {
		
        String[] valuePairs = { "list", "recentchanges", "rclimit", "rcnamespace" };
        String[] valuePairsContinue = new String[5];
        String srOffset = "0";
        for (int i = 0; i < valuePairs.length; i++) {
            valuePairsContinue[i] = valuePairs[i];
        }
        valuePairsContinue[4] = ""; // index of 4
        Connector connector = new Connector();
        List<SearchResult> resultSearchResults = new ArrayList<SearchResult>(1024);
        ArrayList<Page> allPageResult = new ArrayList<Page>();
        XMLSearchParser parser;
        try {
            // get all search results
            String responseBody = connector.queryXML(user, valuePairs);
            while (responseBody != null) {
                parser = new XMLSearchParser(responseBody);
                parser.parse();
                srOffset = parser.getSrOffset();
                //System.out.println(">>>>> " + srOffset);
                List<SearchResult> listOfSearchResults = parser.getSearchResultList();
                resultSearchResults.addAll(listOfSearchResults);
                /*for (SearchResult searchResult : listOfSearchResults) {
                    // print search result information
                    System.out.println("sr: " + searchResult.toString());
                }*/
                if (srOffset.length() > 0) {
                    // use the sroffset from the last query to get the next block of
                    // search results
                    valuePairsContinue[5] = srOffset;
                    responseBody = connector.queryXML(user, valuePairsContinue);
                } else {
                    break;
                }
            }
            // get the content of the category members with namespace==0
            int count = 0;
            List<String> strList = new ArrayList<String>();
            for (SearchResult searchResult : resultSearchResults) {
                if (searchResult.getNs().equals("0")) {
                    // namespace "0" - all titles without a namespace prefix
                    strList.add(searchResult.getTitle());
                    if (++count == 10) {
                        List<Page> listOfPages = user.queryContent(strList);
                        for (Page page : listOfPages) {
                            //System.out.println("p1: "+page.getTitle());
                            // print the raw content of the wiki page:
                            // System.out.println(page.getCurrentContent());
                        	allPageResult.add(page);
                        }
                        count = 0;
                        strList = new ArrayList<String>();
                    }
                }
            }
            if (count != 0) {
                List<Page> listOfPages = user.queryContent(strList);
                for (Page page : listOfPages) {
                    //System.out.println("p2: "+page.getTitle());
                    // print the raw content of the wiki page:
                    // System.out.println(page.getCurrentContent());

                	allPageResult.add(page);
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
			e.printStackTrace();
		}
		return allPageResult;
    }
}
