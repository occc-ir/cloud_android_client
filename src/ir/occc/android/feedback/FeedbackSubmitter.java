package ir.occc.android.feedback;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

public class FeedbackSubmitter extends AsyncTask<FeedbackData, Void, String>{
	
	private View rootView = null;
	
	public FeedbackSubmitter(View view) {
		rootView = view;
	}
	
	@Override
	protected String doInBackground(FeedbackData... data) {
		int count = data.length;

		String responseString = "";

		for (int i = 0; i < count; i++) {
			try {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpParams params = client.getParams();
				HttpConnectionParams.setConnectionTimeout(params, 20000);
				HttpConnectionParams.setSoTimeout(params, 25000);
				HttpProtocolParams.setUseExpectContinue(client.getParams(), true);

				HttpPost post = new HttpPost(data[i].getURL().toURI());
				post.setHeader("soapaction", "POST");
				post.setHeader("Content-Type", "text/xml; charset:utf-8");

				String envelop = data[i].getData();
				try {
					HttpEntity sendEntity = new StringEntity(envelop);
					post.setEntity(sendEntity);

					ResponseHandler<String> rh = new ResponseHandler<String>() {

						@Override
						public String handleResponse(HttpResponse response)
								throws ClientProtocolException, IOException {

							HttpEntity responseEntity = response.getEntity();

							StringBuffer out = new StringBuffer();
							byte[] b = EntityUtils.toByteArray(responseEntity);

							out.append(new String(b, 0, b.length));

							return out.toString();
						}
					};

					responseString = client.execute(post, rh);

				} catch (IOException e) {

				}

				client.getConnectionManager().shutdown();
				/*HttpURLConnection con = (HttpURLConnection)urls[i].openConnection();
        		 con.setRequestMethod("POST");
        		 con.setRequestProperty("content-property", "");*/
			} catch (/*IOException | */URISyntaxException e) {

			}

			if (isCancelled()) break;
		}

		return responseString;
	}

	protected void onProgressUpdate(Void... progress) {
		//setProgressPercent(progress[0]);
	}

	protected void onPostExecute(String result) {
		//showDialog("Downloaded " + result + " bytes");
		if (rootView != null && rootView.isEnabled()) {
			Toast.makeText(rootView.getContext(), "Feedback sent. Thank you for feedback.", Toast.LENGTH_LONG).show();
		}
	}
}
