package com.budget.moneymanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class IncomeActivity extends AppCompatActivity {
    RecyclerView incomeRecyclerView;
    private FirebaseAuth iAuth;
    DatabaseReference mIncomeData;
    private String uid;
    MainAdapter incomeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageButton image;
        image =findViewById(R.id.back);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IncomeActivity.super.onBackPressed();
            }
        });
        iAuth = FirebaseAuth.getInstance();
        incomeRecyclerView = findViewById(R.id.incomeRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation((LinearLayoutManager.VERTICAL));
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        incomeRecyclerView.setLayoutManager(layoutManager);
        uid=iAuth.getCurrentUser().getUid();
        mIncomeData = FirebaseDatabase.getInstance().getReference("income").child(uid);
        FirebaseRecyclerOptions<ListData> options =
                new FirebaseRecyclerOptions.Builder<ListData>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("income").child(uid), ListData.class)
                        .build();
        incomeAdapter =new MainAdapter(options);
        incomeRecyclerView.setAdapter(incomeAdapter);

    }
    @Override
    protected void onStart()
    {
        super.onStart();
        incomeAdapter.startListening();
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        incomeAdapter.stopListening();
    }
}