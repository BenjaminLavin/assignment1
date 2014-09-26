/*
 * @author Benjamin Lavin
 */
package com.blavin.todolist;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;

// Singleton class. We only ever want one instance of this
public class TodoItemList {
	private ArrayList<TodoItem> mTodoItems;
	private static TodoItemList sTodoItemList;
	private Context mAppContext;
	private GsonFileDataManager dataManager;
	public static final int CHOICE_CURRENT = 0;
	public static final int CHOICE_ARCHIVED = 1;
	
	private TodoItemList(Context appContext){
		mAppContext = appContext;
		dataManager = new GsonFileDataManager(mAppContext);
		mTodoItems = loadTodoItems(); //Load items from file upon creation
	}
	
	//Create new instance if none exists, otherwise just return the existing instance
	public static TodoItemList get(Context c){
		if (sTodoItemList == null){
			sTodoItemList = new TodoItemList(c.getApplicationContext());
		}
		return sTodoItemList;
	}
	
	//Load items from file
	public ArrayList<TodoItem> loadTodoItems(){
		return dataManager.loadItems();
	}
	
	//Save items to file
	public void saveTodoItems(){
        dataManager.saveItems(mTodoItems);
	}
	
	//Add new item
	public void addTodoItem(TodoItem t) {
        mTodoItems.add(t);
    }
	
	//Delete item
    public void deleteTodoItem(TodoItem t) {
        mTodoItems.remove(t);
    }
	
    //Return ALL items
	public ArrayList<TodoItem> getTodoItems(){
		return mTodoItems;
	}
	
	//Return either current or archived items
	public ArrayList<TodoItem> getTodoItems(int choice){
		ArrayList<TodoItem> currentItems = new ArrayList<TodoItem>();
		ArrayList<TodoItem> archivedItems = new ArrayList<TodoItem>();
		removeEmptyItems();
		for(TodoItem t : mTodoItems){
			if (t.isArchived()) archivedItems.add(t);
			else currentItems.add(t);
		}
		
		switch(choice){
		case CHOICE_CURRENT:
			return currentItems;
		case CHOICE_ARCHIVED:
			return archivedItems;
		default:
			return mTodoItems;
		}
	}
	
	// Set or clear all items' selected variable
	public void changeAllSelectedToBe(boolean bool){
		for(TodoItem t : mTodoItems){
			t.setSelected(bool);
		}
	}
	
	// Get formated email body for selected Todo items
	public String getEmailBodyForSelected(){
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
	
	// Get formated email body for ALL Todo items with current first, then archived for easier distinction
	public String getEmailBodyForAll(){
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
	
	// Remove all items with a blank title
	public void removeEmptyItems(){
		ArrayList<TodoItem> items = new ArrayList<TodoItem>();
		for(TodoItem t : mTodoItems){
			if(!t.getTitle().equals("")) items.add(t);
		}
		mTodoItems = items;
	}
	
	// Get Todo item by it's unique ID
	public TodoItem getTodoItem(UUID id){
		for (TodoItem t : mTodoItems){
			if (t.getId().equals(id))
				return t;
		}
		return null;
	}
	
	// Get formatted summary of the Todo item list
	public String getSummary(){
		return "Total: " + mTodoItems.size() + 
				"\nNumber complete: " + getNumComplete(true, false) + 
				"\nNumber uncomplete: " + getNumComplete(false, false) + 
				"\nNumber archived: " + getNumArchived() +
				"\n\tNumber complete & archived: " + getNumComplete(true, true) +
				"\n\tNumber uncomplete & archived: " + getNumComplete(false, true);
	}
	
	// Get number of completed or uncompleted items that are current or archived
	public int getNumComplete(boolean complete, boolean archived){
		int count = 0;
		for(TodoItem t : mTodoItems){
			if (t.isCompleted() == complete && t.isArchived() == archived) count++;
		}
		return count;
	}
	
	// Number of archived items only
	public int getNumArchived(){
		return getTodoItems(CHOICE_ARCHIVED).size();
	}

}
