package com.blavin.todolist;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GsonFileDataManager{
	
	private Gson mGson;
	private static final String ITEMS_FILENAME = "currentItems.sav";
	
	private Context ctx;
	
	public GsonFileDataManager(Context ctx) {
		this.ctx = ctx;
		mGson = new Gson();
	}
	

	public ArrayList<TodoItem> loadItems() {
		ArrayList<TodoItem> items = new ArrayList<TodoItem>();

		try {
			BufferedReader fis = new BufferedReader(new InputStreamReader(ctx.openFileInput(ITEMS_FILENAME)));
			String line;
			StringBuffer fileContent = new StringBuffer();
			
			while ((line = fis.readLine()) != null){
				fileContent.append(line);
			}
			
			Type collectionType = new TypeToken<Collection<TodoItem>>(){}.getType();
			items = mGson.fromJson(fileContent.toString(), collectionType);
			
		} catch (Exception e) {
			Log.i("TODOList", "Error casting");
			e.printStackTrace();
		} 

		return items;
		
	}

	public void saveItems(ArrayList<TodoItem> items) {
		try {
			FileOutputStream fos = ctx.openFileOutput(ITEMS_FILENAME, Context.MODE_PRIVATE);
			String json = mGson.toJson(items);
			fos.write(json.getBytes());
			fos.close();
			
			Log.i("Persistence", "Saved: " + json);
			
		} 
		catch (Exception e) {
			Log.i("TodoList", "Saving error");
			e.printStackTrace();
		}
	}

}
