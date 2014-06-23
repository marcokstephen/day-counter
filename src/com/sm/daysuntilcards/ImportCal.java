package com.sm.daysuntilcards;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Calendars;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class ImportCal extends Activity {
	private List<Boolean> checkedStatus;
	private static List<CalEvent> calendarList;
	static int toDay=0,toMonth=0,toYear=0;
	static int fromDay=0,fromMonth=0,fromYear=0;
	static Time fromTime;
	static Time toTime;
	public static ImportListAdapter ila;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		setTheme(MainActivity.APP_THEME);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.import_cal_layout);
		
		Calendar c = Calendar.getInstance();
		toDay = c.get(Calendar.DATE);
		toMonth = (c.get(Calendar.MONTH)+1);
		toYear = c.get(Calendar.YEAR);
		fromDay = c.get(Calendar.DATE);
		fromMonth = (c.get(Calendar.MONTH)-1);
		fromYear = c.get(Calendar.YEAR);
		final ListView importList = (ListView) findViewById(R.id.import_listview);
		fromTime = new Time();
		fromTime.set(fromDay, fromMonth, fromYear);
		toTime = new Time();
		toTime.set(toDay,toMonth,toYear);
		calendarList = getCalendarEvents(fromTime.toMillis(false),toTime.toMillis(false));
		sortCalendarList(calendarList);

		ila = new ImportListAdapter(this);
		importList.setAdapter(ila);
		
		Button cancelButton = (Button) findViewById(R.id.cancel_import_button);
		Button acceptButton = (Button) findViewById(R.id.import_button);
		final TextView fromDateView = (TextView) findViewById(R.id.fromDateView);
		final TextView toDateView = (TextView) findViewById(R.id.toDateView);
		fromDateView.setText(dateToString(fromDay,fromMonth,fromYear));
		toDateView.setText(dateToString(toDay,toMonth,toYear));
		
		toDateView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				showToDatePickerDialog(v);
				Time ttime = new Time();
				ttime.set(toDay,toMonth,toYear);
				toTime = ttime;
				calendarList = getCalendarEvents(fromTime.toMillis(false),ttime.toMillis(false));
				sortCalendarList(calendarList);
				ImportListAdapter newila = new ImportListAdapter(ImportCal.this);
				importList.setAdapter(newila);
			}
		});
		fromDateView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				showFromDatePickerDialog(v);
				Time ftime = new Time();
				ftime.set(fromDay,fromMonth,fromYear);
				fromTime = ftime;
				calendarList = getCalendarEvents(ftime.toMillis(false),toTime.toMillis(false));
				sortCalendarList(calendarList);
				ImportListAdapter newila = new ImportListAdapter(ImportCal.this);
				importList.setAdapter(newila);
			}
		});
		
		acceptButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				try{
					addCheckedEvents();
				} catch (JSONException e){
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				finish();
			}
		});
		cancelButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	private void updateTo(){
		
	}
	
	private void addCheckedEvents() throws JSONException, IOException{
		List<CalEvent> callist = calendarList;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean daysSince = prefs.getBoolean("daysSinceDefault",false);
		
		for (int i = 0; i < checkedStatus.size(); i++){
			if (checkedStatus.get(i)){
				CalEvent ce = callist.get(i);
				JSONObject obj = new JSONObject();
				String name = ce.getName().replaceAll("/", "PARSE");
				Time time = new Time();
				time.set(ce.getDate());
				int day = time.monthDay;
				int month = time.month;
				int year = time.year;
				int hour = time.hour;
				int minute = time.minute;
				obj.put("name", name);
				obj.put("day", day);
				obj.put("month", month);
				obj.put("year", year);
				obj.put("hour", hour);
				obj.put("minute", minute);
				obj.put("weekends", false);
				obj.put("since", daysSince);
				obj.put("repeat", false);
				obj.put("repeatRate", 0);
				obj.put("notify", false);
				obj.put("alarmid", Integer.parseInt(month+""+day+""+hour+""+minute));
				String stringDate = obj.toString();
				String filename = name;
				FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
				fos.write(stringDate.getBytes());
				fos.close();
			}
		}
	}
	
	@SuppressWarnings("static-access")
	private List<CalEvent> getCalendarEvents(long startTime, long endTime){
		List<CalEvent> callist = new ArrayList<CalEvent>();
		String[] projection = new String[] { "calendar_id","title","dtstart" };
		ContentResolver cr = getContentResolver();
		Uri uri = Calendars.CONTENT_URI;

		String selection = "((dtstart >= "+startTime+") AND (dtend <= "+endTime+"))";
		
		Cursor cursor = cr.query(uri.parse("content://com.android.calendar/events/"),projection,selection,null,null);
		
		while (cursor.moveToNext()) {
			String title = cursor.getString(1);
			long date = cursor.getLong(2);
			CalEvent event = new CalEvent(date, title);
			callist.add(event);
		}
		Log.d("LIST","Size: "+callist.size());
		return callist;
	}
	
	private class ImportListAdapter extends BaseAdapter {
		
		private List<CalEvent> calEventList;
		private LayoutInflater myInflater;
		
		public ImportListAdapter(Context c){
			calEventList = calendarList;
			myInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			int size = calendarList.size();
			checkedStatus = new ArrayList<Boolean>();
			for (int i = 0; i < size; i++){
				checkedStatus.add(false);
			}
		}
		
		@Override
		public int getCount() {
			return calEventList.size();
		}

		@Override
		public Object getItem(int position) {
			return calEventList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			final CalEvent event = calEventList.get(position);
			ViewHolder holder = null;
			convertView = myInflater.inflate(R.layout.import_list_item_layout, null);
			holder = new ViewHolder();
			holder.event = (CheckBox) convertView.findViewById(R.id.calendar_event_checkbox);
			holder.date = (TextView) convertView.findViewById(R.id.calendar_event_dateView);
			convertView.setTag(holder);
				
			holder.event.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked){
						checkedStatus.set(position, true);
					} else {
						checkedStatus.set(position, false);
					}
				}
			});
			
			SimpleDateFormat fromDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			SimpleDateFormat toDateFormat = new SimpleDateFormat("MMM d, yyyy  -  h:mm a");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ImportCal.this);
			boolean twentyFourHourClock = prefs.getBoolean("24hour", false);
			if (twentyFourHourClock){
				toDateFormat = new SimpleDateFormat("MMM d, yyyy - HH:mm");
			}
			
			Time time = new Time();
			time.set(event.getDate());
			int day = time.monthDay;
			int month = time.month;
			int year = time.year;
			int hour = time.hour;
			int minute = time.minute;
			String fromDate = day+"/"+(month+1)+"/"+year+" "+hour+":"+minute;
			String outputTimeString = "";
			try {
				outputTimeString = toDateFormat.format(fromDateFormat.parse(fromDate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			holder.date.setText(outputTimeString);
			holder.event.setChecked(checkedStatus.get(position));
			holder.event.setText(event.getName());
			return convertView;
		}
	}
	
	private class CalEvent{
		private long date;
		private String name;
		public String getName() {
			return name;
		}
		public long getDate(){
			return date;
		}
		public CalEvent(long date, String name) {
			super();
			this.date = date;
			this.name = name;
		}
	}
	
	static class ViewHolder{
		CheckBox event;
		TextView date;
	}
	
	public void showFromDatePickerDialog(View v) {
	    DialogFragment newFragment = new FromDatePickerFragment();
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	public static class FromDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return new DatePickerDialog(getActivity(), this, fromYear, fromMonth, fromDay);
		}
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			fromDay = dayOfMonth;
			fromMonth = monthOfYear;
			fromYear = year;
			TextView fromDateView = (TextView) getActivity().findViewById(R.id.fromDateView);
			fromDateView.setText(dateToString(fromDay,fromMonth,fromYear));
		}
	}
	
	public void showToDatePickerDialog(View v) {
	    DialogFragment newFragment = new ToDatePickerFragment();
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	public static class ToDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return new DatePickerDialog(getActivity(), this, toYear, toMonth, toDay);
		}
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			toDay = dayOfMonth;
			toMonth = monthOfYear;
			toYear = year;
			TextView toDateView = (TextView) getActivity().findViewById(R.id.toDateView);
			toDateView.setText(dateToString(toDay,toMonth,toYear));
		}
	}
	
	private static String dateToString(int day, int month, int year){
		SimpleDateFormat fromDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat toDateFormat = new SimpleDateFormat("MMM d, yyyy");
		String fromDate = day+"/"+(month+1)+"/"+year;
		String toDate = "";
		try {
			toDate = toDateFormat.format(fromDateFormat.parse(fromDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return toDate;
	}
	
	private void sortCalendarList(List<CalEvent> cl){
		Collections.sort(cl, new Comparator<CalEvent>(){
			public int compare(CalEvent event1, CalEvent event2){
				long event1ms = event1.getDate();
				long event2ms = event2.getDate();
				
				if (event1ms < event2ms) return -1;
				return 1;
			}
		});
	}
}