package com.nunesrafael.android.checkpoint.model;

import java.util.Date;

public class Allocation {
	
	private long id;
	private Date entryTime;
	private Date exitTime;
	private String comment;
	private String category;
	
	public Allocation() {
		
	}
	
	public Allocation(long id) {
		this.id = id;
	}
	
	public Allocation(int id, Date entryTime, Date exitTime, String comment, String category) {
		this.id = id;
		this.entryTime = entryTime;
		this.exitTime = exitTime;
		this.comment = comment;
		this.category = category;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getEntryTime() {
		return entryTime;
	}
	public void setEntryTime(Date entryTime) {
		this.entryTime = entryTime;
	}
	public Date getExitTime() {
		return exitTime;
	}
	public void setExitTime(Date exitTime) {
		this.exitTime = exitTime;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
}