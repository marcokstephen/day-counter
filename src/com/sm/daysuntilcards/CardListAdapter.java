package com.sm.daysuntilcards;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
		long differenceDays = 0;
		try {
			year = event.getInt("year");
			month = event.getInt("month");
			day = event.getInt("day");
			hour = event.getInt("hour");
			minute = event.getInt("minute");
			title = event.getString("name");
			Time now = new Time();
			now.setToNow();
			long currentDateInMs = now.toMillis(false);
			now.set(0,minute,hour,day,month,year);
			long eventDateInMs = now.toMillis(false);
			long difference = Math.abs(currentDateInMs-eventDateInMs);
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
			
		} catch (JSONException e) {
			e.printStackTrace();
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