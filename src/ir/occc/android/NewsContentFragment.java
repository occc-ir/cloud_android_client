package ir.occc.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NewsContentFragment extends Fragment {

	int mCurrentPage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/** Getting the arguments to the Bundle object */
		Bundle data = getArguments();

		/** Getting integer data of the key current_page from the bundle */
		mCurrentPage = data.getInt("current_page", 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Get data from RSS and set data to view

		View v = inflater.inflate(R.layout.fragment_news_content, container,false);

		TextView tvTitle = (TextView ) v.findViewById(R.id.tvNewsTitle);
		tvTitle.setText("Dynamic title #" + mCurrentPage);

		TextView tvContent = (TextView ) v.findViewById(R.id.tvNewsContent);
		tvContent.setText("You are viewing the page #" + mCurrentPage + "\n\n" + "Swipe Horizontally left / right");

		return v;
	}
}
