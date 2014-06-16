package com.sm.daysuntilcards;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

public class DrawCalendar extends View {
	Paint paint = new Paint();
	private String monthName;
	private int dayNumber;
	
	public DrawCalendar(Context context, String month, int day) {
		super(context);
		monthName = month;
		dayNumber = day;
	}
	
	@Override
	public void onDraw(Canvas canvas){
		RectF rectf = new RectF(20,0,800,800);
		paint.setStrokeWidth(3);
		paint.setColor(Color.parseColor("#dddddd"));
		canvas.drawRoundRect(rectf, 88, 88, paint);
		rectf = new RectF(0,0,820,220);
		paint.setColor(Color.parseColor("#ff7070"));
		canvas.drawRoundRect(rectf,88,88,paint);
		paint.setTextSize(450);
		if (dayNumber > 9){
			canvas.drawText(dayNumber+"", 160, 660, paint);
		} else {
			canvas.drawText(dayNumber+"", 280, 660, paint);
		}
		paint.setTextSize(220);
		paint.setColor(Color.WHITE);
		canvas.drawText(monthName, 230, 185, paint);
	}
}