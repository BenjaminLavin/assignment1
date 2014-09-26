/*
 * @author Benjamin Lavin
 */
package com.blavin.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class TodoItemListFragment extends GenericListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		getActivity().setTitle(R.string.current_title);
		mTodoItems = TodoItemList.get(getActivity()).getTodoItems(TodoItemList.CHOICE_CURRENT);
		
		TodoItemAdapter adapter = new TodoItemAdapter(mTodoItems);
		setListAdapter(adapter);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		mTodoItems = TodoItemList.get(getActivity()).getTodoItems(TodoItemList.CHOICE_CURRENT);
		TodoItemList.get(getActivity()).changeAllSelectedToBe(false);
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
	        	Intent archivedIntent = new Intent(getActivity(), TodoListArchiveActivity.class);
	            startActivityForResult(archivedIntent, 0);
	        	return true;
	        case R.id.menu_item_show_summary:
	        	String toastString = TodoItemList.get(getActivity()).getSummary();
                Toast toast = Toast.makeText(this.getActivity().getApplicationContext(), toastString, Toast.LENGTH_LONG);
                toast.show();      
	        	return true;
	        case R.id.menu_item_email:
	        	FragmentManager fm = getActivity().getSupportFragmentManager();
				EmailFragment dialog = new EmailFragment();
				dialog.setTargetFragment(TodoItemListFragment.this, REQUEST_EMAIL);
				dialog.show(fm, DIALOG_EMAIL);
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    	}
	    }


}
