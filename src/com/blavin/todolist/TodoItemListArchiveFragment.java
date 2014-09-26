/*
 * @author Benjamin Lavin
 */
package com.blavin.todolist;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class TodoItemListArchiveFragment extends GenericListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		getActivity().setTitle(R.string.archived_title);
		mTodoItems = TodoItemList.get(getActivity()).getTodoItems(TodoItemList.CHOICE_ARCHIVED);
		TodoItemList.get(getActivity()).changeAllSelectedToBe(false);
		
		TodoItemAdapter adapter = new TodoItemAdapter(mTodoItems);
		setListAdapter(adapter);
		
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
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		mTodoItems = TodoItemList.get(getActivity()).getTodoItems(TodoItemList.CHOICE_ARCHIVED);
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
			/*
			 * Taken from Android Programming book (see README)
			 * 
			 * Alternative way to return to previous screen to the "back" button on the phone
			 */
	        case android.R.id.home:
	        	if (NavUtils.getParentActivityName(getActivity()) != null) {
	                NavUtils.navigateUpFromSameTask(getActivity());
	            }
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    	}
	    }
	


}
