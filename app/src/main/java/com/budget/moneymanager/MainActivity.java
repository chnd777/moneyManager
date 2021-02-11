package com.budget.moneymanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.*;
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navDrawer=findViewById(R.id.drawer_layout);
        bottomNavView=findViewById(R.id.bottomNav);
        navView =findViewById(R.id.navigview);

        ImageButton image;
        image =findViewById(R.id.drawerr);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navDrawer.openDrawer(Gravity.LEFT);
            }
        });

        recyclerView= findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation((LinearLayoutManager.VERTICAL));
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        llAuth = FirebaseAuth.getInstance();
        if( llAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            return;
        }
        uid=llAuth.getCurrentUser().getUid();
        mDataBase = FirebaseDatabase.getInstance().getReference("datalist").child(uid);
        FirebaseRecyclerOptions<ListData> options =
                new FirebaseRecyclerOptions.Builder<ListData>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("datalist").child(uid), ListData.class)
                        .build();
        adapter =new MainAdapter(options);

        recyclerView.setAdapter(adapter);

        navView.setNavigationItemSelectedListener((MenuItem item) -> {
            switch(item.getItemId()){
                case R.id.share:
                    Intent intentInvite = new Intent(Intent.ACTION_SEND);
                    intentInvite.setType("text/plain");
                    String body = "https://github.com/chnd777/moneyManager";
                    String subject = "Your Subject";
                    intentInvite.putExtra(Intent.EXTRA_SUBJECT, subject);
                    intentInvite.putExtra(Intent.EXTRA_TEXT, body);
                    startActivity(Intent.createChooser(intentInvite, "Share using"));
                    navDrawer.closeDrawer(Gravity.LEFT);
                    return false;
                case R.id.expenses:
                    startActivity(new Intent(getApplicationContext(), ExpensesActivity.class));
                    navDrawer.closeDrawer(Gravity.LEFT);
                    return false;
                case R.id.incomes:
                    startActivity(new Intent(getApplicationContext(), IncomeActivity.class));
                    navDrawer.closeDrawer(Gravity.LEFT);
                    return false;
                case R.id.logout:
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    navDrawer.closeDrawer(Gravity.LEFT);
                    return false;
            }
            return false;
        });
        bottomNavView.setOnNavigationItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.expense:
                    addExpense();
                    return false;
                case R.id.dashboard:
                    navDrawer.openDrawer(Gravity.LEFT);
                    return false;
                case R.id.income:
                    addIncome();
                    return false;
            }
            return false;
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
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
        CalendarView calenderr= mView.findViewById(R.id.expenseCalender);
        Date date = new Date();
        long msec =date.getTime();
        calenderr.setMaxDate(msec);
        calenderr.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // display the selected date by using a toast
                dat.setText(dayOfMonth+"/"+(month+1)+"/"+year);
            }
        });
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
            DatabaseReference expenseData=FirebaseDatabase.getInstance().getReference("expense").child(uid).child(id);
            ListData newitem =new ListData(amtt , re, da);
            dr.setValue(newitem);
            expenseData.setValue(newitem);
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
        CalendarView calender= mmView.findViewById(R.id.incomeCalender);
        Date date = new Date();
        long msec =date.getTime();
        calender.setMaxDate(msec);
        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // display the selected date by using a toast
                dat.setText(dayOfMonth+"/"+(month+1)+"/"+year);
            }
        });
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
                DatabaseReference data=FirebaseDatabase.getInstance().getReference("datalist").child(uid).child(id);
                DatabaseReference incomedata=FirebaseDatabase.getInstance().getReference("income").child(uid).child(id);
                ListData item =new ListData(amtt , re, da);
                data.setValue(item);
                incomedata.setValue(item);
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