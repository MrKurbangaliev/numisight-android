package com.iremia.app.numisight.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.iremia.app.numisight.R;

import java.util.Objects;

public class DeletePaymentDialog extends DialogFragment {

    // Use this instance of the interface to deliver action events
    DeletePaymentDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (DeletePaymentDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(requireActivity() + " must implement DeletePaymentDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_payment_record)
                .setPositiveButton(R.string.word_yes, (dialog, id) -> {
                    // Send the positive btn_save_account event back to the host activity
                    listener.onDialogPositiveClick();
                })
                .setNegativeButton(R.string.word_no, (dialog, id) -> Objects.requireNonNull(DeletePaymentDialog.this.getDialog()).dismiss());
        // Create the AlertDialog object and return it
        return builder.create();
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface DeletePaymentDialogListener {
        void onDialogPositiveClick();
    }
}
