package com.budget.moneymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ExpensesActivity extends AppCompatActivity implements RecyclerInterface{
    RecyclerView expenseRecyclerView;
    private FirebaseAuth eAuth;
    DatabaseReference mExpenseData;
    private String uid;
    MainAdapter expenseAdapter;
    Toast mToast;
    private DatabaseReference mData;
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
        expenseAdapter =new MainAdapter(options,this);
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
        //expenseAdapter.stopListening();
    }
    public void showAToast (String message){
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        mToast.show();
    }
    @Override
    public void onClick(int position) {
        showAToast("Long click to delete..!!");
        //Toast.makeText(ExpensesActivity.this.getApplicationContext(), "Long click to delete ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongClick(int position) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(ExpensesActivity.this);
        alertDialog.setTitle("Delete..!!!");
        alertDialog.setMessage("Are you sure to delete.!!");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                mData = FirebaseDatabase.getInstance().getReference("expense").child(uid);
                mData.addListenerForSingleValueEvent(new ValueEventListener() {
                    int i=0;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            if(i==position){
                                FirebaseDatabase.getInstance().getReference("expense").child(uid).child(ds.getKey()).removeValue();
                                FirebaseDatabase.getInstance().getReference("datalist").child(uid).child(ds.getKey()).removeValue();
                                //FirebaseDatabase.getInstance().getReference("expense").child(uid).child(ds.getKey()).removeValue();
                                return;
                            }
                            i++;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Toast.makeText(ExpensesActivity.this.getApplicationContext(), "sucessfully deleted ",Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialogg = alertDialog.create();
        alertDialogg.show();
        //Toast.makeText(MainActivity.this.getApplicationContext(), "sucessfully deleted "+position,Toast.LENGTH_SHORT).show();
        return;

    }
}