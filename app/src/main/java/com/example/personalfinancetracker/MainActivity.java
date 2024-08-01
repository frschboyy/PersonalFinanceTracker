package com.example.personalfinancetracker;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private TextView totalIncome, totalExpenses, balance;
    private Button newTransaction, trackExpenses;
    private ImageView logout;
    private DBhelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.hide();
        }

        mAuth = FirebaseAuth.getInstance();
        db = new DBhelper(this);
        db.setUserTableName(mAuth.getUid());

        logout = findViewById(R.id.logout);
        totalIncome = findViewById(R.id.income);
        totalExpenses = findViewById(R.id.expense);
        balance = findViewById(R.id.balance);
        newTransaction = findViewById(R.id.newTransaction);
        trackExpenses = findViewById(R.id.trackExpenses);

        // Load summary data
        loadSummaryData();

        newTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransaction.class);
            startActivity(intent);
        });

        trackExpenses.setOnClickListener(v -> {
            Intent intent = new Intent(this, TransactionList.class);
            startActivity(intent);
        });

        logout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, Login.class));
            finish();
        });
    }

    private void loadSummaryData() {
        // Calculate and display total income, expenses, and balance
        double income = db.getTotalIncome();
        double expenses = db.getTotalExpenses();
        double balanceAmount = income - expenses;

        totalIncome.setText(String.valueOf(income));
        totalExpenses.setText(String.valueOf(expenses));
        balance.setText(String.valueOf(balanceAmount));
    }
}
