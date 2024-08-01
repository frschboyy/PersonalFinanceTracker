package com.example.personalfinancetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBhelper extends SQLiteOpenHelper {
    private static final String DB_Name = "finance.db";
    private static final int DB_Version = 1;

    private String transact = "transactions";
    private static final String id = "id";
    private static final String amount = "amount";
    private static final String desc = "description";
    private static final String date = "date";
    private static final String category = "category";
    private static final String type = "type";

    public DBhelper(Context context) {
        super(context, DB_Name, null, DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Initial table creation is not needed here as tables are user-specific
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + transact);
        onCreate(db);
    }

    private String getUserTableName() {
        return transact;
    }

    public void setUserTableName(String uid){
        transact = "transactions_" + uid;
        createUserTable();
    }

    private void createUserTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String createUserTableQuery = "CREATE TABLE IF NOT EXISTS " + transact + "("
                + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + amount + " REAL, "
                + desc + " TEXT, "
                + type + " TEXT,"
                + category + " TEXT, "
                + date + " TEXT)";
        db.execSQL(createUserTableQuery);
    }


    public void addTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(amount, transaction.getAmount());
        values.put(desc, transaction.getDescription());
        values.put(date, transaction.getDate());
        values.put(category, transaction.getCategory());
        values.put(type, transaction.getType());

        db.insert(transact, null, values);
        db.close();
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        // Order by date in ascending order
        String selectQuery = "SELECT * FROM " + transact + " ORDER BY " + date + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction(
                        cursor.getDouble(cursor.getColumnIndex(amount)),
                        cursor.getString(cursor.getColumnIndex(desc)),
                        cursor.getString(cursor.getColumnIndex(category)),
                        cursor.getString(cursor.getColumnIndex(type)),
                        cursor.getString(cursor.getColumnIndex(date))
                );
                transaction.setId(cursor.getInt(cursor.getColumnIndex(id)));
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return transactions;
    }

    public double getTotalIncome() {
        return getTotalAmountByType("Income");
    }

    public double getTotalExpenses() {
        return getTotalAmountByType("Expense");
    }

    private double getTotalAmountByType(String type) {
        double total = 0;
        String selectQuery = "SELECT SUM(" + amount + ") as total FROM " + transact + " WHERE " + DBhelper.type + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{type});

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndex("total"));
        }
        cursor.close();
        db.close();
        return total;
    }
}

