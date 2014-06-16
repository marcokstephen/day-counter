package com.sm.daysuntilcards;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

public class DrawCalendar extends View {
	Paint paint = new Paint();
	private String monthName;
	private int dayNumber;
	
	public DrawCalendar(Context context) {
		super(context);
		//monthName = month;
		//dayNumber = day;
	}
	
	public DrawCalendar(Context context, AttributeSet attrs){
		super(context,attrs);
	}
	
	public DrawCalendar(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}
	
	@SuppressWarnings("unused")
	public void onDraw(Canvas canvas){
		/*int canvasSize = canvas.getWidth();
		Log.d("CAN",canvas.getWidth()+"");
		RectF rectf = new RectF(20,0,800,800);
		paint.setStrokeWidth(3);
		paint.setColor(Color.parseColor("#dddddd"));
		canvas.drawRoundRect(rectf, 88, 88, paint);
		rectf = new RectF(0,0,820,220);
		paint.setColor(Color.parseColor("#ff7070"));
		canvas.drawRoundRect(rectf,88,88,paint);
		paint.setTextSize(450);
		if (2 > 9){
			canvas.drawText("b"+"", 160, 660, paint);
		} else {
			canvas.drawText("b"+"", 280, 660, paint);
		}
		paint.setTextSize(220);
		paint.setColor(Color.WHITE);
		canvas.drawText("a", 230, 185, paint);*/
	}
}