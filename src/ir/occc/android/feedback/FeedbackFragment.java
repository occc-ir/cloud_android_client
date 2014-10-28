package ir.occc.android.feedback;

import ir.occc.android.BaseV4Fragment;
import ir.occc.android.R;

import java.net.MalformedURLException;
import java.net.URL;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class FeedbackFragment extends BaseV4Fragment implements
OnItemSelectedListener, OnClickListener {

	private View rootView;
	private Spinner spinner;
	private String feedbackType = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_feedback, container, false);
			spinner = (Spinner) rootView.findViewById(R.id.spinnerFeedback);
			spinner.setOnItemSelectedListener(this);
			
			Button btn = (Button)rootView.findViewById(R.id.btnSubmit);
			btn.setOnClickListener(this);
		}
		else {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			parent.removeView(rootView);
		}

		return rootView;
	}

	//@Override
	//public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	//}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		feedbackType = parent.getItemAtPosition(position).toString();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}

	@Override
	public void onClick(View v) {
		try {
			EditText et = (EditText)rootView.findViewById(R.id.etFeedback);
			String message = feedbackType + "\n\t" + et.getText().toString(); 
			
			SendEmail(message);
			SendToService(message);
			
		} catch (MalformedURLException e) {
			
		}
	}

	private void SendToService(String message)
			throws MalformedURLException {
		FeedbackSubmitter feedback = new FeedbackSubmitter(rootView);
		
		FeedbackData fd = new FeedbackData();
		fd.setURL(new URL("http://occc.ir/blah.asmx"));
		fd.setData(message);
		/*AsyncTask<URL, Void, String> execute = */
		feedback.execute(fd);
	}
	
	private void SendEmail(String message) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"ahmad.babaei@gmail.com"});
		i.putExtra(Intent.EXTRA_SUBJECT, "occc_androidclient_report");
		i.putExtra(Intent.EXTRA_TEXT   , message);
		
		try {
		    startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(rootView.getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
}
