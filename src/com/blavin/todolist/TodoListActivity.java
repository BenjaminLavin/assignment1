/*
 * Taken from Android Programming book (see README)
 */

package com.blavin.todolist;

import android.support.v4.app.Fragment;

public class TodoListActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new TodoItemListFragment();
	}

}
