package com.iremia.app.numisight.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.iremia.app.numisight.dbHelper.DatabaseHelper;
import com.iremia.app.numisight.model.Account;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AccountsDialog extends DialogFragment {

    DatabaseHelper dbHelper;
    AccountsDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface.
        try {
            listener = (AccountsDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(requireContext() + " must implement AccountsDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Access database.
        dbHelper = new DatabaseHelper(getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose an account").setItems(accountsArray(dbHelper.getAccountsList(getContext())), (dialog, which) -> listener.onDialogItemClick(getDbFileName(which), getAccount(which)));
        return builder.create();
    }

    private Account getAccount(int which) {
        Account account = new Account();
        String clickedName = String.valueOf(Array.get(accountsArray(dbHelper.getAccountsList(getContext())), which));
        for (Account a : dbHelper.getAccountsList(getContext())) {
            if (a.getAccountLabel().equals(clickedName)) {
                account = a;
            }
        }
        return account;
    }

    /**
     * Finds a specific database file name based on clicked account title in the dialog.
     *
     * @param which Index of a clicked item.
     * @return Database file name.
     */
    @Nullable
    private String getDbFileName(int which) {
        String dbFileName = null;
        String clickedName = String.valueOf(Array.get(accountsArray(dbHelper.getAccountsList(getContext())), which));
        for (Account a : dbHelper.getAccountsList(getContext())) {
            if (a.getAccountLabel().equals(clickedName)) {
                dbFileName = a.getDbReference();
                Toast.makeText(getContext(), "Displaying: " + a, Toast.LENGTH_SHORT).show();
            }
        }
        return dbFileName;
    }

    /**
     * Converts ArrayList of accounts into an array of their titles.
     *
     * @param list List of accounts.
     * @return Simple array of String.
     */
    private String[] accountsArray(ArrayList<Account> list) {
        String[] accountNames = new String[list.size()];

        for (Account a : list) {
            accountNames[list.indexOf(a)] = a.getAccountLabel();
        }

        return accountNames;
    }

    public interface AccountsDialogListener {
        void onDialogItemClick(String dbFileName, Account account);
    }
}
