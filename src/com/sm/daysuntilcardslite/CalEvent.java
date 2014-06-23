package com.sm.daysuntilcardslite;

public class CalEvent{
	private long date;
	private String name;
	public String getName() {
		return name;
	}
	public long getDate(){
		return date;
	}
	public CalEvent(long date, String name) {
		super();
		this.date = date;
		this.name = name;
	}
}