package com.iremia.app.numisight;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.iremia.app.numisight.databinding.ActivityInitialAccountBinding;
import com.iremia.app.numisight.dbHelper.DatabaseHelper;
import com.iremia.app.numisight.model.Account;

public class InitialAccount extends AppCompatActivity {

    ActivityInitialAccountBinding binding;
    DatabaseHelper dbh;
    String currency;
    boolean customCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityInitialAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbh = new DatabaseHelper(InitialAccount.this);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_codes, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.spinnerInitialAccountCurrency.setAdapter(adapter);

        // Currency code picker
        binding.spinnerInitialAccountCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!parent.getSelectedItem().toString().equals("Other")) {
                    currency = parent.getSelectedItem().toString();
                } else {
                    binding.etInitialAccountCurrency.setVisibility(View.VISIBLE);
                    binding.spinnerInitialAccountCurrency.setVisibility(View.GONE);
                    customCurrency = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(InitialAccount.this, "Please, select currency code", Toast.LENGTH_SHORT).show();
            }
        });

        // Create button
        binding.btnInitialAccountCreate.setOnClickListener(view -> {
            Account newAccount;

            if (customCurrency)
                currency = binding.etInitialAccountCurrency.getText().toString().toUpperCase();

            try {
                // TODO: Make a filed to enter a balance for initial account
                newAccount = new Account(-1, binding.etInitialAccountTitle.getText().toString(), dbFileName(), currency, 0.0);
            } catch (Exception e) {
                Toast.makeText(InitialAccount.this, "Error while adding an account", Toast.LENGTH_SHORT).show();
                newAccount = new Account(-1, "Error", "No database", "UAH", 0.0);
            }

            boolean accountCreated = dbh.addAccount(newAccount);

            if (accountCreated)
                Toast.makeText(this, "Added " + newAccount.getAccountLabel(), Toast.LENGTH_SHORT).show();

            // Save created account to preferences
            String keyFile = getString(R.string.current_account_key_file);
            SharedPreferences sharedPref = getSharedPreferences(keyFile, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.current_account_key_file), newAccount.getDbReference());
            editor.apply();

            startActivity(new Intent(InitialAccount.this, MainActivity.class));
        });

        binding.etInitialAccountTitle.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == IME_ACTION_DONE) {
                binding.tvInitialAccountDbName.setText(dbFileName());
            }
            return false;
        });
    }

    @NonNull
    private String dbFileName() {
        return binding.etInitialAccountTitle.getText().toString().toLowerCase() + "Account.db";
    }
}