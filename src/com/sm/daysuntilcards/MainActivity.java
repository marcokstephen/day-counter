/*Things to do:
 * Fix layout of card
 * Refresh list automatically
 * 
 */

package com.sm.daysuntilcards;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;

public class MainActivity extends Activity implements ActionBar.TabListener {
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	static List<JSONObject> daysUntil;
	static List<JSONObject> daysSince;
	static CardListAdapter cla;
	static CardListAdapter cla2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Calendar c = Calendar.getInstance();
		daysUntil = new ArrayList<JSONObject>();
		daysSince = new ArrayList<JSONObject>();
		
		FileInputStream fis;
		try {
			String[] file = fileList();
			for (int i = 0; i < file.length; i++){
				String listFile = file[i];
				String value = "";
				fis = openFileInput(listFile);
				byte[] input = new byte[fis.available()];
				while(fis.read(input) != -1){
					value += new String(input);
				}
				fis.close();
				try {
					Log.d("OUTPUT",value);
					JSONObject dateJsonObj = new JSONObject(value);
					boolean isAfterCurrentDate = checkIfAfterCurrentDate(dateJsonObj, c);
					if (isAfterCurrentDate){
						daysUntil.add(dateJsonObj);
					} else {
						daysSince.add(dateJsonObj);
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
		
		Collections.sort(daysUntil, new Comparator<JSONObject>() {
			public int compare(JSONObject event1, JSONObject event2){
				long event1ms = eventToMs(event1);
				long event2ms = eventToMs(event2);
				if (event1ms < event2ms) return -1;
				return 1;
			}
		});
		Collections.sort(daysSince, new Comparator<JSONObject>() {
			public int compare(JSONObject event1, JSONObject event2){
				long event1ms = eventToMs(event1);
				long event2ms = eventToMs(event2);
				if (event1ms > event2ms) return -1;
				return 1;
			}
		});
		
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}
	
	public long eventToMs (JSONObject event){
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
	public boolean checkIfAfterCurrentDate(JSONObject event, Calendar c){
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
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date currentDate, eventDate;
		try {
			currentDate = sdf.parse(year+"-"+month+"-"+day);
			eventDate = sdf.parse(eventYear+"-"+eventMonth+"-"+eventDay);
			
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
			return true;
		} else if (id == R.id.create_date) {
			Intent intent = new Intent(this, CreateEvent.class);
			startActivity(intent);
		} else if (id == R.id.clear_all){
			promptToDeleteAll();
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
		public UntilFragment() {
		}
		@Override
		public void onActivityCreated(Bundle savedInstanceState){
			super.onActivityCreated(savedInstanceState);
			getListView().setDivider(null);
			getListView().setDividerHeight(0);
			getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
					alertDialogBuilder.setTitle("Temp Options");
					alertDialogBuilder.setMessage("Do you want to delete this event?")
					.setCancelable(true)
					.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   dialog.dismiss();
		                   }
		               })
		               .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   dialog.dismiss();
		                   }
		               });
	 
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
	 
					// show it 
					alertDialog.show();
					return false;
				}
		    });
			cla = new CardListAdapter(getActivity(), daysUntil);
			setListAdapter(cla);
		}
	}
	
	public static class SinceFragment extends ListFragment {
		public SinceFragment() {
		}
		@Override
		public void onActivityCreated(Bundle savedInstanceState){
			super.onActivityCreated(savedInstanceState);
			getListView().setDivider(null);
			getListView().setDividerHeight(0);
		    getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
					alertDialogBuilder.setTitle("Temp Options");
					alertDialogBuilder.setMessage("Do you want to delete this event?")
					.setCancelable(true)
					.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   dialog.dismiss();
		                   }
		               })
		               .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   dialog.dismiss();
		                   }
		               });
	 
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
	 
					// show it 
					alertDialog.show();
					return false;
				}
		    });
			cla2 = new CardListAdapter(getActivity(), daysSince);
			setListAdapter(cla2);
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
}
