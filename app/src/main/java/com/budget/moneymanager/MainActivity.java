package com.budget.moneymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.TextUtils.isEmpty;

public class MainActivity extends AppCompatActivity implements RecyclerInterface{
    private DrawerLayout navDrawer;
    private BottomNavigationView bottomNavView;
    private NavigationView navView;
    private RecyclerView recyclerView;
    MainAdapter adapter;
    TextView user;
    private FirebaseAuth llAuth;
    static String uid;
    Toast mToast;
    private DatabaseReference mDataBase;
    private DatabaseReference mData;
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
        llAuth = FirebaseAuth.getInstance();
        if( llAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            return;
        }
        uid=llAuth.getCurrentUser().getUid();
        user =(TextView) findViewById(R.id.username1);
        FirebaseDatabase.getInstance().getReference("userName").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name=snapshot.getValue().toString();
                Toast.makeText(MainActivity.this,"Hi "+name+"! welcome to money manager App",Toast.LENGTH_LONG).show();
                user.setText("Hello "+name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        user.setText("Hello guyzzz!");
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

        mDataBase = FirebaseDatabase.getInstance().getReference("datalist").child(uid);
        FirebaseRecyclerOptions<ListData> options =
                new FirebaseRecyclerOptions.Builder<ListData>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("datalist").child(uid), ListData.class)
                        .build();

        adapter =new MainAdapter(options ,this);
        //recyclerView.setOnLongClickListener();
        recyclerView.setAdapter(adapter);

        navView.setNavigationItemSelectedListener((MenuItem item) -> {
            switch(item.getItemId()){
                case R.id.share:
                    Intent intentInvite = new Intent(Intent.ACTION_SEND);
                    intentInvite.setType("text/plain");
                    String body = "https://github.com/chnd777/moneyManager/blob/master/Money-manager.apk?raw=true";
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
                    AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("Signout..!!!");
                    alertDialog.setMessage("Are you sure to signout from the app.!!");
                    alertDialog.setCancelable(true);
                    alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            navDrawer.closeDrawer(Gravity.LEFT);
                            Toast.makeText(MainActivity.this,"signed out Sucessfully ",Toast.LENGTH_LONG).show();
                        }
                    });
                    alertDialog.setNegativeButton("No",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog alertDialo = alertDialog.create();
                    alertDialo.show();
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
        //adapter.stopListening();
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
            String regex="^[0-3]?[0-9]/[0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher((CharSequence)da);
            if(isEmpty(re)){
                reason.setError("enter reason ");
                return;
            }
            if(isEmpty(amtt)){
                amt.setError("enter valid amount ");
                return;
            }
            if(!matcher.matches()){
                dat.setError("Invalid date select date below!!");
                return;
            }
            String id = mDataBase.push().getKey();
            DatabaseReference dr=FirebaseDatabase.getInstance().getReference("datalist").child(uid).child(id);
            DatabaseReference expenseData=FirebaseDatabase.getInstance().getReference("expense").child(uid).child(id);
            ListData newitem =new ListData(amtt , re, da);
            dr.setValue(newitem);
            expenseData.setValue(newitem);
            alert.dismiss();
            recyclerView.getLayoutManager().scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
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
                String regex="^[0-3]?[0-9]/[0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2}$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher((CharSequence)da);
                if(isEmpty(re)){
                    reason.setError("enter reason ");
                    return;
                }
                if(isEmpty(amtt)){
                    amt.setError("enter valid amount ");
                    return;
                }
                if(!matcher.matches()){
                    dat.setError("Invalid select below!!");
                    return;
                }
                String id=mDataBase.push().getKey();
                DatabaseReference data=FirebaseDatabase.getInstance().getReference("datalist").child(uid).child(id);
                DatabaseReference incomedata=FirebaseDatabase.getInstance().getReference("income").child(uid).child(id);
                ListData item =new ListData(amtt , re, da);
                data.setValue(item);
                incomedata.setValue(item);
                alert.dismiss();
                recyclerView.getLayoutManager().scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
            }
        });
    }
    public void showAToast (String message){
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        mToast.show();
    }
    @Override
    public void onBackPressed(){
        if(navDrawer.isDrawerOpen(Gravity.LEFT)){
            navDrawer.closeDrawer(Gravity.LEFT);
        }
        else{
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Exit App..!!!");
            alertDialog.setMessage("Do you want to exit the app..!!");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent a = new Intent(Intent.ACTION_MAIN);
                    a.addCategory(Intent.CATEGORY_HOME);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(a);
                    //Toast.makeText(MainActivity.this,"Sucessfully deleted!!",Toast.LENGTH_LONG).show();
                }
            });
            alertDialog.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog alertDialogg = alertDialog.create();
            alertDialogg.show();
        }
    }
    @Override
    public void onClick(int position) {
        showAToast("long click to delete");
        //Toast.makeText(MainActivity.this.getApplicationContext(), "Long click to delete ", Toast.LENGTH_SHORT).show();
    }
    @SuppressLint("WrongConstant")
    @Override
    public void onLongClick(int position) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Delete..!!!");
        alertDialog.setMessage("Are you sure to delete.!!");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        mData = FirebaseDatabase.getInstance().getReference("datalist").child(uid);
                        mData.addListenerForSingleValueEvent(new ValueEventListener() {
                            int i=0;
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds:snapshot.getChildren()){
                                    if(i==position){
                                        FirebaseDatabase.getInstance().getReference("datalist").child(uid).child(ds.getKey()).removeValue();
                                        FirebaseDatabase.getInstance().getReference("income").child(uid).child(ds.getKey()).removeValue();
                                        FirebaseDatabase.getInstance().getReference("expense").child(uid).child(ds.getKey()).removeValue();
                                        return;
                                    }
                                    i++;
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        Toast.makeText(MainActivity.this,"Sucessfully deleted!!",Toast.LENGTH_LONG).show();
                    }
                });
        alertDialog.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialo = alertDialog.create();
        alertDialo.show();
        //Toast.makeText(MainActivity.this.getApplicationContext(), "sucessfully deleted "+position,Toast.LENGTH_SHORT).show();
        return;
    }
}