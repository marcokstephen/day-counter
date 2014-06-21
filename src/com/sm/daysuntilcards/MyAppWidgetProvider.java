package com.sm.daysuntilcards;

import org.json.JSONException;
import org.json.JSONObject;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public class MyAppWidgetProvider extends AppWidgetProvider {
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
		final int N = appWidgetIds.length;
		
		for (int i = 0; i < N; i++){
			int appWidgetId = appWidgetIds[i];
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.big_widget);
			
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
			String eventString = sharedPrefs.getString(appWidgetId+"", "");
			JSONObject jsonEvent = new JSONObject();
			try{
				jsonEvent = new JSONObject(eventString);
			} catch (JSONException e){
				e.printStackTrace();
			}
			String daysRemaining = "0";
			try {
				daysRemaining = WidgetConfigure.getDaysRemaining(jsonEvent);
			} catch (JSONException e) {
				e.printStackTrace();
			} 
			views.setTextViewText(R.id.widgetDaysRemaining, daysRemaining);
			
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}
}
