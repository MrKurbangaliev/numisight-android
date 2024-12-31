package com.iremia.app.numisight;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iremia.app.numisight.adapter.PaymentAdapter;
import com.iremia.app.numisight.databinding.ActivityMainBinding;
import com.iremia.app.numisight.dbHelper.DatabaseHelper;
import com.iremia.app.numisight.dbHelper.PaymentDbHelper;
import com.iremia.app.numisight.dialog.AccountsDialog;
import com.iremia.app.numisight.model.Account;
import com.iremia.app.numisight.model.Transaction;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements AccountsDialog.AccountsDialogListener {

    PaymentDbHelper dbh;
    ActivityMainBinding binding;
    SharedPreferences accountPref;
    Account currentAccount;
    ActionBar actionBar;
    DatabaseHelper accountsDBH;
    RecyclerView recyclerView;
    RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> paymentAdapter;
    int paymentListingCount;
    boolean isAddBtnClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup toolbar
        setSupportActionBar(binding.mainToolbar);
        actionBar = getSupportActionBar();

        // Get accounts database
        accountsDBH = new DatabaseHelper(MainActivity.this);

        paymentListingCount = 20;

        // Withdraw button.
        binding.withdrawBtn.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, CreatePayment.class);
            i.putExtra("operation_sign", 0);
            startActivity(i);
        });

        // Deposit button.
        binding.depositBtn.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, CreatePayment.class);
            i.putExtra("operation_sign", 1);
            startActivity(i);
        });

        // Show more payments button.
        binding.showMoreBtn.setOnClickListener(v -> {
            paymentListingCount += 20;
            updatePaymentsList(dbh);
        });

        // Button animations.
        Animation fromBottom = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        Animation toBottom = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);
        Animation rotateChange = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.rotate_change_anim);
        Animation rotateBack = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.rotate_change_back_anim);

        binding.mainAddBtn.setOnClickListener(v -> {
            if (isAddBtnClicked) {
                changeFab(toBottom, rotateBack, false);
                isAddBtnClicked = false;
            } else {
                changeFab(fromBottom, rotateChange, true);
                isAddBtnClicked = true;
            }
        });

        // Show last used account or open first app launch page
        if (!accountsDBH.getAccountsList(MainActivity.this).isEmpty()) {
            currentAccount = accountsDBH.selectAccount(MainActivity.this, currentDbName());

            // Create a database.
            dbh = new PaymentDbHelper(MainActivity.this, currentDbName());

            // Display current account.
            displayAccountName();

            // Bind RecyclerView to display payment history
            recyclerView = binding.rvPaymentHistory;

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            // Display list and balances.
            updatePaymentsList(dbh);
            displayBalance(currentAccount.getBalance());
        } else {
            startActivity(new Intent(MainActivity.this, InitialAccount.class));
            finish();
        }

        // Handle FAB buttons when scrolled.
        binding.rvPaymentHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Get the layout manager associated with the RecyclerView
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                // Handle show more payment records button
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;

                    // Get the last visible item position
                    int lastVisibleItemPosition = Objects.requireNonNull(linearLayoutManager).findLastVisibleItemPosition();

                    // Get the total item count in the adapter
                    int totalItemCount = linearLayoutManager.getItemCount();

                    // Check if the last visible item is the last item in the data set
                    // Check if items fill the screen
                    if (lastVisibleItemPosition == totalItemCount - 1) {
                        recyclerView.post(() -> {
                            if (checkAmountOfItems(recyclerView, totalItemCount))
                                binding.showMoreBtn.show();
                        });

                    } else {
                        binding.showMoreBtn.hide();
                    }
                }

                // Handle FAB (deposit/withdraw) buttons
                if (dy > 0) {
                    if (isAddBtnClicked) {
                        binding.depositBtn.startAnimation(toBottom);
                        binding.withdrawBtn.startAnimation(toBottom);
                        binding.mainAddBtn.startAnimation(rotateBack);
                        binding.mainAddBtn.hide();

                        binding.depositBtn.setClickable(false);
                        binding.withdrawBtn.setClickable(false);
                        isAddBtnClicked = false;
                    }
                    binding.mainAddBtn.hide();
                } else {
                    binding.mainAddBtn.show();
                }
            }
        });

        // Receive incoming intent message and create a payment using that data
        Bundle incoming = getIntent().getExtras();
        if (incoming != null) {
            createNewPaymentRecord(incoming.getInt("operation_sign"),
                    incoming.getDouble("amount"),
                    (Date) incoming.get("payment_date"),
                    incoming.getString("payment_description"),
                    incoming.getString("payment_category"),
                    incoming.getInt("payment_type"));
        }
    }

    private boolean checkAmountOfItems(@NonNull RecyclerView recyclerView, int totalItemCount) {
        // Calculate how many items can fit on the screen
        // Measure the height of a single item
        View firstChildView = recyclerView.getChildAt(0);
        if (firstChildView != null) {
            int itemsThatFit = recyclerView.getHeight() / firstChildView.getHeight();

            return totalItemCount > itemsThatFit;
        }
        return false;
    }

    /**
     * Show or hide FAB buttons with custom animation.
     *
     * @param verticalMovement   Animation to move buttons to bottom
     * @param rotationalMovement Animation to rotate main button back
     * @param clickable          State of buttons
     */
    private void changeFab(Animation verticalMovement, Animation rotationalMovement, boolean clickable) {
        binding.depositBtn.startAnimation(verticalMovement);
        binding.withdrawBtn.startAnimation(verticalMovement);
        binding.mainAddBtn.startAnimation(rotationalMovement);

        binding.depositBtn.setClickable(clickable);
        binding.withdrawBtn.setClickable(clickable);
    }

    private void displayAccountName() {
        if (actionBar != null) actionBar.setTitle(currentAccount.getAccountLabel());
    }

    /**
     * @return Database name for last used account
     */
    @NonNull
    private String currentDbName() {
        String keyFile = getString(R.string.current_account_key_file);
        accountPref = getSharedPreferences(keyFile, Context.MODE_PRIVATE);
        // TODO: Add checking mechanism for existence of the db file in the database
        // and if not found, set the main account as the current one

        return accountPref.getString(keyFile, "mainAccount.db");
    }

    @Override
    public void onResume() {
        super.onResume();
        paymentListingCount = 20;
        updatePaymentsList(dbh);
        displayBalance(currentAccount.getBalance());
    }

    // Sets adapter for list view.
    private void updatePaymentsList(PaymentDbHelper databaseHelper) {
        paymentAdapter = new PaymentAdapter(databaseHelper.getPaymentsHistory(MainActivity.this, paymentListingCount), this);

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int scrollPosition = Objects.requireNonNull(layoutManager).findFirstVisibleItemPosition();

        View firstVisibleView = layoutManager.findViewByPosition(scrollPosition);
        int offset = (firstVisibleView == null) ? 0 : firstVisibleView.getTop();

        recyclerView.setAdapter(paymentAdapter);
        layoutManager.scrollToPositionWithOffset(scrollPosition, offset);
    }

    // Update views with new data.
    private void displayBalance(double balance) {
        String balanceText = currentAccount.getCurrency() + " " + balance;
        binding.tvBalance.setText(balanceText);
    }

    private void createNewPaymentRecord(int transactionType, double amount, Date date, String description, String category, int paymentMethod) {
        Transaction newTransaction;

        try {
            // Calculate new balances.
            double newBalance = 0;

            if (transactionType == 1) {
                newBalance = round(currentAccount.getBalance() + amount);
            } else if (transactionType == 0) {
                newBalance = round(currentAccount.getBalance() - amount);
            }

            // Create a payment
            newTransaction = new Transaction(-1, transactionType, amount, date, description, category, paymentMethod, newBalance);

            // Record new balance in the database
            currentAccount.setBalance(newBalance);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Error creating a payment record", Toast.LENGTH_SHORT).show();

            newTransaction = new Transaction(-1, 0, 0, Calendar.getInstance().getTime(), "Error", "Error", 0, 0.0);
        }

        // Write payment record
        boolean b = dbh.addPayment(newTransaction);
        if (b) Toast.makeText(MainActivity.this, R.string.word_saved, Toast.LENGTH_SHORT).show();

        // Write balance changes
        accountsDBH.updateBalance(currentAccount);
    }

    private double round(double number) {
        return (double) Math.round((number) * 100) / 100;
    }

    // Action bar menu options.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.accounts) {
            DialogFragment dialog = new AccountsDialog();
            dialog.show(getSupportFragmentManager(), "AccountsDialog");
        } else if (item.getItemId() == R.id.action_settings) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogItemClick(String dbFileName, Account account) {
        SharedPreferences.Editor editor = accountPref.edit();
        editor.putString(getString(R.string.current_account_key_file), dbFileName);
        editor.apply();

        currentAccount = account;
        dbh = new PaymentDbHelper(MainActivity.this, dbFileName);

        // Display current account.
        displayAccountName();

        displayBalance(account.getBalance());
        updatePaymentsList(dbh);
    }
}