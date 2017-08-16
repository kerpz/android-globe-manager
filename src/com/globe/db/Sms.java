package com.globe.db;

public class Sms {
	
	int id;
	String date;
	String sender;
	String content;
	
	public Sms() {
	}

	public Sms(int id, String date, String sender, String content) {
		this.id = id;
		this.date = date;
		this.sender = sender;
		this.content = content;
	}

	public Sms(String date, String sender, String content) {
		this.date = date;
		this.sender = sender;
		this.content = content;
	}

	public int getID() {
		return this.id;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public String getSender() {
		return this.sender;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getContent() {
		return this.content;
	}
	
	public void setContent(String content) {
		this.sender = content;
	}
}
