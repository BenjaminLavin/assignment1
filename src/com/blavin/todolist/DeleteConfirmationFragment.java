/** Confirm deletion of todo item before actually deleting it
 * 
 * @author Benjamin Lavin
 *
 */

package com.blavin.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class DeleteConfirmationFragment extends DialogFragment {
	public static final String EXTRA_DELETE = "com.blavin.todolist.delete";
	private boolean mShouldDelete;
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
            .setTitle(R.string.delete_confirmation)
            .setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mShouldDelete = true;
					sendResult(Activity.RESULT_OK);
				}
			})
            .create();
    }
    

    private void sendResult(int resultCode) {
    	if (getTargetFragment() == null)
    		return;
    	Intent i = new Intent();
    	i.putExtra(EXTRA_DELETE, mShouldDelete);
    	getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }
    
}
