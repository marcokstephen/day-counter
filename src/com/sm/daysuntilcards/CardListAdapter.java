package com.sm.daysuntilcards;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CardListAdapter extends BaseAdapter{
	
	private Context context;
	private List<JSONObject> eventJsonList;
	private LayoutInflater myInflater;
	
	public CardListAdapter(Context c, List<JSONObject> eventList){
		context = c;
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
		try {
			year = event.getInt("year");
			month = event.getInt("month");
			day = event.getInt("day");
			hour = event.getInt("hour");
			minute = event.getInt("minute");
			title = event.getString("name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (convertView == null){
			convertView = myInflater.inflate(R.layout.card_front, null);
		}
		TextView cardTextView = (TextView)convertView.findViewById(R.id.cardTextView);
		cardTextView.setText(title);
		return convertView;
	}
}