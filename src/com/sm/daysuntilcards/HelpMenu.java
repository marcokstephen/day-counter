package com.sm.daysuntilcards;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class HelpMenu extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(MainActivity.APP_THEME);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_menu);
		TextView helpTextView = (TextView) findViewById(R.id.helpTextView);
		String htmlString = "&#8226; To view more details about an event, tap the"
				+ " event's card<p> &#8226; To edit or delete an event, long press "
				+ "the card and an options menu will appear<br />";
		helpTextView.setText(Html.fromHtml(htmlString));
	}
}
