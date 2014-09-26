package com.blavin.todolist;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class TodoItemListArchiveFragment extends ListFragment {
	private static final String TAG = "TodoItemListFragment";
	private static final String DIALOG_EMAIL = "email";
	private static final int REQUEST_EMAIL = 0;
	
	private ArrayList<TodoItem> mTodoItems;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		getActivity().setTitle(R.string.archived);
		mTodoItems = TodoItemList.get(getActivity()).getArchivedTodoItems();
		TodoItemList.get(getActivity()).clearAllSelected();
		
		TodoItemAdapter adapter = new TodoItemAdapter(mTodoItems);
		setListAdapter(adapter);
		
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	    	if (NavUtils.getParentActivityName(getActivity()) != null) {
	        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
	    	}
	    }
		
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
		mTodoItems = TodoItemList.get(getActivity()).getArchivedTodoItems();
		TodoItemList.get(getActivity()).saveTodoItems();
		
		TodoItemAdapter adapter = new TodoItemAdapter(mTodoItems);
		setListAdapter(adapter);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    inflater.inflate(R.menu.archived_todo_list, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.archived_menu_item_show_summary:
	        	String toastString = TodoItemList.get(getActivity()).getSummary();
                Toast toast = Toast.makeText(this.getActivity().getApplicationContext(), toastString, Toast.LENGTH_LONG);
                toast.show();   
                return true;
	        case R.id.archived_menu_item_email:
	        	FragmentManager fm = getActivity().getSupportFragmentManager();
				EmailFragment dialog = new EmailFragment();
				dialog.setTargetFragment(TodoItemListArchiveFragment.this, REQUEST_EMAIL);
				dialog.show(fm, DIALOG_EMAIL);
				return true;
	        case android.R.id.home:
	        	if (NavUtils.getParentActivityName(getActivity()) != null) {
	                NavUtils.navigateUpFromSameTask(getActivity());
	            }
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    	}
	    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode != Activity.RESULT_OK) return;
	    if (requestCode == REQUEST_EMAIL) {
	        int option = (int)data.getIntExtra(EmailFragment.EXTRA_EMAIL, 0);
	        switch (option) {
	        case EmailFragment.EMAIL_ALL:
	        	String emailAllBody = TodoItemList.get(getActivity()).emailAll();
	        	if(!emailAllBody.equals("")){
	        	Intent iii = new Intent(Intent.ACTION_SEND);
	        	iii.setType("message/rfc822");
	        	iii.putExtra(Intent.EXTRA_SUBJECT, "All Todo Items");
	        	iii.putExtra(Intent.EXTRA_TEXT, emailAllBody);
	        	try {
	        	    startActivity(Intent.createChooser(iii, "Send mail..."));
	        	} catch (android.content.ActivityNotFoundException ex) {
	        	    Toast.makeText(this.getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
	        	}
	        	}
	        	else{
	        		Toast.makeText(this.getActivity(), "There are no items", Toast.LENGTH_SHORT).show();
	        	}
	        	break;
	        case EmailFragment.EMAIL_SELECTED:
	        	String emailSelectedBody = TodoItemList.get(getActivity()).emailSelected();
	        	if(!emailSelectedBody.equals("")){
		        	Intent ii = new Intent(Intent.ACTION_SEND);
		        	ii.setType("message/rfc822");
		        	ii.putExtra(Intent.EXTRA_SUBJECT, "Current Todo Items");
		        	ii.putExtra(Intent.EXTRA_TEXT, emailSelectedBody);
		        	try {
		        	    startActivity(Intent.createChooser(ii, "Send mail..."));
		        		} 
		        	catch (android.content.ActivityNotFoundException ex) {
		        	    Toast.makeText(this.getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		        		}
		        	}
	        	else{
	        		Toast.makeText(this.getActivity(), "You haven't selected any items", Toast.LENGTH_SHORT).show();
	        	}
	        	break;
	        default:
	        	break;
	        }
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
					t.setSelected(!t.isSelected());
					((TodoItemAdapter)getListAdapter()).notifyDataSetChanged();
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
					t.setSelected(!t.isSelected());
					((TodoItemAdapter)getListAdapter()).notifyDataSetChanged();
					return false;
				}
			});
			
			TextView moreTextView = (TextView)convertView.findViewById(R.id.list_todo_item_moreTextView);
			moreTextView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent(getActivity(), TodoItemActivity.class);
					i.putExtra(TodoItemFragment.EXTRA_TODO_ITEM_ID, t.getId());
					startActivity(i);
					}
			});
			
			moreTextView.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					t.setSelected(!t.isSelected());
					((TodoItemAdapter)getListAdapter()).notifyDataSetChanged();
					return false;
				}
			});
			
			if(t.isSelected()){
				convertView.setBackgroundColor(Color.parseColor("#BCE6B1"));
			}
			else{
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}
			
			return convertView;
		}
	}

}
