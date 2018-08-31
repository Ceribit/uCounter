package com.ceri.android.ucounter.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.ceri.android.ucounter.R;
import com.ceri.android.ucounter.ui.activities.CounterActivity;
import com.ceri.android.ucounter.ui.presenters.CounterDialogPresenter;

public class DeleteCounterDialog extends DialogFragment {
    private CounterDialogPresenter mCounterDialogPresenter;
    private int mId;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create corresponding presenter
        mCounterDialogPresenter = new CounterDialogPresenter();
        mCounterDialogPresenter.attach(getContext());

        // Use builder class to construct dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.counter_dialog_delete, null));

        // User clicked delete counter
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(mCounterDialogPresenter.deleteCounter(mId)){
                    Toast.makeText(getContext(), "Counter Deleted", Toast.LENGTH_SHORT).show();
                }
                ((CounterActivity)getActivity()).notifyChange();

            }
        });

        // User clicked cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            Toast.makeText(getContext(), "Counter not added", Toast.LENGTH_SHORT).show();
        }
        });

        return builder.create();
    }

        // Get id
        public void setId(int id){
            Log.e("DeleteCounterDialog57", "You notified me Mr.  " + id);
            mId = id;
        }
}
