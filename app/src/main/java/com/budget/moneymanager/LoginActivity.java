package com.budget.moneymanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import static android.text.TextUtils.isEmpty;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private FirebaseAuth lAuth;
    private ProgressDialog lDialog;
    private Switch switch1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lAuth= FirebaseAuth.getInstance();

        if( lAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            return;
        }
        setContentView(R.layout.activity_login);
        lDialog= new ProgressDialog(this);
        loginFun();
    }
    private void loginFun(){
        TextView createAccount = findViewById(R.id.newAccount);
        TextView resetPassword = findViewById(R.id.forgotPassword);
        createAccount.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), RegistrationActivity.class)));
        resetPassword.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),ResetPasswordActivity.class)));
        email = findViewById(R.id.lEmail);
        password = findViewById(R.id.lPassword);
        Button butlog = findViewById(R.id.login);
        switch1=findViewById(R.id.switch1);
        switch1.setOnClickListener(v -> {
            if(switch1.isChecked()){
                switch1.setChecked(true);
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                switch1.setChecked(false);
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        butlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sLEmail = email.getText().toString();
                String sLPassword = password.getText().toString();
                if (isEmpty(sLEmail)) {
                    email.setError("email required!..");
                    return;
                }
                if (sLPassword.length() < 6) {
                    password.setError("wrong Password");
                    return;
                }
                lDialog.setMessage("login processing..");
                lDialog.show();
                lAuth.signInWithEmailAndPassword(sLEmail, sLPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        lDialog.dismiss();
                        Toast.makeText(LoginActivity.this.getApplicationContext(), "Login successful..", Toast.LENGTH_SHORT).show();
                        LoginActivity.this.startActivity(new Intent(LoginActivity.this.getApplicationContext(), MainActivity.class));
                    } else {
                        lDialog.dismiss();
                        Toast.makeText(LoginActivity.this.getApplicationContext(), "login failed because"+task.getException(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Info");
            alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
            alertDialog.show();
            return false;
        }
        return true;
    }
    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}