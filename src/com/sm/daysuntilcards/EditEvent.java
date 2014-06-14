package com.sm.daysuntilcards;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.sm.daysuntilcards.CreateEvent.DatePickerFragment;
import com.sm.daysuntilcards.CreateEvent.TimePickerFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import android.os.Build;
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
	static boolean weekends = false, since = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		String eventString = intent.getStringExtra("com.sm.daysuntilcards.EVENT");
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
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		setContentView(R.layout.fragment_create_event);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		twentyFourHourClock = prefs.getBoolean("24hour",false);
		final Calendar c = Calendar.getInstance();
		
		EditText eventText = (EditText) findViewById(R.id.eventText);
		EditText repeatRateText = (EditText) findViewById(R.id.repeatRateEditText);
		TextView dateView = (TextView) findViewById(R.id.dateView);
		TextView timeView = (TextView) findViewById(R.id.timeView);
		TextView repeatRateView1 = (TextView) findViewById(R.id.repeatRateView1);
		TextView repeatRateView2 = (TextView) findViewById(R.id.repeatRateView2);
		CheckBox weekBox = (CheckBox) findViewById(R.id.weekBox);
		CheckBox sinceBox = (CheckBox) findViewById(R.id.sinceBox);
		Button dateButton = (Button) findViewById(R.id.dateButton);
		Button timeButton = (Button) findViewById(R.id.timeButton);
		Button createButton = (Button) findViewById(R.id.createButton);
		Spinner repeatSpinner = (Spinner) findViewById(R.id.repeatSpinner);
		
		eventText.setText(name);
		if (repeatRate != 0) repeatRateText.setText(""+repeatRate);
	}
}