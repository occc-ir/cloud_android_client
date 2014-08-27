package ir.occc.android.adapter;

import ir.occc.android.NewsContentFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

public class NewsPagesAdapter extends FragmentPagerAdapter {

	final int PAGE_COUNT = 5;

	public NewsPagesAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public int getCount() {
		return PAGE_COUNT;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return false;
	}

	@Override
	public Fragment getItem(int pageNum) {
		NewsContentFragment newsContentFragment = new NewsContentFragment();
		Bundle data = new Bundle();
		data.putInt("current_page", pageNum);
		newsContentFragment.setArguments(data);
		return null;
	}
}
