package com.sm.daysuntilcardslite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.sm.daysuntilcardslite.ImportCal.ViewHolder;

@SuppressLint("SimpleDateFormat")
class ImportListAdapter extends BaseAdapter {

	private List<CalEvent> calEventList;
	private LayoutInflater myInflater;
	private List<Boolean> checkedStatus;
	private Context ctxt;

	public ImportListAdapter(Context c){
		calEventList = ImportCal.calendarList;
		myInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		checkedStatus = ImportCal.checkedStatus;
		ctxt = c;
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
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctxt);
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