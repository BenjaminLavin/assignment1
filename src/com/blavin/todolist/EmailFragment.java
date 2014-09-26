/**
 * @author Benjamin Lavin
 * 
 * Email dialog fragment to prompt for email type
 */
package com.blavin.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;


public class EmailFragment extends DialogFragment {
	public static final String EXTRA_EMAIL = "com.blavin.todolist.email";
	public static final int EMAIL_SELECTED = 0;
	public static final int EMAIL_ALL = 1;
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_email, null);
    	
    	AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
    	dialog.setView(v);
    	Button emailAllButton = (Button)v.findViewById(R.id.dialog_email_all);
    	emailAllButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendResult(Activity.RESULT_OK, EMAIL_ALL);
				dismiss();
			}
		});
    	
    	Button emailSelectedButton = (Button)v.findViewById(R.id.dialog_email_selected);
    	emailSelectedButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendResult(Activity.RESULT_OK, EMAIL_SELECTED);
				dismiss();
			}
		});
    	
        return dialog.create();
    }
    

    private void sendResult(int resultCode, int option) {
    	if (getTargetFragment() == null)
    		return;
    	Intent i = new Intent();
    	i.putExtra(EXTRA_EMAIL, option);
    	getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }
    
}
