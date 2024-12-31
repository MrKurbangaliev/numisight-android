package com.iremia.app.numisight.dbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.iremia.app.numisight.model.Account;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String ACCOUNTS_LIST = "Accounts";
    public static final String COLUMN_ID = "AccountId";
    public static final String COLUMN_TITLE = "Title";
    public static final String COLUMN_DATABASE_TITLE = "DatabaseName";
    public static final String COLUMN_BALANCE = "Balance";
    public static final String COLUMN_CURRENCY = "CurrencyCode";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "accounts.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + ACCOUNTS_LIST
                + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " TEXT, " + COLUMN_DATABASE_TITLE + " TEXT, "
                + COLUMN_CURRENCY + " TEXT, "
                + COLUMN_BALANCE + " DOUBLE);";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, String.valueOf(account.getAccountLabel()));
        cv.put(COLUMN_DATABASE_TITLE, String.valueOf(account.getDbReference()));
        cv.put(COLUMN_CURRENCY, account.getCurrency());
        cv.put(COLUMN_BALANCE, account.getBalance());

        long insert = db.insert(ACCOUNTS_LIST, null, cv);
        return insert != -1;
    }

    public void editAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + ACCOUNTS_LIST + " SET " + COLUMN_TITLE + "=" + "'" + account.getAccountLabel() + "'" + " WHERE " + COLUMN_ID + "=" + account.getAccountId() + ";";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        cursor.close();
        db.close();
    }

    public void deleteAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + ACCOUNTS_LIST + " WHERE " + COLUMN_ID + "=" + account.getAccountId() + ";";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        cursor.close();
        db.close();
    }

    public boolean deleteAccountDbFile(Context context, String dbFileName) {
        return context.deleteDatabase(dbFileName);
    }

    public Account selectAccount(Context context, String dbFileName) {
        Account selectedAccount = null;

        String query = "SELECT * FROM " + ACCOUNTS_LIST + " WHERE " + COLUMN_DATABASE_TITLE + " = '" + dbFileName + "';";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            int accountId = cursor.getInt(0);
            String accountName = cursor.getString(1);
            String dbName = cursor.getString(2);
            String currency = cursor.getString(3);
            double balance = cursor.getDouble(4);

            selectedAccount = new Account(accountId, accountName, dbName, currency, balance);
        } else {
            Toast.makeText(context, "Error in AccountDBHelper.selectAccount()", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();

        return selectedAccount;
    }

    public void updateBalance(Account account) {
        String query = "UPDATE " + ACCOUNTS_LIST + " SET " + COLUMN_BALANCE + " = " + account.getBalance() + " WHERE " + COLUMN_ID + " = " + account.getAccountId() + ";";

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        cursor.close();
        db.close();
    }

    public ArrayList<Account> getAccountsList(Context context) {
        ArrayList<Account> returnList = new ArrayList<>();

        // Get data from the database.
        String query = "SELECT * FROM " + ACCOUNTS_LIST;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            // Loop through the result set and create new account objects. Put them into returnList.
            do {
                int accountId = cursor.getInt(0);
                String accountName = cursor.getString(1);
                String dbName = cursor.getString(2);
                String currency = cursor.getString(3);
                double balance = cursor.getDouble(4);

                Account account = new Account(accountId, accountName, dbName, currency, balance);
                returnList.add(account);
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(context, "Accounts list is empty", Toast.LENGTH_SHORT).show();
        }

        // Close both cursor and the database upon completion.
        cursor.close();
        db.close();

        return returnList;
    }
}