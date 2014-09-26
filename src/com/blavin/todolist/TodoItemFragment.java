/*
 * Inspired by example in Android Programming book (see README)
 *
 * @author Benjamin Lavin
 */

package com.blavin.todolist;

import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class TodoItemFragment extends Fragment {
	public static final String EXTRA_TODO_ITEM_ID = "com.blavin.TODOList.todo_item_id";
	private static final String DIALOG_DELETE = "delete";

    private static final int REQUEST_DELETE = 0;
	
	private TodoItem mTodoItem;
	private EditText mTitleField;
	private CheckBox mCompletedCheckBox;
	private RadioButton mArchivedRadioButton;
	private RadioGroup mRadioButtonsGroup;
	private Button mDeleteButton;
	
	/*
	 * On create, set local var mTodoItem to the one with the UUID passed to this fragment
	 * 
	 * Save all Todo Items on creation to preserve any changes made in the previous fragment
	 * should the user close the app upon reaching this screen
	 */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.task_editor_title);
		UUID todoItemId = (UUID)getArguments().getSerializable(EXTRA_TODO_ITEM_ID);
		mTodoItem = TodoItemList.get(getActivity()).getTodoItem(todoItemId);
		TodoItemList.get(getActivity()).saveTodoItems();
		
		setHasOptionsMenu(true);
		
	}
	
	/*
	 * Taken from Android Programming book (see README)
	 * 
	 * Alternative way to return to previous screen to the "back" button on the phone
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	if (NavUtils.getParentActivityName(getActivity()) != null) {
	                NavUtils.navigateUpFromSameTask(getActivity());
	            }
	            return true;
	        default:
	        	return super.onOptionsItemSelected(item);
	} }
	
	/*
	 * Upon returning from delete dialog, determine whether or not to delete the Todo item
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode != Activity.RESULT_OK) return;
	    if (requestCode == REQUEST_DELETE) {
	        boolean shouldDelete = (boolean)data.getBooleanExtra(DeleteConfirmationFragment.EXTRA_DELETE, false);
	        if(shouldDelete)
	        	deleteItem();
	    }
	}
	
	/*
	 * Delete the Todo item
	 * Return to either the current todos list or the archived list depending on which list the todo item was in
	 * Display a confirmation of deletion toast if the item wasn't blank
	 */
	public void deleteItem(){
		Intent intent;
		if(!mTodoItem.isArchived()){
			intent = new Intent(getActivity(), TodoListActivity.class);
		}
		else{
			intent = new Intent(getActivity(), TodoListArchiveActivity.class);
		}

		if(!mTodoItem.getTitle().replaceAll("\\s+","").equals(""))
			Toast.makeText(this.getActivity(), "\"" + mTodoItem.getTitle() + "\" deleted", Toast.LENGTH_SHORT).show();
		TodoItemList.get(getActivity()).deleteTodoItem(mTodoItem);
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(intent);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_todo_item, parent, false);
		
		/*
		 * Taken from Android Programming book (see README)
		 * 
		 * Alternative way to return to previous screen to the "back" button on the phone
		 */
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	    	if (NavUtils.getParentActivityName(getActivity()) != null) {
	    		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
	    	}
	    }
		
		mDeleteButton = (Button)v.findViewById(R.id.delete_button);
		/*
		 * Setting the color taken from
		 * http://stackoverflow.com/questions/1521640/standard-android-button-with-a-different-color
		 * under attribution creative commons license
		 */
		mDeleteButton.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
		mDeleteButton.setOnClickListener(new View.OnClickListener() { //Confirm user wants to delete the item
			
			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DeleteConfirmationFragment dialog = new DeleteConfirmationFragment();
				dialog.setTargetFragment(TodoItemFragment.this, REQUEST_DELETE);
				dialog.show(fm, DIALOG_DELETE);
				
			}
		});
		
		mTitleField = (EditText) v.findViewById(R.id.todo_item_title);
		mTitleField.setText(mTodoItem.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher() {
			// It only matters when the text changes, not before or after
			public void onTextChanged(CharSequence c, int start, int before, int count){
				mTodoItem.setTitle(c.toString());
			}
			
			public void beforeTextChanged(CharSequence c, int start, int count, int after){
				// Do nothing
			}
			
			public void afterTextChanged(Editable c){
				// Do nothing
			}
		});
		
		// Set and check the completed status of the Todo item
		mCompletedCheckBox = (CheckBox)v.findViewById(R.id.todo_item_completed);
		mCompletedCheckBox.setChecked(mTodoItem.isCompleted());
		mCompletedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
				mTodoItem.setCompleted(isChecked);
			}
		});
		
		mRadioButtonsGroup = (RadioGroup)v.findViewById(R.id.radioGroup1);
		int radioButtonId;
		if(mTodoItem.isArchived()){
			radioButtonId = R.id.todo_item_archived_radio_button;
		}
		else{
			radioButtonId = R.id.todo_item_current_radio_button;
		}
		mRadioButtonsGroup.check(radioButtonId);
		
		//Since there are only 2 choices for the radio buttons, if one isn't selected, then the other must be
		mArchivedRadioButton = (RadioButton)v.findViewById(R.id.todo_item_archived_radio_button);
		mArchivedRadioButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
				mTodoItem.setArchived(isChecked);
			}
		});
		
		return v;
	}
	
	/*
	 * Taken from Android Programming book (see README)
	 */
	public static TodoItemFragment newInstance(UUID todoItemId){
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_TODO_ITEM_ID, todoItemId);
		
		TodoItemFragment fragment = new TodoItemFragment();
		fragment.setArguments(args);
		
		return fragment;
	}

}
