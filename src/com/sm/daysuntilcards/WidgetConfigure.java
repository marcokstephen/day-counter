package com.sm.daysuntilcards;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;

public class WidgetConfigure extends ListActivity {
	List<JSONObject> eventList = new ArrayList<JSONObject>();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String files[] = fileList();
		int fileListSize = files.length;
		String titles[] = new String[fileListSize];
		for (int i = 0; i < fileListSize; i++){
	   		String singleFile = files[i];
			String value = "";
			try {
				FileInputStream fis = openFileInput(singleFile);
				byte[] input = new byte[fis.available()];
				while(fis.read(input) != -1){
					value += new String(input);
				}
				fis.close();
			} catch (IOException e){
				e.printStackTrace();
			}
			String title = "";
			try {
				JSONObject newJsonObject = new JSONObject(value);
				eventList.add(newJsonObject);
				title = newJsonObject.getString("name");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			title = title.replaceAll("PARSE","/");
			titles[i] = title;
		}
		setContentView(R.layout.configure_widget);
		setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, titles));
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		JSONObject jsonEvent = eventList.get(position);
		String title = "";
		String timeRemaining = "";
		try {
			timeRemaining = getDaysRemaining(jsonEvent);
			title = jsonEvent.getString("name");
			title = title.replaceAll("PARSE","/");
		} catch (JSONException e){
			e.printStackTrace();
		}
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
		if (extras != null){
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.big_widget);
		views.setTextViewText(R.id.widgetEventTitle, title);
		views.setTextViewText(R.id.widgetDaysRemaining, timeRemaining);
		
		appWidgetManager.updateAppWidget(mAppWidgetId, views);
		
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		
		//writing the json string to shared preferences so it can be used onUpdate
		SharedPreferences sharedPref = getSharedPreferences("widgetData", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(mAppWidgetId+"", jsonEvent.toString());
		editor.commit();
		
		setResult(RESULT_OK, resultValue);
		finish();
	}

	public static String getDaysRemaining(JSONObject jsonEvent) throws JSONException{
		Boolean weekends = false;
		weekends = jsonEvent.getBoolean("weekends");
		int year = jsonEvent.getInt("year");
		int month = jsonEvent.getInt("month");
		int day = jsonEvent.getInt("day");
		int hour = jsonEvent.getInt("hour");
		int minute = jsonEvent.getInt("minute");
		long timeDifference = 0;
		if (weekends){
			timeDifference = CardListAdapter.getNoWeekendsDifference(year, month, day, hour, minute); 
		} else {
			Time now = new Time();
			now.setToNow();
			long currentDateInMs = now.toMillis(false);
			now.set(0,minute,hour,day,month,year); //setting "now" to the event date
			long eventDateInMs = now.toMillis(false);
			timeDifference = Math.abs(currentDateInMs-eventDateInMs);
		}
		
		long differenceDays = timeDifference/(1000*60*60*24);
		
		return differenceDays+"";
	}
}
