package ir.occc.android;

import ir.occc.android.common.Common;
import ir.occc.android.common.QueryType;
import ir.occc.android.rss.RssService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;

/**
 * @author Ahmad
 *
 */
public class BaseV4Fragment extends Fragment {
	
	/**
	 * Start a service
	 * @param serviceClass The service class which want to start
	 * @param intent Intent that contain service requirements
	 */
	protected void startService(Class<?> serviceClass, Intent intent) {
		if (serviceClass == null) {
			return;
		}
		
		if (intent == null) {
			intent = new Intent(getActivity(), serviceClass);
		}
		intent.putExtra(Common.RECEIVER, resultReceiver);

		getActivity().stopService(intent);
		getActivity().startService(intent);
	}
	
	/** Stop running service
	 * @param serviceClass The service class which want to start
	 */
	protected void stopService(Class<?> serviceClass) {
		Intent intent = new Intent(getActivity(), serviceClass);
		
		getActivity().stopService(intent);
	}
	
	/**
	 * Once the {@link RssService} finishes its task, the result is sent to this ResultReceiver.
	 */
	private final ResultReceiver resultReceiver = new ResultReceiver(new Handler()) {
		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			receivedResult(resultCode, resultData);
		};
	};
	
	/**
	 * Override to receive results delivered to this object.
	 * @param Arbitrary result code delivered by the sender, as defined by the sender. 
	 * @param Any additional data provided by the sender.  
	 */
	protected void receivedResult(int resultCode, Bundle resultData) {
		
	}
	
	/**
	 * search for an item
	 * @param type Type of query
	 * @param query The query of search
	 */
	protected void search(QueryType type, String query) {
		
	}
}
