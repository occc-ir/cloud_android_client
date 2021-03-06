package ir.occc.android;

import ir.occc.android.adapter.NavDrawerListAdapter;
import ir.occc.android.adapter.RightNavDrawerListAdapter;
import ir.occc.android.common.Common;
import ir.occc.android.common.Fragments;
import ir.occc.android.common.QueryType;
import ir.occc.android.feedback.FeedbackFragment;
import ir.occc.android.irc.IrcDemoFragment;
import ir.occc.android.irc.activity.AboutActivity;
import ir.occc.android.model.NavDrawerItem;
import ir.occc.android.rss.NewsContentFragment;
import ir.occc.android.rss.NewsFragment;
import ir.occc.android.wiki.WikiContentFragment;
import ir.occc.android.wiki.WikiFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements OnQueryTextListener {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListLeft;
	private ListView mDrawerListRight;
	private RelativeLayout mDrawerRight;
	private ActionBarDrawerToggle mDrawerToggle;
	private SearchView mSearchView;
	private Fragments activeFragment;
	private Fragment fragment;
	private Boolean isTest;
	private String rssLink;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter navLeftAdapter;
	private RightNavDrawerListAdapter navRightAdapter;

	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sharedPreferences = getSharedPreferences("oCCc", MODE_PRIVATE);
		try {
			activeFragment = Fragments.toFragments(sharedPreferences.getString("fragment", "News"));
		} catch (Exception e) {
			activeFragment = Fragments.News;
		}
		try {
			isTest = sharedPreferences.getBoolean("isTest", false);
		} catch (Exception e) {
			isTest = false;
		}
		
		CheckBox chbIsTest = (CheckBox)findViewById(R.id.checkBoxTestValues);
		chbIsTest.setChecked(isTest);
		chbIsTest.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isTest = isChecked;
				SharedPreferences sharedPreferences = getSharedPreferences("oCCc", MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();
				editor.putBoolean("isTest", isTest);
				editor.commit();
			}
		});

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerListLeft = (ListView) findViewById(R.id.list_slidermenuleft);
		mDrawerListLeft.setOnItemClickListener(new LeftSlideMenuClickListener());
		
		mDrawerRight = (RelativeLayout) findViewById(R.id.slidermenuright);
		mDrawerRight.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d("oCCc", "relative layout view touched");
				mSearchView.setIconified(false);
				
				return true;
			}
		});

		mDrawerListRight = (ListView) findViewById(R.id.list_slidermenuright);
		mDrawerListRight.setOnItemClickListener(new RightSlideMenuClickListener());

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// News
		prepareNewsItemMenu();
		// Wiki
		prepareWikiItemMenu();
		// IRC Demo
		prepareIrcWebItemMenu();
		// IRC Console
		prepareIrcConsoleItemMenu();
		// Feedback
		//prepareFeedbackItemMenu();

		// Recycle the typed array
		navMenuIcons.recycle();

		// setting the nav drawer list adapter
		navLeftAdapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
		mDrawerListLeft.setAdapter(navLeftAdapter);

		/*ArrayList<String> rightNavDrawerItems = new ArrayList<String>();
		rightNavDrawerItems.addAll(Arrays.asList(getResources().getStringArray(R.array.rss_feed_items)));
		navRightAdapter = new RightNavDrawerListAdapter(getApplicationContext(), rightNavDrawerItems); 
		mDrawerListRight.setAdapter(navRightAdapter);*/

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
				){
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}

		mSearchView = (SearchView)findViewById(R.id.searchViewMain);
		mSearchView.setOnQueryTextListener(this);
		mSearchView.requestFocus();
	}

	@Override
	public boolean onSearchRequested() {
		Log.d("oCCc", "Search Requested!");
		return super.onSearchRequested();
	}
	
	private void prepareNewsItemMenu() {
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		
	}

	private void prepareWikiItemMenu() {
		
		/*ArrayList<String> rightNavDrawerItems = new ArrayList<String>();
		rightNavDrawerItems.add(null);
		navRightAdapter = new RightNavDrawerListAdapter(getApplicationContext(), rightNavDrawerItems); 
		mDrawerListRight.setAdapter(navRightAdapter);*/
		
		Boolean isVisible = false;
		try {
			isVisible = sharedPreferences.getBoolean("wikiCounterVisible", true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int unreadArticle = 0;
		try {
			unreadArticle = sharedPreferences.getInt("wikiCounterNumber", 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int newArticleCounter = getNewWikiArticleCount();

		if (unreadArticle > 0) {
			unreadArticle += newArticleCounter;
			isVisible = true;
		} 
		else {
			if (newArticleCounter > 0) {
				unreadArticle = newArticleCounter;
				isVisible = true;
			}
			else {
				isVisible = false;
			}
		}

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1), isVisible, String.valueOf(unreadArticle)));
	}
	
	private void prepareIrcWebItemMenu() {
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
	}
	
	private void prepareIrcConsoleItemMenu() {
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
	}
	
	/*private void prepareFeedbackItemMenu() {
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));		
	}*/

	private int getNewWikiArticleCount() {
		// TODO get new article from wiki
		return 14;
	}

	private void displayView(int position) {
		// update the main content by replacing fragments
		SharedPreferences sharedPreferences = getSharedPreferences("oCCc", MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();

		editor.putBoolean("isTest", isTest);

		CheckBox chbIsTest = (CheckBox)findViewById(R.id.checkBoxTestValues);
		chbIsTest.setVisibility(View.GONE);
		
		if (mSearchView == null) {
			mSearchView = (SearchView)findViewById(R.id.searchViewMain);
		}
		mSearchView.setVisibility(View.GONE);
		
		ArrayList<String> rightNavDrawerItems = new ArrayList<String>();
		
		switch (position) {
		case 0:
			
			rightNavDrawerItems.addAll(Arrays.asList(getResources().getStringArray(R.array.rss_feed_items)));
			navRightAdapter = new RightNavDrawerListAdapter(getApplicationContext(), rightNavDrawerItems); 
			mDrawerListRight.setAdapter(navRightAdapter);
			
			fragment = new NewsFragment();
			activeFragment = Fragments.News;
			
			if (rssLink == null || rssLink.length() == 0) {
				try {
					rssLink = sharedPreferences.getString(Common.RSS_LINK, "");
				} catch (Exception e) {
					
				}
			}
			
			Bundle data = new Bundle();
			data.putString(Common.RSS_LINK, rssLink);
			fragment.setArguments(data);
			break;
		case 1:
			rightNavDrawerItems.addAll(Arrays.asList(getResources().getStringArray(R.array.wiki_link_items)));
			navRightAdapter = new RightNavDrawerListAdapter(getApplicationContext(), rightNavDrawerItems); 
			mDrawerListRight.setAdapter(navRightAdapter);
			
			fragment = new WikiFragment();
			activeFragment = Fragments.Wiki;
			
			editor.putBoolean("wikiCounterVisible", false);
			editor.putInt("wikiCounterNumber", 0);
			
			navDrawerItems.get(1).setCounterVisibility(false);
			navDrawerItems.get(1).setCount("0");
			//            fragment = new FindPeopleFragment();
			
			chbIsTest.setVisibility(View.VISIBLE);
			mSearchView.setVisibility(View.VISIBLE);
			break;
		case 2:
			mDrawerListRight.setAdapter(null);
			
			fragment = new IrcDemoFragment();
			activeFragment = Fragments.IrcWeb;
			
			break;
		case 3:
			startActivity(new Intent(this, ServersActivity.class));
			break;
		case 4:
			/*fragment = new FeedbackFragment();
			activeFragment = Fragments.Feedback;*/
			break;
		case 5:
			//            fragment = new WhatsHotFragment();
			//			  activeFragment = Fragments.WhatsHot;
			break;

		default:
			break;
		}
		
		editor.putString("fragment", activeFragment.toString());
		editor.commit();

		if (fragment != null) {
			final FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerListLeft.setItemChecked(position, true);
			mDrawerListLeft.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerListLeft);
			mDrawerLayout.closeDrawer(mDrawerRight);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		switch (activeFragment) {
		case IrcConsole:
			getMenuInflater().inflate(R.menu.irc_servers, menu);
			break;
		default:
			getMenuInflater().inflate(R.menu.main, menu);	
			break;
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		mDrawerLayout.closeDrawer(mDrawerRight);
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			Log.d("oCCc", "Setting selected!!!");
			return true;
		case R.id.action_refresh:
			Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
			if (fragment instanceof NewsFragment) {
				
				if (rssLink == null || rssLink.length() == 0) {
					try {
						rssLink = sharedPreferences.getString(Common.RSS_LINK, "");
					} catch (Exception e) {
						
					}
				}
				
				NewsFragment newsFragment = (NewsFragment)fragment;
				newsFragment.refresh(rssLink);
			} else if (fragment instanceof NewsContentFragment) {
				NewsContentFragment newsContentFragment = (NewsContentFragment)fragment;
				newsContentFragment.refresh();
			} else if (fragment instanceof WikiFragment) {
				WikiFragment wikiFragment = (WikiFragment)fragment;
				
				wikiFragment.wikiTitle = Arrays.asList(getResources().getStringArray(R.array.wiki_link_items)).get(0);
				int idx = wikiFragment.wikiTitle.indexOf(',');
				wikiFragment.wikiTitle = wikiFragment.wikiTitle.substring(idx + 1);
				
				wikiFragment.refresh(QueryType.WikiTitle, wikiFragment.wikiTitle);
			} else if (fragment instanceof IrcDemoFragment) {
				IrcDemoFragment ircDemoFragment = (IrcDemoFragment)fragment;
				ircDemoFragment.refresh();
			} else if (fragment instanceof FeedbackFragment) {
				IrcDemoFragment ircDemoFragment = (IrcDemoFragment)fragment;
				ircDemoFragment.refresh();
			}
			return true;
		case R.id.disconnect_all:
                /*ArrayList<Server> mServers = Yaaic.getInstance().getServersAsArrayList();
                for (Server server : mServers) {
                    if (binder.getService().hasConnection(server.getId())) {
                        server.setStatus(Status.DISCONNECTED);
                        server.setMayReconnect(false);
                        binder.getService().getConnection(server.getId()).quitServer();
                    }
                }*/
                // ugly
                //binder.getService().stopForegroundCompat(R.string.app_name);
		case R.id.about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/***
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerListLeft) || mDrawerLayout.isDrawerOpen(mDrawerRight);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		menu.findItem(R.id.action_refresh).setVisible(!drawerOpen);

		if (mDrawerLayout.isDrawerOpen(mDrawerListLeft)) {
			mDrawerLayout.closeDrawer(mDrawerRight);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * Slide menu item click listener
	 * */
	public class LeftSlideMenuClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}

	}

	public class RightSlideMenuClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			if (fragment instanceof NewsFragment || fragment instanceof NewsContentFragment) {

				TextView tv = (TextView)view.findViewById(R.id.resultTitle);
				rssLink = tv.getTag().toString();
				
				/*RightNavDrawerListAdapter adapter = (RightNavDrawerListAdapter)parent.getAdapter();
				rssLink = adapter.getItem(position).toString();*/
				
				SharedPreferences sharedPreferences = getSharedPreferences("oCCc", MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();
				editor.putString(Common.RSS_LINK, rssLink);
				editor.commit();
				
				if (fragment instanceof NewsFragment) {
					NewsFragment newsFragment = (NewsFragment)fragment;
					newsFragment.refresh(rssLink);
				}
				
				mDrawerLayout.closeDrawer(mDrawerRight);
			}
			else if (fragment instanceof WikiFragment || fragment instanceof WikiContentFragment) {
				
				fragment.getFragmentManager().popBackStack();
				
				TextView tv = (TextView)view.findViewById(R.id.resultTitle);
				String title = tv.getTag().toString();
				
				if (fragment instanceof WikiFragment) {
					WikiFragment wikiFragment = (WikiFragment)fragment;
					
					List<String> wikiLinks = Arrays.asList(getResources().getStringArray(R.array.wiki_link_items));
					for (int i = 0; i < wikiLinks.size(); i++) {
						int idx = wikiLinks.get(i).indexOf(',');
						wikiLinks.set(i, wikiLinks.get(i).substring(idx + 1));
					}
					/*String tagTitle = wikiLinks.get(4);
					int idx = tagTitle.indexOf(',');
					tagTitle = tagTitle.substring(idx + 1);
					if (tagTitle.equals("RecentChanges")) {*/
					if (wikiLinks.indexOf(title) == 2 /*Title.equals("RecentChanges")*/) {
						wikiFragment.refresh(QueryType.WikiSpecialPage, title);
					}
					else {
						wikiFragment.refresh(QueryType.WikiTitle, title);
					}
				}
				
				mDrawerLayout.closeDrawer(mDrawerRight);
			}
		}

	}

	@Override
	public boolean onQueryTextChange(String newText) {
		
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		switch (activeFragment) {
		case News:
			searchInNews(query);
			return true;
		case Wiki:
			searchInWiki(query);
			return true;
		default:
			break;
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (this.getSupportFragmentManager().getBackStackEntryCount() != 0) {
				this.getSupportFragmentManager().popBackStack();
				//this.getSupportFragmentManager().popBackStackImmediate();
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	private void searchInNews(String query) {
		mSearchView.clearFocus();
		mDrawerLayout.closeDrawer(mDrawerRight);
		/*if (fragment instanceof NewsFragment) {
			NewsFragment newsFragment = (NewsFragment)fragment;
			newsFragment.refresh();
		}*/
	}

	private void searchInWiki(String query) {
		mSearchView.clearFocus();
		mDrawerLayout.closeDrawer(mDrawerRight);
		
		if (fragment instanceof WikiFragment) {
			WikiFragment wikiFragment = (WikiFragment)fragment;
			if (!wikiFragment.isVisible()) {
				if (this.getSupportFragmentManager().getBackStackEntryCount() != 0) {
					this.getSupportFragmentManager().popBackStack();
					//this.getSupportFragmentManager().popBackStackImmediate();
				}
			}
			wikiFragment.wikiTitle = query;
			// it doesn't need call "refresh" method, it is called on "onCreateView".
			//wikiFragment.refresh(QueryType.WikiTitle, query);
		}		
	}
}
