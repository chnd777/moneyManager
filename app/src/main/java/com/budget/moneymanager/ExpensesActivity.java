package com.budget.moneymanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ExpensesActivity extends AppCompatActivity {
    RecyclerView expenseRecyclerView;
    private FirebaseAuth eAuth;
    DatabaseReference mExpenseData;
    private String uid;
    MainAdapter expenseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        eAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageButton image;
        image =findViewById(R.id.back);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpensesActivity.super.onBackPressed();
            }
        });
        expenseRecyclerView = findViewById(R.id.expenseRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation((LinearLayoutManager.VERTICAL));
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        expenseRecyclerView.setLayoutManager(layoutManager);
        uid=eAuth.getCurrentUser().getUid();
        mExpenseData = FirebaseDatabase.getInstance().getReference("expense").child(uid);
        FirebaseRecyclerOptions<ListData> options =
                new FirebaseRecyclerOptions.Builder<ListData>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("expense").child(uid), ListData.class)
                        .build();
        expenseAdapter =new MainAdapter(options);
        expenseRecyclerView.setAdapter(expenseAdapter);
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        expenseAdapter.startListening();
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        expenseAdapter.stopListening();
    }
}