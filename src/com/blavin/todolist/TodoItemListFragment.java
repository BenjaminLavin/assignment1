package com.blavin.todolist;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class TodoItemListFragment extends ListFragment {
	private static final String TAG = "TodoItemListFragment";
	
	private ArrayList<TodoItem> mTodoItems;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		getActivity().setTitle(R.string.todo_item_title);
		mTodoItems = TodoItemList.get(getActivity()).getCurrentTodoItems();
		
		TodoItemAdapter adapter = new TodoItemAdapter(mTodoItems);
		setListAdapter(adapter);
		
	}
	
//	@Override
//	public void onListItemClick(ListView l, View v, int position, long id){
//		TodoItem t = ((TodoItemAdapter)getListAdapter()).getItem(position);
//		
//		// Start CrimeActivity
//		//Intent i = new Intent(getActivity(), TodoItemActivity.class);
//		//i.putExtra(TodoItemFragment.EXTRA_TODO_ITEM_ID, t.getId());
//		//startActivity(i);
//		t.setCompleted(!t.isCompleted());
//		((TodoItemAdapter)getListAdapter()).notifyDataSetChanged();
//	}
	
	@Override
	public void onResume(){
		super.onResume();
		mTodoItems = TodoItemList.get(getActivity()).getCurrentTodoItems();
		TodoItemList.get(getActivity()).saveTodoItems();
		
		TodoItemAdapter adapter = new TodoItemAdapter(mTodoItems);
		setListAdapter(adapter);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    inflater.inflate(R.menu.todo_list, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.menu_item_new_todo_item:
	            TodoItem todoItem = new TodoItem();
	            TodoItemList.get(getActivity()).addTodoItem(todoItem);
	            Intent i = new Intent(getActivity(), TodoItemActivity.class);
	            i.putExtra(TodoItemFragment.EXTRA_TODO_ITEM_ID, todoItem.getId());
	            startActivityForResult(i, 0);
	            return true;
	        case R.id.menu_item_archived:
	        	Intent ii = new Intent(getActivity(), TodoListArchiveActivity.class);
	            startActivityForResult(ii, 0);
	        	return true;
	        case R.id.menu_item_show_summary:
	        	String toastString = TodoItemList.get(getActivity()).getSummary();
                Toast toast = Toast.makeText(this.getActivity().getApplicationContext(), toastString, Toast.LENGTH_LONG);
                toast.show();
                
                return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    	}
	    }
	
		private class TodoItemAdapter extends ArrayAdapter<TodoItem>{
		public TodoItemAdapter(ArrayList<TodoItem> todoItems){
			super(getActivity(), 0, todoItems);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			// If we weren't given a view, inflate one
			if (convertView == null){
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_todo_item, null);
			}
			
			// Configure the view for this Todo Item
			final TodoItem t = getItem(position);
			
			TextView titleTextView = (TextView)convertView.findViewById(R.id.list_todo_item_titleTextView);
			titleTextView.setText(t.getTitle());
			
			titleTextView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					t.setCompleted(!t.isCompleted());
					((TodoItemAdapter)getListAdapter()).notifyDataSetChanged();
					TodoItemList.get(getActivity()).saveTodoItems();
					
				}
			});
			
			titleTextView.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					Intent i = new Intent(getActivity(), TodoItemActivity.class);
					i.putExtra(TodoItemFragment.EXTRA_TODO_ITEM_ID, t.getId());
					startActivity(i);
					return false;
				}
			});
			
			CheckBox completedCheckBox = (CheckBox)convertView.findViewById(R.id.list_todo_item_completedCheckBox);
			completedCheckBox.setChecked(t.isCompleted());
			completedCheckBox.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					t.setCompleted(!t.isCompleted());
					((TodoItemAdapter)getListAdapter()).notifyDataSetChanged();
					TodoItemList.get(getActivity()).saveTodoItems();
					
				}
			});
			completedCheckBox.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					Intent i = new Intent(getActivity(), TodoItemActivity.class);
					i.putExtra(TodoItemFragment.EXTRA_TODO_ITEM_ID, t.getId());
					startActivity(i);
					return false;
				}
			});
			
			return convertView;
		}
	}

}
