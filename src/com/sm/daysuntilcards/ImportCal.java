package com.sm.daysuntilcards;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class ImportCal extends Activity {
	private List<Boolean> checkedStatus;
	private List<CalEvent> calendarList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		setTheme(MainActivity.APP_THEME);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.import_cal_layout);
		ListView importList = (ListView) findViewById(R.id.import_listview);
		calendarList = getCalendarEvents();
		Collections.sort(calendarList, new Comparator<CalEvent>(){
			public int compare(CalEvent event1, CalEvent event2){
				long event1ms = event1.getDate();
				long event2ms = event2.getDate();
				
				if (event1ms < event2ms) return -1;
				return 1;
			}
		});

		final ImportListAdapter ila = new ImportListAdapter(this);
		importList.setAdapter(ila);
		
		Button cancelButton = (Button) findViewById(R.id.cancel_import_button);
		Button acceptButton = (Button) findViewById(R.id.import_button);
		
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
	private List<CalEvent> getCalendarEvents(){
		List<CalEvent> callist = new ArrayList<CalEvent>();
		String[] projection = new String[] { "calendar_id","title","dtstart" };
		ContentResolver cr = getContentResolver();
		Uri uri = Calendars.CONTENT_URI;

		Calendar c_start = Calendar.getInstance();
		Calendar c_end = Calendar.getInstance();
		c_end.add(Calendar.MONTH, 1); //shows list for events for next 5 years
		String selection = "((dtstart >= "+c_start.getTimeInMillis()+") AND (dtend <= "+c_end.getTimeInMillis()+"))";
		
		Cursor cursor = cr.query(uri.parse("content://com.android.calendar/events/"),projection,selection,null,null);
		
		while (cursor.moveToNext()) {
			String title = cursor.getString(1);
			long date = cursor.getLong(2);
			CalEvent event = new CalEvent(date, title);
			callist.add(event);
		}
		
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
}