package com.iremia.app.numisight;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.iremia.app.numisight.databinding.ActivityCreatePaymentBinding;
import com.iremia.app.numisight.dialog.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class CreatePayment extends AppCompatActivity implements DatePicker.DatePickerListener {

    ActivityCreatePaymentBinding binding;
    Date currentDate;
    char operationSign;
    String paymentType;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Action bar configuration.
        setSupportActionBar(binding.createPaymentToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        currentDate = Calendar.getInstance().getTime();

        // Listen for incoming intent and define type of payment
        Bundle incoming = getIntent().getExtras();
        if (incoming != null) {
            operationSign = incoming.getChar("operation_sign");
        } else
            Toast.makeText(CreatePayment.this, "(CreatePayment) No Bundle where received", Toast.LENGTH_SHORT).show();

        // Create an ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.drop_down_item, getResources().getStringArray(R.array.categories_array));
        // Apply the adapter to the spinner
        binding.spinnerCategory.setAdapter(adapter);

        // Choose payment type
        binding.rgPaymentType.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton rb = findViewById(i);
            paymentType = rb.getText().toString();
        });

        // Date picker
        binding.btnSetDate.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePicker();
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });

        // Create a new payment record.
        binding.createBtn.setOnClickListener(view -> {
            Intent intent = new Intent(CreatePayment.this, MainActivity.class);

            // Prepare data for sending in an intent.
            double amount = Double.parseDouble(Objects.requireNonNull(binding.etAmount.getText()).toString());
            String paymentDescription = Objects.requireNonNull(binding.etPaymentDescription.getText()).toString().trim();
            category = binding.spinnerCategory.getText().toString();
            if (category.equals("Same as description")) {
                category = Objects.requireNonNull(binding.etPaymentDescription.getText()).toString();
            } else if (category.isEmpty()) {
                category = null;
            }

            if (paymentType != null) {
                if (category != null) {
                    intent.putExtra("operation_sign", operationSign);
                    intent.putExtra("amount", amount);
                    intent.putExtra("payment_date", currentDate);
                    intent.putExtra("payment_description", paymentDescription);
                    intent.putExtra("payment_category", category);
                    intent.putExtra("payment_type", paymentType);

                    startActivity(intent);

                    finish();
                } else
                    Toast.makeText(this, "Choose a category, please.", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(this, "Choose a payment type, please.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDialogDatePicked(Date date) {
        currentDate = date;
    }
}