package com.ceri.android.ucounter.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import com.ceri.android.ucounter.R;
import com.ceri.android.ucounter.ui.activities.CounterActivity;
import com.ceri.android.ucounter.ui.presenters.CounterDialogPresenter;


public class AddNewCounterDialog extends DialogFragment {

    private CounterDialogPresenter mCounterDialogPresenter;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create corresponding presenter
        mCounterDialogPresenter = new CounterDialogPresenter();
        mCounterDialogPresenter.attach(getContext());

        // Use builder class to construct dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.counter_dialog_add, null));

        // Set response value
        // User clicked create counter
        builder.setPositiveButton("Create new counter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText editText = getDialog().findViewById(R.id.new_counter_name);
                String counterName= editText.getText().toString();
                if(mCounterDialogPresenter.insertCounter(counterName, 0)){ // Inform user of new insert
                    Toast.makeText(getContext(), "New counter added", Toast.LENGTH_SHORT).show();
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

}
