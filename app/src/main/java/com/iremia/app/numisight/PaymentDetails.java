package com.iremia.app.numisight;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.DialogFragment;

import com.iremia.app.numisight.databinding.ActivityPaymentDetailsBinding;
import com.iremia.app.numisight.dbHelper.DatabaseHelper;
import com.iremia.app.numisight.dbHelper.PaymentDbHelper;
import com.iremia.app.numisight.dialog.DeletePaymentDialog;
import com.iremia.app.numisight.helper.IconAssignment;
import com.iremia.app.numisight.model.Account;
import com.iremia.app.numisight.model.Transaction;

import java.text.DateFormat;
import java.util.Objects;

public class PaymentDetails extends AppCompatActivity implements DeletePaymentDialog.DeletePaymentDialogListener {

    private ActivityPaymentDetailsBinding binding;
    private Transaction clickedPaymentRecord;
    private PaymentDbHelper dbh;
    private DatabaseHelper accountsDBH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TODO: Configure action bar to look like in PB 24 app
        // Setup toolbar.
        setSupportActionBar(binding.paymentDetailsToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        dbh = new PaymentDbHelper(PaymentDetails.this, currentDbName());

        accountsDBH = new DatabaseHelper(this);

        Bundle incoming = getIntent().getExtras();

        if (incoming != null) {
            clickedPaymentRecord = dbh.selectPayment(this, incoming.getInt("paymentId"));
        }

        actionBar.setTitle("ID: " + clickedPaymentRecord.getPaymentId());

        displayPaymentInfo();
    }

    private void displayPaymentInfo() {
        IconAssignment icon = new IconAssignment(clickedPaymentRecord.getCategory());
        binding.ivCategoryIconPaymentDetails.setImageDrawable(AppCompatResources.getDrawable(this, icon.getIconRes()));
        binding.tvPaymentDetailsCategory.setText(clickedPaymentRecord.getCategory());
        binding.tvPaymentDetailsAmount.setText(String.valueOf(clickedPaymentRecord.getAmount()));

        if (!Objects.equals(clickedPaymentRecord.getDescription(), "")) {
            binding.tvPaymentDetailsDescription.setVisibility(View.VISIBLE);
            binding.tvPaymentDetailsDescription.setText(clickedPaymentRecord.getDescription());
        }

        binding.tvPaymentDetailsDate.setText(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(clickedPaymentRecord.getTimestamp()));

        String type = getString(R.string.word_type_colon) + clickedPaymentRecord.getType();
        binding.tvPaymentDetailsType.setText(type);

        String balance = getString(R.string.word_balance_colon) + clickedPaymentRecord.getBalance() + " " + dbh.getAccount().getCurrency();
        binding.tvPaymentDetailsBalance.setText(balance);
    }

    @NonNull
    private String currentDbName() {
        String keyFile = getString(R.string.current_account_key_file);
        SharedPreferences sharedPreferences = getSharedPreferences(keyFile, Context.MODE_PRIVATE);
        return sharedPreferences.getString(keyFile, "initialAccount.db");
    }

    /**
     * Deletes payment, corrects balance, writes changes to the database
     */
    private void delete() {
        if (clickedPaymentRecord.getTimestamp() == null || clickedPaymentRecord.getDescription().equals("Error")) {
            dbh.deletePayment(clickedPaymentRecord);
        } else {
            double correctedBalance;
            Account currentAccount = dbh.getAccount();

            if (clickedPaymentRecord.getType() == 1) {
                correctedBalance = (double) Math.round((currentAccount.getBalance() - clickedPaymentRecord.getAmount()) * 100) / 100;
                currentAccount.setBalance(correctedBalance);
            } else if (clickedPaymentRecord.getType() == 0) {
                correctedBalance = (double) Math.round((currentAccount.getBalance() + clickedPaymentRecord.getAmount()) * 100) / 100;
                currentAccount.setBalance(correctedBalance);
            }

            // Write changes to the database
            accountsDBH.updateBalance(currentAccount);
            dbh.deletePayment(clickedPaymentRecord);
        }

        Toast.makeText(PaymentDetails.this, "Deleted", Toast.LENGTH_LONG).show();
    }

    // The dialog confirmation of deletion of a payment record
    @Override
    public void onDialogPositiveClick() {
        delete();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.payment_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            DialogFragment dialog = new DeletePaymentDialog();
            dialog.show(getSupportFragmentManager(), "DeletePaymentDialog");
        }
        return super.onOptionsItemSelected(item);
    }
}