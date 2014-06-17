package com.sm.daysuntilcards;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.preference.PreferenceManager;

@SuppressLint("SimpleDateFormat")
public class EditEvent extends Activity {
	static int day = 1;
	static int month = 2;
	static int year = 3;
	static int hour = 0;
	static int minute = 0;
	static SimpleDateFormat fromDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	static SimpleDateFormat toDateFormat = new SimpleDateFormat("MMM d, yyyy");
	static SimpleDateFormat fromTimeFormat = new SimpleDateFormat("HH:mm");
	static SimpleDateFormat toTimeFormat = new SimpleDateFormat("h:mm a");
	static Date eventDate = new Date();
	static boolean twentyFourHourClock = false;
	static boolean daysSinceBox = false;
	static String name = "";
	static int repeat = 0, repeatRate = 0;
	static boolean weekends = false, since = false, notify = false;
	static String oldname = "";
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		final String eventString = intent.getStringExtra("com.sm.daysuntilcards.EVENT");
		final int eventPosition = intent.getIntExtra("com.sm.daysuntilcards.POSITION", 0);
		final boolean untilList = intent.getBooleanExtra("com.sm.daysuntilcards.UNTIL", true);
		
		JSONObject jsonEvent = new JSONObject();
		try {
			jsonEvent = new JSONObject(eventString);
			hour = jsonEvent.getInt("hour");
			minute = jsonEvent.getInt("minute");
			year = jsonEvent.getInt("year");
			month = jsonEvent.getInt("month");
			day = jsonEvent.getInt("day");
			name = jsonEvent.getString("name");
			weekends = jsonEvent.getBoolean("weekends");
			since = jsonEvent.getBoolean("since");
			repeat = jsonEvent.getInt("repeat");
			repeatRate = jsonEvent.getInt("repeatRate");
			notify = jsonEvent.getBoolean("notify");
			oldname = name;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		setContentView(R.layout.fragment_create_event);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		twentyFourHourClock = prefs.getBoolean("24hour",false);
		final Calendar c = Calendar.getInstance();
		
		final EditText eventText = (EditText) findViewById(R.id.eventText);
		final EditText repeatRateText = (EditText) findViewById(R.id.repeatRateEditText);
		final TextView dateView = (TextView) findViewById(R.id.dateView);
		final TextView timeView = (TextView) findViewById(R.id.timeView);
		final CheckBox weekBox = (CheckBox) findViewById(R.id.weekBox);
		final CheckBox sinceBox = (CheckBox) findViewById(R.id.sinceBox);
		final Button dateButton = (Button) findViewById(R.id.dateButton);
		final Button timeButton = (Button) findViewById(R.id.timeButton);
		final Button createButton = (Button) findViewById(R.id.createButton);
		final Spinner repeatSpinner = (Spinner) findViewById(R.id.repeatSpinner);
		final CheckBox notifyBox = (CheckBox) findViewById(R.id.notifyCheckBox);
		
		eventText.setText(name);
		
		if (notify){
			notifyBox.setChecked(true);
		} else {
			notifyBox.setChecked(false);
		}
		
		if (repeatRate != 0) repeatRateText.setText(""+repeatRate);
		weekBox.setChecked(weekends);
		sinceBox.setChecked(since);
		try {
			dateView.setText(toDateFormat.format(fromDateFormat.parse(day+"/"+(month+1)+"/"+year)));
			timeView.setText(toTimeFormat.format(fromTimeFormat.parse(String.format("%02d",hour)+":"+String.format("%02d",minute))));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if (sinceBox.isChecked()){
			repeatSpinner.setEnabled(false);
			showSpinnerMessages(false);
		}
		sinceBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Date currentDate = null;
				try {
					currentDate = fromDateFormat.parse(c.get(Calendar.DATE)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (isChecked || !eventDate.after(currentDate)){
					repeatSpinner.setEnabled(false);
					repeatSpinner.setSelection(0); //also sets showSpinnerMessages(false)
					if (!isChecked) daysSinceBox = false;
				} else {
					repeatSpinner.setEnabled(true);
					daysSinceBox = false;
				}
			}
		});
		
		ArrayAdapter<CharSequence> repeatadapter = ArrayAdapter.createFromResource(this, R.array.spinnerArray, android.R.layout.simple_spinner_item);
		repeatadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		repeatSpinner.setAdapter(repeatadapter);
		repeatSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0){ //don't repeat
					showSpinnerMessages(false);
				} else {
					if (position == 1){
						TextView repeatRateView2 = (TextView) findViewById(R.id.repeatRateView2);
						repeatRateView2.setText("days");
					} else if (position == 2){
						TextView repeatRateView2 = (TextView) findViewById(R.id.repeatRateView2);
						repeatRateView2.setText("weeks");
					} else if (position == 3) {
						TextView repeatRateView2 = (TextView) findViewById(R.id.repeatRateView2);
						repeatRateView2.setText("months");
					} else {
						TextView repeatRateView2 = (TextView) findViewById(R.id.repeatRateView2);
						repeatRateView2.setText("years");
					}
					showSpinnerMessages(true);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		repeatSpinner.setSelection(repeat);
		createButton.setText("Edit Event");
		
		timeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				showTimePickerDialog(v);
			}
		});
		dateButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				showDatePickerDialog(v);
			}
		});
		
		createButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v){
				if (eventText.getText().toString().matches("")){
					Toast.makeText(EditEvent.this, "Missing event name", Toast.LENGTH_SHORT).show();
				    return;
				} else if (repeatSpinner.getSelectedItemPosition() != 0 &&
						(repeatRateText.getText().toString().matches("") ||
								Integer.parseInt(repeatRateText.getText().toString()) == 0)){
					Toast.makeText(EditEvent.this, "Missing repeat rate", Toast.LENGTH_SHORT).show();
				    return;
				} else {
					String eventName = eventText.getText().toString().replaceAll("/", "PARSE"); //fix linux naming bug (canont contain "/")
					if (repeatSpinner.getSelectedItemPosition() == 0) repeatRateText.setText("0");
					boolean weekbool = weekBox.isChecked();
					JSONObject obj = new JSONObject();
					try{
						String filename = eventName;
						obj.put("name", eventName);
						obj.put("day", day);
						obj.put("month", month);
						obj.put("year", year);
						obj.put("hour", hour);
						obj.put("minute", minute);
						obj.put("weekends", weekbool);
						obj.put("since", sinceBox.isChecked());
						obj.put("repeat", repeatSpinner.getSelectedItemPosition());
						obj.put("repeatRate", Integer.parseInt(repeatRateText.getText().toString()));
						obj.put("notify", notifyBox.isChecked());
						String stringDate = obj.toString();
						deleteFile(oldname);
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
			}
		});
	}
	
	public void showSpinnerMessages(boolean show){
		final TextView repeatRateView1 = (TextView) findViewById(R.id.repeatRateView1);
		final TextView repeatRateView2 = (TextView) findViewById(R.id.repeatRateView2);
		final EditText repeatRateEditText = (EditText) findViewById(R.id.repeatRateEditText);
		if (show){
			repeatRateView1.setVisibility(View.VISIBLE);
			repeatRateView2.setVisibility(View.VISIBLE);
			repeatRateEditText.setVisibility(View.VISIBLE);
		} else {
			repeatRateView1.setVisibility(View.INVISIBLE);
			repeatRateView2.setVisibility(View.INVISIBLE);
			repeatRateEditText.setVisibility(View.INVISIBLE);
		}
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
			return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay) {
			hour = hourOfDay;
			minute = minuteOfDay;
			TextView timeText = (TextView)getActivity(). findViewById(R.id.timeView);
			String fromTimeString = String.format("%02d",hour)+":"+String.format("%02d",minute);
			String outputTimeString = fromTimeString;
			Date eventTime = new Date();
			if (!twentyFourHourClock){
				try {
					eventTime = fromTimeFormat.parse(outputTimeString);
					outputTimeString = toTimeFormat.format(eventTime);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			timeText.setText(outputTimeString);
		}
	}
	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		public void onDateSet(DatePicker view, int yearSet, int monthSet, int daySet) {
			year = yearSet;
			month = monthSet;
			day = daySet;
			TextView dateView = (TextView)getActivity(). findViewById(R.id.dateView);
			String dateString = day+"/"+(month+1)+"/"+year;
			String outputDateString = "";
			try {
				eventDate = fromDateFormat.parse(dateString);
				outputDateString = toDateFormat.format(eventDate);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			dateView.setText(outputDateString);
			
			Calendar c = Calendar.getInstance();
			Date currentDate=null,eventDate = null;
			try {
				currentDate = fromDateFormat.parse(c.get(Calendar.DATE)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR));
				eventDate = fromDateFormat.parse(dateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Spinner repeatSpinner = (Spinner) getActivity().findViewById(R.id.repeatSpinner);
			CheckBox sinceBox = (CheckBox) getActivity().findViewById(R.id.sinceBox);
			if (!eventDate.after(currentDate) || sinceBox.isChecked()){
				repeatSpinner.setEnabled(false);
				repeatSpinner.setSelection(0);
			} else {
				repeatSpinner.setEnabled(true);
			}
		}
	}
}