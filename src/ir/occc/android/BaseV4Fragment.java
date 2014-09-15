package ir.occc.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.util.Log;

public class BaseV4Fragment extends Fragment {
	
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
			Log.d("oCCc", "result received!!!");
			receivedResult(resultCode, resultData);
		};
	};
	
	protected void receivedResult(int resultCode, Bundle resultData) {
		
	}
}
