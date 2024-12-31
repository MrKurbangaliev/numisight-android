package com.iremia.app.numisight;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iremia.app.numisight.adapter.AccountAdapter;
import com.iremia.app.numisight.databinding.SettingsActivityBinding;
import com.iremia.app.numisight.dbHelper.DatabaseHelper;

public class SettingsActivity extends AppCompatActivity {

    SettingsActivityBinding binding;
    DatabaseHelper dbHelper;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragment()).commit();
        }

        // Action bar configuration.
        setSupportActionBar(binding.settingsToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(SettingsActivity.this);

        recyclerView = binding.rvAccountsEditor;
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        updateAccountsList();

        binding.addAccountBtn.setOnClickListener(view -> {
            Intent i = new Intent(SettingsActivity.this, AddOrEditAccount.class);
            i.putExtra("isEditForm", false);
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAccountsList();
    }

    private void updateAccountsList() {
        RecyclerView.Adapter<AccountAdapter.AccountViewHolder> mAdapter = new AccountAdapter(dbHelper.getAccountsList(SettingsActivity.this), SettingsActivity.this);
        recyclerView.setAdapter(mAdapter);
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}