package com.example.personalfinancetracker;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class TransactionList extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private RecyclerView transactionRecyclerView;
    private TransactionAdapter transactionAdapter;
    private DBhelper db;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        mAuth = FirebaseAuth.getInstance();

        back = findViewById(R.id.backButton);
        transactionRecyclerView = findViewById(R.id.transactionRecyclerView);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = new DBhelper(this);
        db.setUserTableName(mAuth.getUid());

        List<Transaction> transactionList = db.getAllTransactions();

        transactionAdapter = new TransactionAdapter(transactionList);
        transactionRecyclerView.setAdapter(transactionAdapter);

        back.setOnClickListener(v -> {
            finish();
        });
    }
}
