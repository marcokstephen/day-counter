package com.sm.daysuntilcards;

public class Event {
	private String name;
	private boolean weekends;
	private String date;
	private String time;
	
	public Event(String jsonString){
		super();
		
	}
	
	public Event(String name, boolean weekends, String date, String time) {
		super();
		this.name = name;
		this.weekends = weekends;
		this.date = date;
		this.time = time;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isWeekends() {
		return weekends;
	}
	public void setWeekends(boolean weekends) {
		this.weekends = weekends;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}