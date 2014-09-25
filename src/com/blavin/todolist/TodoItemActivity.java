package com.blavin.todolist;

import java.util.UUID;

import android.support.v4.app.Fragment;

public class TodoItemActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
    	UUID todoItemId = (UUID)getIntent().getSerializableExtra(TodoItemFragment.EXTRA_TODO_ITEM_ID);
    	
    	return TodoItemFragment.newInstance(todoItemId);
    }

}
