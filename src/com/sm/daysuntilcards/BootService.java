package com.sm.daysuntilcards;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;

public class BootService extends Service {

    @Override
    public IBinder onBind(final Intent intent) {
            return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
            super.onStartCommand(intent, flags, startId);

            try {
				LoadAlarmsFromDatabase();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
            stopSelf();
            return 0;
    }

    private void LoadAlarmsFromDatabase() throws JSONException, IOException {
    	FileInputStream fis;
    	String[] eventFileList = fileList();
    	for (int i = 0; i < eventFileList.length; i++){
    		String singleFile = eventFileList[i];
			String value = "";
			fis = openFileInput(singleFile);
			byte[] input = new byte[fis.available()];
			while(fis.read(input) != -1){
				value += new String(input);
			}
			fis.close();
    		JSONObject jsonEvent = new JSONObject(value);
    		
    		Calendar c = Calendar.getInstance();
    		boolean isAfterCurrentDate = MainActivity.checkIfAfterCurrentDate(jsonEvent, c);
    		
    		if (isAfterCurrentDate && jsonEvent.getBoolean("notify")) addAlarm(jsonEvent);
    	}
    }
    
    private void addAlarm(JSONObject jsonEvent) throws JSONException{
    	String name = jsonEvent.getString("name");
    	name.replaceAll("PARSE", "/");
    	int year = jsonEvent.getInt("year");
    	int month = jsonEvent.getInt("month");
    	int day = jsonEvent.getInt("day");
    	int hour = jsonEvent.getInt("hour");
    	int minute = jsonEvent.getInt("minute");
    	int alarmID = jsonEvent.getInt("alarmid");
    	
		Intent i = new Intent(this, CardsService.class);
		i.putExtra("com.sm.daysuntilcards.EVENTNAME", name);
		i.putExtra("com.sm.daysuntilcards.ALARMID", alarmID);
        Time now = new Time();
        now.set(0, minute, hour, day, month, year);
        
        AlarmManager mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(this, alarmID, i, 0);
        mgr.set(AlarmManager.RTC, now.toMillis(false), pi);
        Log.d("service","recreating alarm for "+name);
    }
}