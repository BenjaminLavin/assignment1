package com.blavin.todolist;

import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;


public class TodoListActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new TodoItemListFragment();
	}

}