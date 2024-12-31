package com.iremia.app.numisight.dbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.iremia.app.numisight.model.Account;
import com.iremia.app.numisight.model.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class PaymentDbHelper extends SQLiteOpenHelper {

    public static final String COLUMN_ID = "PaymentId";
    public static final String COLUMN_OPERATION_SIGN = "OperationSign";
    public static final String COLUMN_AMOUNT = "Amount";
    public static final String COLUMN_DATE = "PaymentDate";
    public static final String COLUMN_TIME = "PaymentTime";
    public static final String COLUMN_CATEGORY = "Category";
    public static final String COLUMN_PAYMENT_TYPE = "PaymentType";
    public static final String COLUMN_BALANCE = "Balance";
    public static final String PAYMENT_HISTORY = "PaymentHistory";
    public static final String COLUMN_PAYMENT_DESCRIPTION = "PaymentDescription";

    private final Context context;
    DatabaseHelper databaseHelper;
    Date paymentDate;

    // Constructor
    public PaymentDbHelper(@Nullable Context context, @Nullable String name) {
        super(context, name, null, 1);
        this.context = context;
    }

    // This is called on the first time the database object is accessed.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + PAYMENT_HISTORY + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_OPERATION_SIGN + " CHAR, " + COLUMN_AMOUNT + " DOUBLE, " + COLUMN_DATE + " DATE, " + COLUMN_TIME + " TIMESTAMP, " + COLUMN_PAYMENT_DESCRIPTION + " TEXT, " + COLUMN_CATEGORY + " TEXT, " + COLUMN_PAYMENT_TYPE + " TEXT, " + COLUMN_BALANCE + " DOUBLE);";

        db.execSQL(createTableStatement);
    }

    // This is called when the number of version is changed.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addPayment(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(transaction.getTimestamp());
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(transaction.getTimestamp());

        cv.put(COLUMN_OPERATION_SIGN, transaction.getType());
        cv.put(COLUMN_AMOUNT, transaction.getAmount());
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TIME, timestamp);
        cv.put(COLUMN_PAYMENT_DESCRIPTION, transaction.getDescription());
        cv.put(COLUMN_CATEGORY, transaction.getCategory());
        cv.put(COLUMN_PAYMENT_TYPE, transaction.getPaymentMethod());
        cv.put(COLUMN_BALANCE, transaction.getBalance());

        long insert = db.insert(PAYMENT_HISTORY, null, cv);
        return insert != -1;
    }

    public void deletePayment(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "DELETE FROM " + PAYMENT_HISTORY +
                " WHERE " + COLUMN_ID + "=" + transaction.getPaymentId() + ";";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        cursor.close();
        db.close();
    }

    public Transaction selectPayment(Context context, int id) {
        Transaction selectedPaymentRecord;

        String query = "SELECT * FROM " + PAYMENT_HISTORY + " WHERE " + COLUMN_ID + " = " + id + ";";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            int paymentId = cursor.getInt(0);
            int transactionType = cursor.getInt(1);
            double paymentAmount = cursor.getDouble(2);

            // Extract and format date from text to Date object
            if (cursor.getString(4) == null || Objects.equals(cursor.getString(4), "")) {
                extractOldDateFormat(cursor);
            } else {
                extractNewDateFormat(cursor);
            }

            String paymentDescription = cursor.getString(5);
            String paymentCategory = cursor.getString(6);
            int paymentMethod = cursor.getInt(7);
            double paymentBalance = cursor.getDouble(8);

            selectedPaymentRecord = new Transaction(paymentId, transactionType, paymentAmount, paymentDate, paymentDescription, paymentCategory, paymentMethod, paymentBalance);
        } else {
            Toast.makeText(context, "Error in AccountDBHelper.selectAccount()", Toast.LENGTH_SHORT).show();
            selectedPaymentRecord = null;
        }

        cursor.close();
        db.close();

        return selectedPaymentRecord;
    }

    public ArrayList<Transaction> getPaymentsHistory(Context context, int listingCount) {
        ArrayList<Transaction> returnList = new ArrayList<>();

        // Get data from the database.
        String queryString = String.format(Locale.ENGLISH, "SELECT * FROM %s ORDER BY %s DESC LIMIT %d;", PAYMENT_HISTORY, COLUMN_TIME, listingCount);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            // Loop through the result set and create new payment objects. Put them into returnList.
            do {
                int paymentID = cursor.getInt(0);
                int transactionType = cursor.getInt(1);
                double amount = cursor.getDouble(2);

                // Extract and format date from text to Date object
                if (cursor.getString(4) == null || Objects.equals(cursor.getString(4), "")) {
                    extractOldDateFormat(cursor);
                } else {
                    extractNewDateFormat(cursor);
                }

                String paymentDescription = cursor.getString(5);
                String paymentCategory = cursor.getString(6);
                int paymentMethod = cursor.getInt(7);
                double paymentBalance = cursor.getDouble(8);

                Transaction newPayment = new Transaction(paymentID, transactionType, amount, paymentDate, paymentDescription, paymentCategory, paymentMethod, paymentBalance);
                returnList.add(newPayment);
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(context, "Payment history is empty", Toast.LENGTH_SHORT).show();
        }

        // Close both cursor and the database upon completion.
        cursor.close();
        db.close();

        return returnList;
    }

    public Account getAccount() {
        databaseHelper = new DatabaseHelper(context);
        return databaseHelper.selectAccount(context, getDatabaseName());
    }

    private void extractNewDateFormat(Cursor cursor) {
        try {
            paymentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(cursor.getString(4));
        } catch (ParseException e) {
            // Handle the case where the string cannot be parsed into a date
            Calendar c = Calendar.getInstance();
            c.set(2000, 0, 1);
            paymentDate = c.getTime();
        }
    }

    private void extractOldDateFormat(Cursor cursor) {
        try {
            paymentDate = new SimpleDateFormat("(EEE, MMM d, yyyy)", Locale.getDefault()).parse(cursor.getString(3));
        } catch (ParseException e) {
            // Handle the case where the string cannot be parsed into a date
            Calendar c = Calendar.getInstance();
            c.set(2000, 0, 1);
            paymentDate = c.getTime();
        }
    }
}