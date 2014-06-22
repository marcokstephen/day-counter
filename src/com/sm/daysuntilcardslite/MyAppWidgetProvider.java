package com.sm.daysuntilcardslite;

import org.json.JSONException;
import org.json.JSONObject;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class MyAppWidgetProvider extends AppWidgetProvider {

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
		final int N = appWidgetIds.length;

		for (int i = 0; i < N; i++){
			int appWidgetId = appWidgetIds[i];
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.big_widget);

			SharedPreferences sharedPrefs = context.getSharedPreferences("widgetData", 0);
			String eventString = sharedPrefs.getString(appWidgetId+"", "nothingfound");
			JSONObject jsonEvent = new JSONObject();
			String title = "";
			try{
				jsonEvent = new JSONObject(eventString);
				title = jsonEvent.getString("name");
				title = title.replaceAll("PARSE","/");
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
			views.setTextViewText(R.id.widgetEventTitle, title);

			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}
}