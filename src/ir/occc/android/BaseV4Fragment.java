package ir.occc.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;

public class BaseV4Fragment extends Fragment {
	
	/**
	 * Start a service
	 * @param serviceClass The service class which want to start
	 * @param command The name of command
	 * @param titles String array of title which want to get its content from WIKI service
	 */
	protected void startService(Class<?> serviceClass, Integer command, String[] titles) {
		if (serviceClass == null) {
			return;
		}
		
		Intent intent = new Intent(getActivity(), serviceClass);
		intent.putExtra(WikiService.RECEIVER, resultReceiver);

		if (command != null) {
			intent.putExtra(WikiService.COMMAND, command);			
		}
		if (titles != null) {
			intent.putExtra(WikiService.WIKI_TITLES, titles);	
		}
		
		getActivity().startService(intent);
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
}
