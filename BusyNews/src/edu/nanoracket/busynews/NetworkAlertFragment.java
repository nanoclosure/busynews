package edu.nanoracket.busynews;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

// When no network and cell signal are both unavailable, show the alert. 
public class NetworkAlertFragment extends DialogFragment {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
	
		View v = getActivity().getLayoutInflater()
				.inflate(R.layout.dialog_alert, null);
		
		return new AlertDialog.Builder(getActivity())
		           .setView(v)
		           .setTitle(R.string.network_connection_alert)
		           .setPositiveButton(android.R.string.ok, null)
		           .create();
	}

}
