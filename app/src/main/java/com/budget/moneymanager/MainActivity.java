package com.budget.moneymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.text.TextUtils.isEmpty;



public class MainActivity extends AppCompatActivity {
    private DrawerLayout navDrawer;
    private BottomNavigationView bottomNavView;
    private NavigationView navView;
    private RecyclerView recyclerView;
    ArrayList<ListData> myList = new ArrayList<>();
    MainAdapter adapter;
    private FirebaseAuth llAuth;
    String uid;
    DatabaseReference mDataBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navDrawer=findViewById(R.id.drawer_layout);
        bottomNavView=findViewById(R.id.bottomNav);
        navView =findViewById(R.id.navigview);

        recyclerView= findViewById(R.id.recyclerView);
        adapter =new MainAdapter( myList);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation((LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        llAuth = FirebaseAuth.getInstance();
        if( llAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
        uid=llAuth.getCurrentUser().getUid();
        mDataBase = FirebaseDatabase.getInstance().getReference("datalist").child(uid);
        navView.setNavigationItemSelectedListener((MenuItem item) -> {
            switch(item.getItemId()){
                case R.id.logout:
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    return true;
            }
            return true;
        });
        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.expense:
                        addExpense();
                        break;
                    case R.id.dashboard:
                        navDrawer.openDrawer(Gravity.LEFT);
                        break;
                    case R.id.income:
                        addIncome();
                        break;
                }
                return false;
            }
        });
    }
    private void addExpense(){
        AlertDialog alert=new AlertDialog.Builder(MainActivity.this).create();
        View mView =getLayoutInflater().inflate(R.layout.add_expenses,null);
        alert.setView(mView);
        alert.show();

        EditText reason= mView.findViewById(R.id.reasonexpense);
        EditText amt=mView.findViewById(R.id.expenseamount);
        EditText dat=mView.findViewById(R.id.dateofexpense);
        Button savee= mView.findViewById(R.id.expensebutton);
        savee.setOnClickListener(v -> {
            String re=reason.getText().toString().trim();
            String amtt = amt.getText().toString().trim();
            String da = dat.getText().toString().trim();
            if(isEmpty(re)){
                reason.setError("enter reason ");
                return;
            }
            if(isEmpty(amtt)){
                amt.setError("enter valid amount ");
                return;
            }
            if(isEmpty(da)){
                dat.setError("please enter date ");
                return;
            }
            String id=mDataBase.push().getKey();
            DatabaseReference dr=FirebaseDatabase.getInstance().getReference("datalist").child(uid).child(id);
            ListData newitem =new ListData(amtt , re, da);
            dr.setValue(newitem);
            myList.add(0,newitem);
            adapter.notifyDataSetChanged();

            alert.dismiss();
        });

    }
    private void addIncome(){
        AlertDialog alert=new AlertDialog.Builder(MainActivity.this).create();
        View mmView =getLayoutInflater().inflate(R.layout.add_income,null);
        alert.setView(mmView);
        alert.show();
        EditText reason=mmView.findViewById(R.id.reasonrevenue);
        EditText amt=mmView.findViewById(R.id.incomeamount);
        EditText dat=mmView.findViewById(R.id.dateofincome);
        Button savee= mmView.findViewById(R.id.incomebutton);
        savee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String re = reason.getText().toString().trim();
                String amtt = amt.getText().toString().trim();
                String da = dat.getText().toString().trim();
                if(isEmpty(re)){
                    reason.setError("enter reason ");
                    return;
                }
                if(isEmpty(amtt)){
                    amt.setError("enter valid amount ");
                    return;
                }
                if(isEmpty(da)){
                    dat.setError("please enter date ");
                    return;
                }
                String id=mDataBase.push().getKey();
                DatabaseReference dr=FirebaseDatabase.getInstance().getReference("datalist").child(uid).child(id);
                ListData newitemm =new ListData(amtt , re, da);
                dr.setValue(newitemm);
                myList.add(0,newitemm);
                adapter.notifyDataSetChanged();
                alert.dismiss();
            }
        });
    }
    @Override
    public void onBackPressed(){
        if(navDrawer.isDrawerOpen(Gravity.LEFT)){
            navDrawer.closeDrawer(Gravity.LEFT);
        }
        else{
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }
}