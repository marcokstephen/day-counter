package com.sm.daysuntilcards;

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
		if (convertView == null){
			convertView = myInflater.inflate(R.layout.card_front, null);
		}
		TextView cardTextView = (TextView)convertView.findViewById(R.id.cardTextView);
		cardTextView.setText("AAA");
		return convertView;
	}
}