package com.blavin.todolist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import android.content.Context;

public class TodoItemList {
	private ArrayList<TodoItem> mTodoItems;
	
	private static TodoItemList sTodoItemList;
	private Context mAppContext;
	private GsonFileDataManager dataManager;
	
	private TodoItemList(Context appContext){
		mAppContext = appContext;
		dataManager = new GsonFileDataManager(mAppContext);
		
		mTodoItems = dataManager.loadItems();
	}
	
	public static TodoItemList get(Context c){
		if (sTodoItemList == null){
			sTodoItemList = new TodoItemList(c.getApplicationContext());
		}
		return sTodoItemList;
	}
	
	public void saveTodoItems(){
        dataManager.saveItems(mTodoItems);
	}
	public void addTodoItem(TodoItem t) {
        mTodoItems.add(t);
    }
	
    public void deleteTodoItem(TodoItem t) {
        mTodoItems.remove(t);
    }
	
	public ArrayList<TodoItem> getTodoItems(){
		return mTodoItems;
	}
	
	public void clearAllSelected(){
		for(TodoItem t : mTodoItems){
			t.setSelected(false);
		}
	}
	
	public void setAllSelected(){
		for(TodoItem t : mTodoItems){
			t.setSelected(true);
		}
	}
	
	public String emailSelected(){
		if(mTodoItems.isEmpty()) return "";
		String emailBody = "";
		
		for(TodoItem t : mTodoItems){
			if(t.isSelected()){
				emailBody += "Task: \"" + t.getTitle() + "\" is " + (t.isCompleted()? "complete" : "incomplete") + "\n";
				t.setSelected(false);
			}
		}
		
		return emailBody;
	}
	
	public String emailAll(){
		if(mTodoItems.isEmpty()) return "";
		String emailBody = "Current Items:\n";
		ArrayList<TodoItem> tempItems = new ArrayList<TodoItem>();
		for(TodoItem t : mTodoItems){
			if(!t.isArchived())
				emailBody += "Task: \"" + t.getTitle() + "\" is " + (t.isCompleted()? "complete" : "incomplete") + "\n";
			else
				tempItems.add(t);
		}
		emailBody +="\nArchived Items:\n";
		for(TodoItem t : tempItems){
			emailBody += "Task: \"" + t.getTitle() + "\" is " + (t.isCompleted()? "complete" : "incomplete") + "\n";
		}
		
		return emailBody;
	}
	
	public void removeEmptyItems(){
		ArrayList<TodoItem> items = new ArrayList<TodoItem>();
		for(TodoItem t : mTodoItems){
			if(!t.getTitle().equals("")) items.add(t);
		}
		mTodoItems = items;
	}
	
	public ArrayList<TodoItem> getCurrentTodoItems(){
		ArrayList<TodoItem> currentItems = new ArrayList<TodoItem>();
		removeEmptyItems();
		for(TodoItem t : mTodoItems){
			if (!t.isArchived()) currentItems.add(t);
		}
		return currentItems;
	}
	
	public ArrayList<TodoItem> getArchivedTodoItems(){
		ArrayList<TodoItem> archivedItems = new ArrayList<TodoItem>();
		removeEmptyItems();
		for(TodoItem t : mTodoItems){
			if (t.isArchived()) archivedItems.add(t);
		}
		return archivedItems;
	}
	
	public TodoItem getTodoItem(UUID id){
		for (TodoItem t : mTodoItems){
			if (t.getId().equals(id))
				return t;
		}
		return null;
	}
	
	public String getSummary(){
		return "Total: " + mTodoItems.size() + 
				"\nNumber complete: " + getNumComplete() + 
				"\nNumber uncomplete: " + getNumUncomplete() + 
				"\nNumber archived: " + getNumArchived() +
				"\n\tNumber complete & archived: " + getNumArchivedComplete() +
				"\n\tNumber uncomplete & archived: " + getNumArchivedUncomplete();
	}
	
	public int getNumComplete(){
		int count = 0;
		for(TodoItem t : mTodoItems){
			if (t.isCompleted() && !t.isArchived()) count++;
		}
		return count;
	}
	
	public int getNumUncomplete(){
		int count = 0;
		for(TodoItem t : mTodoItems){
			if (!t.isCompleted() && !t.isArchived()) count++;
		}
		return count;
	}
	
	public int getNumArchived(){
		return getArchivedTodoItems().size();
	}
	
	public int getNumArchivedComplete(){
		int count = 0;
		for(TodoItem t : getArchivedTodoItems()){
			if (t.isCompleted()) count++;
		}
		return count;
	}
	
	public int getNumArchivedUncomplete(){
		int count = 0;
		for(TodoItem t : getArchivedTodoItems()){
			if (!t.isCompleted()) count++;
		}
		return count;
	}

}
