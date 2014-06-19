package com.sm.daysuntilcardslite;

import com.sm.daysuntilcardslite.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(MainActivity.APP_THEME);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
	}

}