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
	private CheckBox mSolvedCheckBox;
	private RadioButton mArchivedRadioButton;
	private RadioGroup mRadioButtonsGroup;
	private Button mDeleteButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		UUID todoItemId = (UUID)getArguments().getSerializable(EXTRA_TODO_ITEM_ID);
		mTodoItem = TodoItemList.get(getActivity()).getTodoItem(todoItemId);
		TodoItemList.get(getActivity()).saveTodoItems();
		
		setHasOptionsMenu(true);
		
	}
	
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode != Activity.RESULT_OK) return;
	    if (requestCode == REQUEST_DELETE) {
	        boolean shouldDelete = (boolean)data.getBooleanExtra(DeleteConfirmationFragment.EXTRA_DELETE, false);
	        if(shouldDelete)
	        	deleteMe();
	    }
	}
	
	public void deleteMe(){
		Intent intent;
		if(!mTodoItem.isArchived()){
			intent = new Intent(getActivity(), TodoListActivity.class);
		}
		else{
			intent = new Intent(getActivity(), TodoListArchiveActivity.class);
		}
		TodoItemList.get(getActivity()).deleteTodoItem(mTodoItem);
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(intent);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_todo_item, parent, false);
		

	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	    	if (NavUtils.getParentActivityName(getActivity()) != null) {
	        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
	    	}
	    }
		
		mDeleteButton = (Button)v.findViewById(R.id.delete_button);
		//http://stackoverflow.com/questions/1521640/standard-android-button-with-a-different-color
		mDeleteButton.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
		mDeleteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DeleteConfirmationFragment dialog = new DeleteConfirmationFragment();
				dialog.setTargetFragment(TodoItemFragment.this, REQUEST_DELETE);
				dialog.show(fm, DIALOG_DELETE);
				
			}
		});
		
		
//		mDeleteButton.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent;
//				if(!mTodoItem.isArchived()){
//					intent = new Intent(getActivity(), TodoListActivity.class);
//				}
//				else{
//					intent = new Intent(getActivity(), TodoListArchiveActivity.class);
//				}
//				TodoItemList.get(getActivity()).deleteTodoItem(mTodoItem);
//			    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			    startActivity(intent);
////				if (NavUtils.getParentActivityName(getActivity()) != null) {
////	                NavUtils.navigateUpFromSameTask(getActivity());
////	            }
//			}
//		});

		
		mRadioButtonsGroup = (RadioGroup)v.findViewById(R.id.radioGroup1);
		int radioButtonId;
		if(mTodoItem.isArchived()){
			radioButtonId = R.id.todo_item_archived_radio_button;
		}
		else{
			radioButtonId = R.id.todo_item_current_radio_button;
		}
		mRadioButtonsGroup.check(radioButtonId);
		
		mArchivedRadioButton = (RadioButton)v.findViewById(R.id.todo_item_archived_radio_button);
		mArchivedRadioButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
				mTodoItem.setArchived(isChecked);
			}
		});
		
		mTitleField = (EditText) v.findViewById(R.id.todo_item_title);
		mTitleField.setText(mTodoItem.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence c, int start, int before, int count){
				mTodoItem.setTitle(c.toString());
			}
			
			public void beforeTextChanged(CharSequence c, int start, int count, int after){
				// This space intentionally left blank
			}
			
			public void afterTextChanged(Editable c){
				// This one too
			}
		});
		
		mSolvedCheckBox = (CheckBox)v.findViewById(R.id.todo_item_completed);
		mSolvedCheckBox.setChecked(mTodoItem.isCompleted());
		mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
				// Set the crime's solved property
				mTodoItem.setCompleted(isChecked);
			}
		});
		
		return v;
	}
	
	public static TodoItemFragment newInstance(UUID todoItemId){
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_TODO_ITEM_ID, todoItemId);
		
		TodoItemFragment fragment = new TodoItemFragment();
		fragment.setArguments(args);
		
		return fragment;
	}

}
