package com.sm.daysuntilcards;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public class CountdownDialog extends DialogFragment {

	static CountdownDialog newInstance(String jsonstring){
		CountdownDialog cd = new CountdownDialog();
		Bundle args = new Bundle();
		args.putString("jsonstring", jsonstring);
		cd.setArguments(args);
		return cd;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		JSONObject jsonEvent = new JSONObject();
		try {
			jsonEvent = new JSONObject(getArguments().getString("jsonstring"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		View v = inflater.inflate(R.layout.countdown_dialog, null);
		TextView titleDialogView = (TextView)getActivity().findViewById(R.id.titleDialogView);
		/*try {
			titleDialogView.setText(jsonEvent.getString("name"));
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
	//	titleDialogView.setText("aaa");
		return v;
	}
}
