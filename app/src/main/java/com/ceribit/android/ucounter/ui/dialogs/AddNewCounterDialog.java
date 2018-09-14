package com.ceribit.android.ucounter.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import com.ceribit.android.ucounter.R;
import com.ceribit.android.ucounter.ui.activities.CounterActivity;
import com.ceribit.android.ucounter.ui.presenters.CounterDialogPresenter;


public class AddNewCounterDialog extends DialogFragment {

    private CounterDialogPresenter mCounterDialogPresenter;

    // Specifies whether to jump to the end of the ViewPager
    private static int MOVE_TO_END = -1;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create corresponding presenter
        mCounterDialogPresenter = new CounterDialogPresenter();
        mCounterDialogPresenter.attach(getContext());

        // Use builder class to construct dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), getResources().getColor(R.color.colorAccent));
        LayoutInflater inflater = getActivity().getLayoutInflater();


        builder.setView(inflater.inflate(R.layout.counter_dialog_add, null));

        SpannableString title = new SpannableString(getString(R.string.dialog_add_title));
        title.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark)),
                0,
                title.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        builder.setTitle(title);
        // User clicked create counter
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText editText = getDialog().findViewById(R.id.dialog_counter_view);
                String counterName= editText.getText().toString();
                if(mCounterDialogPresenter.insertCounter(counterName, 0)){ // Inform user of new insert
                    Toast.makeText(getContext(), "New counter added", Toast.LENGTH_SHORT).show();
                }
                ((CounterActivity)getActivity()).notifyChange(MOVE_TO_END);
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
