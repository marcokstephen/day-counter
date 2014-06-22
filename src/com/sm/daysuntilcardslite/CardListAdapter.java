package com.sm.daysuntilcardslite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.sm.daysuntilcardslite.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class CardListAdapter extends BaseAdapter{
	
	private List<JSONObject> eventJsonList;
	private LayoutInflater myInflater;
	private Context context;
	
	public CardListAdapter(Context c, List<JSONObject> eventList){
		eventJsonList = eventList;
		myInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		context = c;
	}

	@Override
	public int getCount() {
		return eventJsonList.size();
	}

	@Override
	public Object getItem(int position) {
		return eventJsonList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		JSONObject event = (JSONObject) getItem(position);
		int year=0,month=0,day=0,hour=0,minute=0;
		String title = "placeholder";
		String timeRemaining = "";
		boolean weekends = false, notify = false;
		long differenceDays = 0; long difference = 0;
		try {
			year = event.getInt("year");
			month = event.getInt("month");
			day = event.getInt("day");
			hour = event.getInt("hour");
			minute = event.getInt("minute");
			title = event.getString("name");
			title = title.replaceAll("PARSE", "/");
			weekends = event.getBoolean("weekends");
			notify = event.getBoolean("notify");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Time now = new Time();
		now.setToNow();
		long currentDateInMs = now.toMillis(false);
		now.set(0,minute,hour,day,month,year); //setting "now" to the event date
		if (!weekends){ //count weekends
			long eventDateInMs = now.toMillis(false);
			difference = Math.abs(currentDateInMs-eventDateInMs);
		} else {
			difference = getNoWeekendsDifference(year,month,day,hour,minute);
		}
		differenceDays = difference/(1000*60*60*24);
		
		if (differenceDays == 1){
			timeRemaining = differenceDays + " day";
		} else {
			timeRemaining = differenceDays + " days";
		}
		
		//if differenceDays == 0, timescale is down to hours
		if (differenceDays == 0){
			differenceDays = difference/(1000*60*60);
			if (differenceDays == 1){
				timeRemaining = differenceDays + " hour";
			} else {
				timeRemaining = differenceDays + " hours";
			}
		}
		
		//if differenceDays == 0, timescale is down to minutes
		if (differenceDays == 0){
			differenceDays = difference/(1000*60);
			if (differenceDays == 1){
				timeRemaining = differenceDays + " minute";
			} else {
				timeRemaining = differenceDays+ " minutes";
			}
		}
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Boolean showWeekendNotice = prefs.getBoolean("showWeekendNotice", true);
		String calendarTheme = prefs.getString("calendarIconTheme", "1");
		
		ViewHolder holder = null;
		if (convertView == null){
			convertView = myInflater.inflate(R.layout.card_front, null);
			holder = new ViewHolder();
			holder.title= (TextView) convertView.findViewById(R.id.eventNameView);
			holder.count = (TextView) convertView.findViewById(R.id.daysView);
			holder.weekend = (TextView) convertView.findViewById(R.id.weekendNoticeView);
			holder.calMonthView = (TextView) convertView.findViewById(R.id.calMonthView);
			holder.calDayView = (TextView) convertView.findViewById(R.id.calDayView);
			holder.alarmIcon = (ImageView) convertView.findViewById(R.id.notificationIconView);
			holder.calendarIcon = (FrameLayout) convertView.findViewById(R.id.calendarFrameView);
			if (calendarTheme.equals("1")){
				holder.calendarIcon.setBackgroundResource(R.drawable.calendar_image);
			} else if (calendarTheme.equals("2")){
				holder.calendarIcon.setBackgroundResource(R.drawable.calendar_image_orange);
				holder.calDayView.setTextColor(Color.parseColor("#fdae46"));
			} else if (calendarTheme.equals("3")){
				holder.calendarIcon.setBackgroundResource(R.drawable.calendar_image_green);
				holder.calDayView.setTextColor(Color.parseColor("#5bc865"));
			} else if (calendarTheme.equals("4")){
				holder.calendarIcon.setBackgroundResource(R.drawable.calendar_image_blue);
				holder.calDayView.setTextColor(Color.parseColor("#61abff"));
			} else if (calendarTheme.equals("5")){
				holder.calendarIcon.setBackgroundResource(R.drawable.calendar_image_purple);
				holder.calDayView.setTextColor(Color.parseColor("#9c5faf"));
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		SimpleDateFormat sdf1 = new SimpleDateFormat("MM");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MMM");
		Date temptime = new Date();
		String outputMonthString = "";
		try {
			temptime = sdf1.parse(""+(month+1));
			outputMonthString = sdf2.format(temptime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		holder.calMonthView.setText(outputMonthString);
		holder.calDayView.setText(""+day);
		holder.calMonthView.bringToFront();
		holder.calDayView.bringToFront();
		if (day < 10){
			holder.calDayView.setText(" "+day);
		}
		holder.title.setText(title);
		holder.count.setText(timeRemaining);
		if (weekends && showWeekendNotice){
			holder.weekend.setVisibility(View.VISIBLE);
		} else {
			holder.weekend.setVisibility(View.GONE);
		}
		if (notify) {
			holder.alarmIcon.setVisibility(View.VISIBLE);
		} else {
			holder.alarmIcon.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	public static long getNoWeekendsDifference(int year,int month,int day,int hour,int minute){
		long difference = 0;
		Time now = new Time();
		now.setToNow();
		long currentDateInMs = now.toMillis(false);
		now.set(0,minute,hour,day,month,year); //setting "now" to the event date
		if (currentDateInMs < now.toMillis(false)) { //do not count weekends, only for daysUntil
			//only works for days until so far
			difference = 0;
			Calendar currentDate = Calendar.getInstance();
			Calendar eventDate = Calendar.getInstance();
			eventDate.set(year, month, day, hour, minute, 00);

			if (currentDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
				currentDate.add(Calendar.DATE, 1);
				currentDate.set(currentDate.get(Calendar.YEAR),currentDate.get(Calendar.MONTH),currentDate.get(Calendar.DATE),0,0,0);
			} else if (currentDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
				currentDate.add(Calendar.DATE, 2);
				currentDate.set(currentDate.get(Calendar.YEAR),currentDate.get(Calendar.MONTH),currentDate.get(Calendar.DATE),0,0,0);
			}

			if (eventDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
				eventDate.add(Calendar.DATE, -2);
				eventDate.set(eventDate.get(Calendar.YEAR),eventDate.get(Calendar.MONTH),eventDate.get(Calendar.DATE),23,59,59);
			} else if (eventDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
				eventDate.add(Calendar.DATE, -1);
				eventDate.set(eventDate.get(Calendar.YEAR),eventDate.get(Calendar.MONTH),eventDate.get(Calendar.DATE),23,59,59);
			}
			
			if ((currentDate.get(Calendar.WEEK_OF_YEAR) == eventDate.get(Calendar.WEEK_OF_YEAR)) &&
					(currentDate.get(Calendar.YEAR) == eventDate.get(Calendar.YEAR))){
				difference = Math.abs(eventDate.getTimeInMillis()-currentDate.getTimeInMillis());
			} else {
				while (currentDate.before(eventDate)){
					difference += 432000000; //millisPerWorkWeek
					currentDate.add(Calendar.DATE, 7);
					if ((currentDate.get(Calendar.WEEK_OF_YEAR) == eventDate.get(Calendar.WEEK_OF_YEAR)) &&
							(currentDate.get(Calendar.YEAR) == eventDate.get(Calendar.YEAR))){
						difference += eventDate.getTimeInMillis()-currentDate.getTimeInMillis();
						break;
					}
				}
			}
		} else { //do not count weekends, days since
			difference = 0;
			Calendar currentDate = Calendar.getInstance();
			Calendar eventDate = Calendar.getInstance();
			eventDate.set(year, month, day, hour, minute, 00);
			if (currentDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
				currentDate.add(Calendar.DATE, -2);
				currentDate.set(currentDate.get(Calendar.YEAR),currentDate.get(Calendar.MONTH),currentDate.get(Calendar.DATE),0,0,0);
			} else if (currentDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
				currentDate.add(Calendar.DATE, -1);
				currentDate.set(currentDate.get(Calendar.YEAR),currentDate.get(Calendar.MONTH),currentDate.get(Calendar.DATE),0,0,0);
			}
			
			if (eventDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
				eventDate.add(Calendar.DATE, 1);
				eventDate.set(eventDate.get(Calendar.YEAR),eventDate.get(Calendar.MONTH),eventDate.get(Calendar.DATE),23,59,59);
			} else if (eventDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
				eventDate.add(Calendar.DATE, 2);
				eventDate.set(eventDate.get(Calendar.YEAR),eventDate.get(Calendar.MONTH),eventDate.get(Calendar.DATE),23,59,59);
			}
			
			if ((currentDate.get(Calendar.WEEK_OF_YEAR) == eventDate.get(Calendar.WEEK_OF_YEAR)) &&
					(currentDate.get(Calendar.YEAR) == eventDate.get(Calendar.YEAR))){
				difference = Math.abs(eventDate.getTimeInMillis()-currentDate.getTimeInMillis());
			} else {
				while (currentDate.after(eventDate)){
					difference += 432000000; //millisPerWorkWeek
					currentDate.add(Calendar.DATE, -7);
					if ((currentDate.get(Calendar.WEEK_OF_YEAR) == eventDate.get(Calendar.WEEK_OF_YEAR)) &&
							(currentDate.get(Calendar.YEAR) == eventDate.get(Calendar.YEAR))){
						difference -= eventDate.getTimeInMillis()-currentDate.getTimeInMillis();
						break;
					}
				}
			}
		}
		return difference;
	}
	
	static class ViewHolder {
		//used to prevent bug from occurring when android os reuses the listview card views
	  TextView title;
	  TextView count;
	  TextView weekend;
	  int position;
	  TextView calMonthView;
	  TextView calDayView;
	  ImageView alarmIcon;
	  FrameLayout calendarIcon;
	}
}