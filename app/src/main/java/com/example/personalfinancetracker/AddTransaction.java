package com.example.personalfinancetracker;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTransaction extends AppCompatActivity {
    private EditText amountEditText, descriptionEditText;
    private Spinner categorySpinner;
    private RadioGroup typeRadioGroup;
    private Button saveButton, trackExpenses, back;
    private DBhelper db;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.hide();
        }

        amountEditText = findViewById(R.id.amountEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        typeRadioGroup = findViewById(R.id.typeRadioGroup);
        saveButton = findViewById(R.id.saveButton);
        trackExpenses = findViewById(R.id.trackExpenses);
        back = findViewById(R.id.back);

        mAuth = FirebaseAuth.getInstance();

        db = new DBhelper(this);
        db.setUserTableName(mAuth.getUid());

        updateCategoryList();

        typeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> updateCategoryList());

        saveButton.setOnClickListener(v -> {
            boolean success = saveTransaction();
            if (success) {
                Intent intent = new Intent(AddTransaction.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        trackExpenses.setOnClickListener(v -> {
            Intent intent = new Intent(this, TransactionList.class);
            startActivity(intent);
        });

        back.setOnClickListener(v -> {
            finish();
        });
    }

    private boolean saveTransaction() {
        String amountString = amountEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        int selectedTypeId = typeRadioGroup.getCheckedRadioButtonId();
        String type = selectedTypeId == R.id.incomeRadioButton ? "Income" : "Expense";

        // Validate amount
        if (amountString.isEmpty()){
            Toast.makeText(this, "Enter Valid Amount!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate category
        if (category.isEmpty()){
            Toast.makeText(this, "Pick a Category!", Toast.LENGTH_SHORT).show();
            return false;
        }

        double amount = Double.parseDouble(amountString);

        if (amount <= 0) {
            Toast.makeText(this, "Invalid Amount", Toast.LENGTH_SHORT).show();
            return false;
        }

        double balance = db.getTotalIncome() - db.getTotalExpenses();

        if (amount > balance && type.equals("Expense")) {
            Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Get current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String date = sdf.format(new Date());

        // Create a new Transaction object and save to db
        Transaction transaction = new Transaction(amount, description, category, type, date);
        db.addTransaction(transaction);
        Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show();
        return true;
    }

    private void updateCategoryList() {
        int selectedTypeId = typeRadioGroup.getCheckedRadioButtonId();
        String[] categories;

        if (selectedTypeId == R.id.incomeRadioButton) {
            categories = getResources().getStringArray(R.array.income_array);
        } else if (selectedTypeId == R.id.expenseRadioButton) {
            categories = getResources().getStringArray(R.array.expense_array);
        } else {
            categories = getResources().getStringArray(R.array.null_array);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
    }
}
