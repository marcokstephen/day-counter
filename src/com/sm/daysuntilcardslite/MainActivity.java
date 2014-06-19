package com.sm.daysuntilcardslite;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sm.daysuntilcardslite.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends Activity implements ActionBar.TabListener {
	static SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	public static List<JSONObject> daysUntil;
	public static List<JSONObject> daysSince;
	public static CardListAdapter cla;
	public static CardListAdapter cla2;
	public static int APP_THEME = R.style.Theme_Example;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		int theme = Integer.parseInt(prefs.getString("actionBarTheme", "0"));
		if (theme == 0){
			APP_THEME = R.style.Theme_Example;
		} else if (theme == 1){
			APP_THEME = R.style.Theme_Red;
		} else if (theme == 2){
			APP_THEME = R.style.Theme_Orange;
		} else if (theme == 3){
			APP_THEME = R.style.Theme_Green;
		} else if (theme == 4){
			APP_THEME = R.style.Theme_Blue;
		} else if (theme == 5){
			APP_THEME = R.style.ThemePurple;
		}
		setTheme(APP_THEME);
		super.onCreate(savedInstanceState);
		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
	
		setContentView(R.layout.activity_main);
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		refreshListFragments(this);

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});
		
		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		refreshListFragments(this);
	}
	
	public void refreshListFragments(Context c){
		generateLists(this);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		final ActionBar actionBar = getActionBar();
		int page = actionBar.getSelectedNavigationIndex();
		mViewPager.setCurrentItem(page);
	}
	
	public static long eventToMs (JSONObject event){
		int year=0,month=0,day=0,hour=0,minute=0;
		try {
			year = event.getInt("year");
			month = event.getInt("month");
			day = event.getInt("day");
			hour = event.getInt("hour");
			minute = event.getInt("minute");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Time now = new Time();
		now.set(0,minute,hour,day,month,year);
		return now.toMillis(false);
	}
	
	@SuppressLint("SimpleDateFormat")
	public static boolean checkIfAfterCurrentDate(JSONObject event, Calendar c){
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int eventYear=0,eventMonth=0,eventDay=0,eventHour=0,eventMinute=0;
		
		try {
			eventYear = event.getInt("year");
			eventMonth = event.getInt("month");
			eventDay = event.getInt("day");
			eventHour = event.getInt("hour");
			eventMinute = event.getInt("minute");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date currentDate, eventDate;
		try {
			currentDate = sdf.parse(year+"-"+month+"-"+day+" "+hour+":"+minute);
			eventDate = sdf.parse(eventYear+"-"+eventMonth+"-"+eventDay+" "+eventHour+":"+eventMinute);
			
			if (eventDate.after(currentDate)){
				return true;
			} else if (eventDate.before(currentDate)){
				return false;
			} else if (hour > eventHour) {
				return true;
			} else if (hour < eventHour){
				return false;
			} else if (eventMinute > minute){
				return true;
			} else return false;
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		} else if (id == R.id.create_date) {
			Intent intent = new Intent(this, CreateEvent.class);
			startActivity(intent);
		} else if (id == R.id.clear_all){
			promptToDeleteAll();
		} else if (id == R.id.help){
			Intent intent = new Intent(this, HelpMenu.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}
	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0){
				return new UntilFragment();
			} else {
				return new SinceFragment();
			}
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}

	public static class UntilFragment extends ListFragment {
		static final int FRAGMENT_GROUPID = 1;
		static final int MENU_EDIT = 2;
		static final int MENU_REMOVE = 3;
		
		public UntilFragment() {
		}
		@Override
		public void onActivityCreated(Bundle savedInstanceState){
			super.onActivityCreated(savedInstanceState);
			getListView().setDivider(null);
			getListView().setDividerHeight(0);
			registerForContextMenu(getListView());
			cla = new CardListAdapter(getActivity(), daysUntil);
			setListAdapter(cla);
			getListView().setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent = new Intent(getActivity(), DialogActivity.class);
					intent.putExtra("com.sm.daysuntilcardslite.JSONSTRING", daysUntil.get(position).toString());
					startActivity(intent);
				}
			});
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			super.onCreateView(inflater, container, savedInstanceState);
			View view = inflater.inflate(R.layout.list_fragment_events, container, false);
			AdView mAdView;
	        mAdView = (AdView) view.findViewById(R.id.adView);
	        mAdView.loadAd(new AdRequest.Builder().build());
			return view;
		}
		
	   @Override
	    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
	        super.onCreateContextMenu(menu, v, menuInfo);
	        menu.add(FRAGMENT_GROUPID, MENU_EDIT, Menu.NONE, "Edit");
	        menu.add(FRAGMENT_GROUPID, MENU_REMOVE, Menu.NONE, "Remove");
	    }
	   
	   @Override
	   public boolean onContextItemSelected(MenuItem item){
		   if (item.getGroupId() == FRAGMENT_GROUPID){
			   AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			   int position = info.position;
				switch (item.getItemId()){
				case MENU_EDIT:
					Intent intent = new Intent(getActivity(), EditEvent.class);
					intent.putExtra("com.sm.daysuntilcardslite.EVENT", daysUntil.get(position).toString());
					Log.d("OUTPUTINFO",daysUntil.get(position).toString());
					intent.putExtra("com.sm.daysuntilcardslite.POSITION", position);
					intent.putExtra("com.sm.daysuntilcardslite.UNTIL", true);
					startActivity(intent);
					return false;
				case MENU_REMOVE:
					String name = "temp";
						try {
						name = daysUntil.get(position).getString("name");
					} catch (JSONException e) {
						e.printStackTrace();
					}
	        	   getActivity().deleteFile(name);
	        	   daysUntil.remove(position);
	        	   cla.notifyDataSetChanged();
	        	   return false;
				default:
					return super.onContextItemSelected(item);
				}
		   }
		   return false;
	   }
	}
	
	public static class SinceFragment extends ListFragment {
		static final int FRAGMENT_GROUPID = 4;
		static final int MENU_EDIT = 5;
		static final int MENU_REMOVE = 6;
		
		public SinceFragment() {
		}
		@Override
		public void onActivityCreated(Bundle savedInstanceState){
			super.onActivityCreated(savedInstanceState);
			getListView().setDivider(null);
			getListView().setDividerHeight(0);
			registerForContextMenu(getListView());
			cla2 = new CardListAdapter(getActivity(), daysSince);
			setListAdapter(cla2);
			getListView().setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent = new Intent(getActivity(), DialogActivity.class);
					intent.putExtra("com.sm.daysuntilcardslite.JSONSTRING", daysSince.get(position).toString());
					startActivity(intent);
				}
			});
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			super.onCreateView(inflater, container, savedInstanceState);
			View view = inflater.inflate(R.layout.list_fragment_events, container, false);
			AdView mAdView;
	        mAdView = (AdView) view.findViewById(R.id.adView);
	        mAdView.loadAd(new AdRequest.Builder().build());
			return view;
		}
		
		@Override
	    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
	        super.onCreateContextMenu(menu, v, menuInfo);
	        menu.add(FRAGMENT_GROUPID, MENU_EDIT, Menu.NONE, "Edit");
	        menu.add(FRAGMENT_GROUPID, MENU_REMOVE, Menu.NONE, "Remove");
	    }
	   
	   @Override
	   public boolean onContextItemSelected(MenuItem item){
		   if (item.getGroupId() == FRAGMENT_GROUPID){
			   AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			   int position = info.position;
			   switch (item.getItemId()){
			   case MENU_EDIT:
				   Intent intent = new Intent(getActivity(), EditEvent.class);
				   intent.putExtra("com.sm.daysuntilcardslite.EVENT", daysSince.get(position).toString());
				   intent.putExtra("com.sm.daysuntilcardslite.POSITION", position);
				   intent.putExtra("com.sm.daysuntilcardslite.UNTIL", false);
				   startActivity(intent);
				   return false;
			   case MENU_REMOVE:
				   String name = "temp";
				   try {
					   name = daysSince.get(position).getString("name");
				   } catch (JSONException e) {
					   e.printStackTrace();
				   }
	        	   getActivity().deleteFile(name);
	        	   daysSince.remove(position);
	        	   cla2.notifyDataSetChanged();
	        	   return false;
			   default:
				   return super.onContextItemSelected(item);
			   }
		   }
		   return false;
	   }
   }
	
	public void promptToDeleteAll(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
		alertDialogBuilder.setTitle("Delete All Events");
		alertDialogBuilder.setMessage("Are you sure that you want to delete all events?")
		.setCancelable(true)
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
            	   String[] files = fileList();
            	   for (int i = 0; i < files.length; i++){
            		   deleteFile(files[i]);
            	   }
              		daysUntil = new ArrayList<JSONObject>();
            		daysSince = new ArrayList<JSONObject>();

            		//attempts to refresh fragments
            		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
            		mViewPager = (ViewPager) findViewById(R.id.pager);
            		mViewPager.setAdapter(mSectionsPagerAdapter);
               }
           })
           .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
            	   dialog.dismiss();
               }
           });
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	public static JSONObject addDaysToObj(JSONObject event){
		//only runs if repeat is set to 1 (daily), 2 (weekly), 3 (monthly), or 4 (yearly)
		
		JSONObject dateJsonObj = event;
		int year=0,month=0,day=0;
		int repeat=0,repeatRate=0;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Calendar c = Calendar.getInstance();
		Calendar currentDate = Calendar.getInstance();
		try {
			year = dateJsonObj.getInt("year");
			month = dateJsonObj.getInt("month");
			day = dateJsonObj.getInt("day");
			repeat = dateJsonObj.getInt("repeat");
			repeatRate = dateJsonObj.getInt("repeatRate");
			c.setTime(sdf.parse(day+"-"+(month+1)+"-"+year));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if (repeat == 1){ //daily
			do{
				c.add(Calendar.DATE, repeatRate);
			} while (c.before(currentDate));
		} else if (repeat == 2) { //weekly
			do{
				c.add(Calendar.DATE, repeatRate*7);
			} while (c.before(currentDate));
		} else if (repeat == 3) { //monthly
			do{
				c.add(Calendar.MONTH, repeatRate);
			} while (c.before(currentDate));
		} else if (repeat == 4) { //yearly
			do {
				c.add(Calendar.YEAR, repeatRate);
			} while (c.before(currentDate));
		}
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DATE);
		try {
			dateJsonObj.put("year", year);
			dateJsonObj.put("month", month);
			dateJsonObj.put("day", day);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dateJsonObj;
	}
	
	public static void sortList(List<JSONObject> eventList, final boolean daysUntil){
		//if list is daysuntil, value is true, if list is dayssince, value is false
		Collections.sort(eventList, new Comparator<JSONObject>() {
			public int compare(JSONObject event1, JSONObject event2){
				long event1ms = eventToMs(event1);
				long event2ms = eventToMs(event2);
				if (daysUntil){
					if (event1ms < event2ms) return -1;
				} else {
					if (event1ms > event2ms) return -1;
				}
				return 1;
			}
		});
	}
	
	public static void generateLists(Context context){
		daysUntil = new ArrayList<JSONObject>();
		daysSince = new ArrayList<JSONObject>();
		Calendar c = Calendar.getInstance();
		
		FileInputStream fis;
		try {
			String[] file = context.fileList();
			for (int i = 0; i < file.length; i++){
				String listFile = file[i];
				String value = "";
				fis = context.openFileInput(listFile);
				byte[] input = new byte[fis.available()];
				while(fis.read(input) != -1){
					value += new String(input);
				}
				fis.close();
				try {
					JSONObject dateJsonObj = new JSONObject(value);
					boolean since=false, notify=false;
					String name = "";
					int repeat = 0;
					try {
						name = dateJsonObj.getString("name");
						since = dateJsonObj.getBoolean("since");
						repeat = dateJsonObj.getInt("repeat");
						notify = dateJsonObj.getBoolean("notify");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					boolean isAfterCurrentDate = checkIfAfterCurrentDate(dateJsonObj, c);
					if (isAfterCurrentDate){
						daysUntil.add(dateJsonObj);
					} else if (since) {
						daysSince.add(dateJsonObj);
					} else if (repeat != 0){
						dateJsonObj = addDaysToObj(dateJsonObj);
						FileOutputStream fos = context.openFileOutput(name, Context.MODE_PRIVATE);
						fos.write(dateJsonObj.toString().getBytes());
						fos.close();
						daysUntil.add(dateJsonObj); //adding to daysUntil because it is now an upcoming date
						if (notify) refreshNotification(context, dateJsonObj);
					} else {
						context.deleteFile(name);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		sortList(daysUntil, true);
		sortList(daysSince, false);
	}
	
	public static void refreshNotification(Context context, JSONObject dateJsonObj) throws JSONException{
		int minute = dateJsonObj.getInt("minute");
		int hour = dateJsonObj.getInt("hour");
		int day = dateJsonObj.getInt("day");
		int month = dateJsonObj.getInt("month");
		int year = dateJsonObj.getInt("year");
		int alarmID = dateJsonObj.getInt("alarmid");
		String name = dateJsonObj.getString("name");
		name.replaceAll("PARSE", "/");
		Intent i = new Intent(context, CardsService.class);
		i.putExtra("com.sm.daysuntilcardslite.EVENTNAME", name);
		i.putExtra("com.sm.daysuntilcardslite.ALARMID", alarmID);
        Time now = new Time();
        now.set(0, minute, hour, day, month, year);
        
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(context, alarmID, i, 0);
        mgr.set(AlarmManager.RTC, now.toMillis(false), pi);
	}
}