package com.blavin.todolist;

import java.util.UUID;

public class TodoItem {
	
	private UUID mId;
	private String mTitle;
	private boolean mArchived;
	private boolean mCompleted;
	
	public TodoItem() {
		// Generate unique identifier
		mId = UUID.randomUUID();
		mArchived = false;
		mCompleted = false;
		mTitle = "";
	}

	@Override
	public String toString(){
		return mTitle;
	}
	
	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public UUID getId() {
		return mId;
	}

	public boolean isArchived() {
		return mArchived;
	}

	public void setArchived(boolean archived) {
		mArchived = archived;
	}

	public boolean isCompleted() {
		return mCompleted;
	}

	public void setCompleted(boolean completed) {
		mCompleted = completed;
	}


}

