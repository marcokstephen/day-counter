package com.sm.daysuntilcards;

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