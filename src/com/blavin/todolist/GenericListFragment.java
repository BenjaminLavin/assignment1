/*
 * @author Benjamin Lavin
 * 
 * Generic List Fragment class which provides some common code for subclasses
 */

package com.blavin.todolist;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public abstract class GenericListFragment extends ListFragment {
	protected static final String DIALOG_EMAIL = "email";
	protected static final int REQUEST_EMAIL = 0;
	
	protected ArrayList<TodoItem> mTodoItems;
	
	/*
	 * Upon getting info back from the email dialog fragment, decide which type of emailing to do
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode != Activity.RESULT_OK) return; 
	    if (requestCode == REQUEST_EMAIL) {
	        int option = (int)data.getIntExtra(EmailFragment.EXTRA_EMAIL, 0);
	        switch (option) {
		        case EmailFragment.EMAIL_ALL: //Email all (current and archived) items
		        	String emailAllBody = TodoItemList.get(getActivity()).getEmailBodyForAll();
		        	if(!emailAllBody.equals("")){ //Check if there is actually anything to email. Provide toast error if not
		        		sendEmail(emailAllBody, "All Todo Items");
		        	}
		        	else{
		        		Toast.makeText(this.getActivity(), "There are no items", Toast.LENGTH_SHORT).show();
		        	}
		        	break;
		        case EmailFragment.EMAIL_SELECTED: //Email only items that are currently selected
		        	String emailSelectedBody = TodoItemList.get(getActivity()).getEmailBodyForSelected();
		        	if(!emailSelectedBody.equals("")){ //Check if there is actually anything to email. Provide toast error if not
		        		sendEmail(emailSelectedBody, "Selected Todo Items");
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
	
	public void sendEmail(String body, String subject){
		/*Following taken from
		 * http://stackoverflow.com/questions/2197741/how-can-i-send-emails-from-my-android-application
		 * under attribution creative commons license
		 * */
    	Intent emailAllIntent = new Intent(Intent.ACTION_SEND);
    	emailAllIntent.setType("message/rfc822");
    	emailAllIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
    	emailAllIntent.putExtra(Intent.EXTRA_TEXT, body);
    	
    	try {
    	    startActivity(Intent.createChooser(emailAllIntent, "Send mail..."));
    	} catch (android.content.ActivityNotFoundException ex) {
    	    Toast.makeText(this.getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
    	}
	}
	
	// Inner class for custom ArrayList adapter
	protected class TodoItemAdapter extends ArrayAdapter<TodoItem>{
		public TodoItemAdapter(ArrayList<TodoItem> todoItems){
			super(getActivity(), 0, todoItems);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			// Inflate new view if one wasn't provided
			// Taken from Android Programming book (see README)
			if (convertView == null){
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_todo_item, null);
			}
			
			// Configure the view for this Todo Item
			final TodoItem t = getItem(position);
			
			//Title of the Todo Item
			TextView titleTextView = (TextView)convertView.findViewById(R.id.list_todo_item_titleTextView);
			titleTextView.setText(t.getTitle());

			//Toggle complete on click
			titleTextView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					t.setCompleted(!t.isCompleted());
					((TodoItemAdapter)getListAdapter()).notifyDataSetChanged();
					TodoItemList.get(getActivity()).saveTodoItems();
					
				}
			});
			
			//Toggle selected on long click
			titleTextView.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					t.setSelected(!t.isSelected());
					((TodoItemAdapter)getListAdapter()).notifyDataSetChanged();
					return false;
				}
			});
			
			//Visual representation of the completed status of the Todo Item
			CheckBox completedCheckBox = (CheckBox)convertView.findViewById(R.id.list_todo_item_completedCheckBox);
			completedCheckBox.setChecked(t.isCompleted());
			
			//Toggle complete on click
			completedCheckBox.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					t.setCompleted(!t.isCompleted());
					((TodoItemAdapter)getListAdapter()).notifyDataSetChanged();
					TodoItemList.get(getActivity()).saveTodoItems();
					
				}
			});
			
			//Toggle selected on long click
			completedCheckBox.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					t.setSelected(!t.isSelected());
					((TodoItemAdapter)getListAdapter()).notifyDataSetChanged();
					return false;
				}
			});
			
			//Used to open new fragment to edit current Todo Item
			TextView moreTextView = (TextView)convertView.findViewById(R.id.list_todo_item_moreTextView);
			moreTextView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent(getActivity(), TodoItemActivity.class);
					i.putExtra(TodoItemFragment.EXTRA_TODO_ITEM_ID, t.getId()); //Pass Todo Item's unique UUID so the info can be populated in the new fragment
					startActivity(i);
					}
			});
			
			//Toggle selected on long click
			moreTextView.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					t.setSelected(!t.isSelected());
					((TodoItemAdapter)getListAdapter()).notifyDataSetChanged();
					return false;
				}
			});
			
			//Set background color of the whole section to help visualize of the item is selected or not
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
