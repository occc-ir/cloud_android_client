package ir.occc.android;

import ir.occc.android.adapter.NavDrawerListAdapter;
import ir.occc.android.model.NavDrawerItem;

import java.util.ArrayList;

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

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

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
		// Photos
		/*navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Communities, Will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
		// Pages
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// What's hot, We  will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));
		 */

		// Recycle the typed array
		navMenuIcons.recycle();

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
		mDrawerListLeft.setAdapter(adapter);
		mDrawerListRight.setAdapter(adapter);

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

	private int getNewWikiArticleCount() {
		// TODO get new article from wiki
		return 14;
	}

	private void displayView(int position) {
		// update the main content by replacing fragments
		SharedPreferences sharedPreferences = getSharedPreferences("oCCc", MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();

		editor.putBoolean("isTest", isTest);
		
		switch (position) {
		case 0:
			fragment = new NewsFragment();
			activeFragment = Fragments.News;
			
			break;
		case 1:
			fragment = new WikiFragment();
			activeFragment = Fragments.Wiki;
			
			editor.putBoolean("wikiCounterVisible", false);
			editor.putInt("wikiCounterNumber", 0);
			
			navDrawerItems.get(1).setCounterVisibility(false);
			navDrawerItems.get(1).setCount("0");
			//            fragment = new FindPeopleFragment();
			break;
		case 2:
			//            fragment = new PhotosFragment();
			//			  activeFragment = Fragments.Photos;
			break;
		case 3:
			//			fragment = new WikiFragment();
			//			  activeFragment = Fragments.Wiki;
			break;
		case 4:
			//            fragment = new PagesFragment();
			//			  activeFragment = Fragments.Pages;
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
		getMenuInflater().inflate(R.menu.main, menu);
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
				NewsFragment newsFragment = (NewsFragment)fragment;
				newsFragment.refresh();
			} else if (fragment instanceof NewsContentFragment) {
				NewsContentFragment newsContentFragment = (NewsContentFragment)fragment;
				newsContentFragment.refresh();
			} else if (fragment instanceof WikiFragment) {
				WikiFragment wikiFragment = (WikiFragment)fragment;
				wikiFragment.refresh(QueryType.WikiTitle, " ");
			}
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
			displayView(position);
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
		
		/*if (fragment instanceof NewsFragment) {
			NewsFragment newsFragment = (NewsFragment)fragment;
			newsFragment.refresh();
		}*/
	}

	private void searchInWiki(String query) {
		mSearchView.clearFocus();
		
		if (fragment instanceof WikiFragment) {
			WikiFragment wikiFragment = (WikiFragment)fragment;
			if (!wikiFragment.isVisible()) {
				if (this.getSupportFragmentManager().getBackStackEntryCount() != 0) {
					this.getSupportFragmentManager().popBackStack();
					//this.getSupportFragmentManager().popBackStackImmediate();
				}
			}
			wikiFragment.refresh(QueryType.WikiTitle, query);
		}
	}
}
