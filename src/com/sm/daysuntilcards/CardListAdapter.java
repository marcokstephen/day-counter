package com.sm.daysuntilcards;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CardListAdapter extends BaseAdapter{
	
	private List<JSONObject> eventJsonList;
	private LayoutInflater myInflater;
	
	public CardListAdapter(Context c, List<JSONObject> eventList){
		eventJsonList = eventList;
		myInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		boolean weekends = false;
		long differenceDays = 0; long difference = 0;
		try {
			year = event.getInt("year");
			month = event.getInt("month");
			day = event.getInt("day");
			hour = event.getInt("hour");
			minute = event.getInt("minute");
			title = event.getString("name");
			weekends = event.getBoolean("weekends");
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
		} else { //do not count weekends
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
				boolean makeLastSubtraction = true;
				while (currentDate.before(eventDate)){
					difference += 432000000; //millisPerWorkWeek
					currentDate.add(Calendar.DATE, 7);
					if ((currentDate.get(Calendar.WEEK_OF_YEAR) == eventDate.get(Calendar.WEEK_OF_YEAR)) &&
							(currentDate.get(Calendar.YEAR) == eventDate.get(Calendar.YEAR))){
						difference += Math.abs(eventDate.getTimeInMillis()-currentDate.getTimeInMillis());
						makeLastSubtraction = false;
						break;
					}
				}
				if (makeLastSubtraction) difference -= Math.abs(currentDate.getTimeInMillis()-eventDate.getTimeInMillis());
			}
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
		
		if (convertView == null){
			convertView = myInflater.inflate(R.layout.card_front, null);
		}
		TextView cardTextView = (TextView)convertView.findViewById(R.id.eventNameView);
		cardTextView.setText(title);
		TextView daysView = (TextView)convertView.findViewById(R.id.daysView);
		daysView.setText(timeRemaining);
		return convertView;
	}
}