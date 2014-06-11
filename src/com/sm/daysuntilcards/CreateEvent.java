package com.sm.daysuntilcards;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class CreateEvent extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_create_event);
		
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		final EditText eventText = (EditText) findViewById(R.id.eventText);
		final TextView timeView = (TextView) findViewById(R.id.timeView);
		timeView.setText(String.format("%02d",hour)+":"+String.format("%02d",minute));
		final TextView dateView = (TextView) findViewById(R.id.dateView);
		dateView.setText("1/2/3");
		
		Button timeButton = (Button) findViewById(R.id.timeButton);
		timeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				showTimePickerDialog(v);
			}
		});
		Button dateButton = (Button) findViewById(R.id.dateButton);
		dateButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				showDatePickerDialog(v);
			}
		});
		
		final CheckBox weekBox = (CheckBox) findViewById(R.id.weekBox);
		Button createButton = (Button) findViewById(R.id.createButton);
		createButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				boolean weekbool = weekBox.isChecked();
				
				JSONObject obj = new JSONObject();
				try{
					String filename = eventText.getText().toString();
					obj.put("name", eventText.getText());
					obj.put("date", dateView.getText());
					obj.put("time", timeView.getText());
					obj.put("weekends", weekbool);
					String stringDate = obj.toString();
					FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
					fos.write(stringDate.getBytes());
					fos.close();
				} catch (JSONException e){
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				finish();
			}
		});
	}
	
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getFragmentManager(), "timePicker");
	}
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	
	public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
		@Override
		public Dialog onCreateDialog (Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			TextView timeText = (TextView)getActivity(). findViewById(R.id.timeView);
			timeText.setText(String.format("%02d",hourOfDay)+":"+String.format("%02d",minute));
		}
	}
	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
				
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
			TextView dateView = (TextView)getActivity(). findViewById(R.id.dateView);
			dateView.setText(day+"/"+month+"/"+year);
		}
	}
}
