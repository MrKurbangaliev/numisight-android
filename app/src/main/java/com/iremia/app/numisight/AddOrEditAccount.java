package com.iremia.app.numisight;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.iremia.app.numisight.databinding.ActivityAddAccountBinding;
import com.iremia.app.numisight.dbHelper.DatabaseHelper;
import com.iremia.app.numisight.model.Account;

public class AddOrEditAccount extends FragmentActivity {

    ActivityAddAccountBinding binding;
    DatabaseHelper accountsDBH;
    String dbName;
    String dbFileName;
    String currency;
    boolean customCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Instantiate accounts database
        accountsDBH = new DatabaseHelper(AddOrEditAccount.this);

        // Receive info about clicked account
        Bundle incomingIntent = getIntent().getExtras();

        // Implement logic for add or edit account
        if (incomingIntent != null) {
            dbName = incomingIntent.getString("database_name");

            if (incomingIntent.getBoolean("isEditForm")) {
                editAccount();
            } else {
                displayDbName();
                addNewAccount();
            }
        }

        // Cancel button
        binding.btnCancel.setOnClickListener(view -> finish());
    }

    private void displayDbName() {
        binding.etAccountTitle.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == IME_ACTION_DONE) {
                dbFileName = binding.etAccountTitle.getText().toString().toLowerCase() + "Account.db";
                String text = "File name: " + dbFileName;
                binding.tvDbFileName.setVisibility(View.VISIBLE);
                binding.tvDbFileName.setText(text);
            } else {
                Toast.makeText(AddOrEditAccount.this, "Please, press enter key", Toast.LENGTH_SHORT).show();
            }

            return false;
        });
    }

    private void editAccount() {
        binding.btnDelete.setVisibility(View.VISIBLE);
        binding.spinnerCurrency.setVisibility(View.GONE);
        binding.tvAddAccountCurrency.setVisibility(View.GONE);
        binding.tvDbFileName.setVisibility(View.VISIBLE);

        Account clickedAccount = accountsDBH.selectAccount(AddOrEditAccount.this, dbName);

        binding.etAccountTitle.setText(clickedAccount.getAccountLabel());

        String infoText = "Currency: " + clickedAccount.getCurrency() + "\n\nFile: " + clickedAccount.getDbReference();
        binding.tvDbFileName.setText(infoText);

        // Save changes
        binding.btnSaveAccount.setOnClickListener(v -> {
            String newName = binding.etAccountTitle.getText().toString();
            clickedAccount.setAccountLabel(newName);
            accountsDBH.editAccount(clickedAccount);
            finish();
        });

        // Delete button
        binding.btnDelete.setOnClickListener(v -> deleteAccount(clickedAccount));
    }

    private void addNewAccount() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_codes, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.spinnerCurrency.setAdapter(adapter);

        // Currency code picker
        binding.spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!parent.getSelectedItem().toString().equals("Other")) {
                    currency = parent.getSelectedItem().toString();
                } else {
                    binding.etCurrency.setVisibility(View.VISIBLE);
                    binding.spinnerCurrency.setVisibility(View.GONE);
                    customCurrency = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(AddOrEditAccount.this, "Please, select currency code", Toast.LENGTH_SHORT).show();
            }
        });

        // Save button
        binding.btnSaveAccount.setOnClickListener(view -> {
            Account newAccount;

            dbFileName = binding.etAccountTitle.getText().toString().toLowerCase() + "Account.db";

            if (customCurrency) currency = binding.etCurrency.getText().toString().toUpperCase();

            try {
                newAccount = new Account(-1, binding.etAccountTitle.getText().toString(), dbFileName, currency, 0.0);
            } catch (Exception e) {
                Toast.makeText(AddOrEditAccount.this, "Error while adding an account", Toast.LENGTH_SHORT).show();
                newAccount = new Account(-1, "Error", "No database", "UAH", 0.0);
            }

            boolean accountCreated = accountsDBH.addAccount(newAccount);

            if (accountCreated)
                Toast.makeText(this, "Added " + newAccount.getAccountLabel(), Toast.LENGTH_SHORT).show();

            finish();
        });
    }

    private void deleteAccount(Account account) {
        accountsDBH.deleteAccount(account);

        if (accountsDBH.deleteAccountDbFile(AddOrEditAccount.this, account.getDbReference()))
            Toast.makeText(AddOrEditAccount.this, "Account deleted!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(AddOrEditAccount.this, "Error while deleting account", Toast.LENGTH_SHORT).show();
        finish();
    }
}