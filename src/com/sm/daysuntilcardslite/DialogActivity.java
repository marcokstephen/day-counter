package com.sm.daysuntilcardslite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.sm.daysuntilcardslite.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class DialogActivity extends Activity {
	
	static SimpleDateFormat fromDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	static SimpleDateFormat toDateFormat = new SimpleDateFormat("MMM d, yyyy");
	static SimpleDateFormat fromTimeFormat = new SimpleDateFormat("HH:mm");
	static SimpleDateFormat toTimeFormat = new SimpleDateFormat("h:mm a");
	static Date eventDate = new Date();
	static boolean twentyFourHourClock = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //must be between super/setContentView
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		twentyFourHourClock = prefs.getBoolean("24hour",false);
		Intent intent = getIntent();
		final String eventString = intent.getStringExtra("com.sm.daysuntilcardslite.JSONSTRING");
		
		JSONObject jsonEvent = new JSONObject();
		int minute=0,hour=0,day=0,month=0,year=0;
		String name = "";
		boolean weekends = false;
		try {
			jsonEvent = new JSONObject(eventString);
			year = jsonEvent.getInt("year");
			month = jsonEvent.getInt("month");
			day = jsonEvent.getInt("day");
			hour = jsonEvent.getInt("hour");
			minute = jsonEvent.getInt("minute");
			name = jsonEvent.getString("name");
			name = name.replaceAll("PARSE","/");
			weekends = jsonEvent.getBoolean("weekends");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		final int finalmin=minute;
		final int finalhour=hour;
		final int finalday=day;
		final int finalmonth=month;
		final int finalyear=year;
		final boolean finalweekends=weekends;
		
		String fromTimeString = String.format("%02d",hour)+":"+String.format("%02d",minute);
		String outputTimeString = fromTimeString;
		String dateString = day+"/"+(month+1)+"/"+year;
		String outputDateString = "";
		Date eventTime = new Date();
		if (!twentyFourHourClock){
			try {
				eventTime = fromTimeFormat.parse(outputTimeString);
				outputTimeString = toTimeFormat.format(eventTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		try {
			eventDate = fromDateFormat.parse(dateString);
			outputDateString = toDateFormat.format(eventDate);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		setContentView(R.layout.activity_dialog);
		
		TextView dialogNameView = (TextView) findViewById(R.id.dialogNameView);
		TextView dialogDateView = (TextView) findViewById(R.id.dialogDateView);
		TextView dialogTimeView = (TextView) findViewById(R.id.dialogTimeView);
		dialogNameView.setText(name);
		dialogDateView.setText(outputDateString);
		dialogTimeView.setText(outputTimeString);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fullEventTimeString = year+"-"+(month+1)+"-"+day+" "+hour+":"+minute+":00";
		@SuppressWarnings("unused")
		Date fullEventDate = new Date();
		try {
			fullEventDate = sdf.parse(fullEventTimeString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int screenWidthPercent = (int) (metrics.widthPixels * 0.90);
		int screenHeightPercent = (int) (metrics.heightPixels * 0.80);
		getWindow().setLayout(screenWidthPercent, screenHeightPercent);

		updateTime(minute,hour,day,month,year,weekends);
		
		UIUpdater mUIUpdater = new UIUpdater(new Runnable() {
	         @Override 
	         public void run() {
	        	 updateTime(finalmin,finalhour,finalday,finalmonth,finalyear,finalweekends);
	         }
	    });
		mUIUpdater.startUpdates();
	}
	
	public void updateTime(int minute,int hour,int day,int month,int year,boolean weekends){
		long difference = 0;
		if (!weekends){
			Time now = new Time();
			now.setToNow();
			long currentDateInMillis = now.toMillis(false);
			now.set(0,minute,hour,day,month,year);
			long eventDateInMillis = now.toMillis(false);
			difference = Math.abs(eventDateInMillis - currentDateInMillis);
		} else {
			difference = CardListAdapter.getNoWeekendsDifference(year, month, day, hour, minute);
			TextView excludesWeekendsDialogView = (TextView) findViewById(R.id.excludesWeekendsDialogView);
			excludesWeekendsDialogView.setVisibility(View.VISIBLE);
		}
			long diffDays = difference/(1000*60*60*24);
			long daysRemainder = difference%(1000*60*60*24);
			long diffHours = daysRemainder/(1000*60*60);
			long hoursRemainder = daysRemainder%(1000*60*60);
			long diffMinutes = hoursRemainder/(1000*60);
			long minutesRemainder = hoursRemainder%(1000*60);
			long diffSeconds = minutesRemainder/1000;
			TextView dialogDaysView = (TextView) findViewById(R.id.dialogDaysView);
			TextView dialogHoursView = (TextView) findViewById(R.id.dialogHoursView);
			TextView dialogMinutesView = (TextView) findViewById(R.id.dialogMinutesView);
			TextView dialogSecondsView = (TextView) findViewById(R.id.dialogSecondsView);
			String days = " days";
			String hours = " hours";
			String minutes = " minutes";
			String seconds = " seconds";
			if (diffDays == 1) days = " day";
			if (diffHours == 1) hours = " hour";
			if (diffMinutes == 1) minutes = " minute";
			if (diffSeconds == 1) seconds = " second";
			dialogDaysView.setText(diffDays+days);
			dialogHoursView.setText(diffHours+hours);
			dialogMinutesView.setText(diffMinutes+minutes);
			dialogSecondsView.setText(diffSeconds+seconds);
	}
	
	public class UIUpdater{
		private Handler mHandler = new Handler(Looper.getMainLooper());
		private Runnable mStatusChecker;
		private int UPDATE_INTERVAL = 1000;
		
        public UIUpdater(final Runnable uiUpdater) {
            mStatusChecker = new Runnable() {
                @Override
                public void run() {
                    // Run the passed runnable
                    uiUpdater.run();
                    // Re-run it after the update interval
                    mHandler.postDelayed(this, UPDATE_INTERVAL);
                }
            };
        }
        
        public UIUpdater(Runnable uiUpdater, int interval){
            this(uiUpdater);
            UPDATE_INTERVAL = interval;
        }

        public synchronized void startUpdates(){
            mStatusChecker.run();
        }

        public synchronized void stopUpdates(){
            mHandler.removeCallbacks(mStatusChecker);
        }
	}
}
