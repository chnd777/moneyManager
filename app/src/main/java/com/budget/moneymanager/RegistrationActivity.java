package com.budget.moneymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;

import static android.text.TextUtils.isEmpty;

public class RegistrationActivity extends AppCompatActivity {

    private EditText email;
    private EditText name;
    private EditText password;
    private EditText confirmPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog rDialog;
    private Switch switchr;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth= FirebaseAuth.getInstance();
        rDialog= new ProgressDialog(this);
        registration();
    }
    private void registration() {
        email = findViewById(R.id.REmail);
        name = findViewById(R.id.RName);
        password = findViewById(R.id.RPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        Button signUp;
        signUp = findViewById(R.id.Signup);

        switchr=findViewById(R.id.switchr);
        switchr.setOnClickListener(v -> {
            if(switchr.isChecked()){
                switchr.setChecked(true);
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                switchr.setChecked(false);
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        });
        signUp.setOnClickListener(view -> {
            String rmemail = email.getText().toString();
            username = name.getText().toString();
            String rmpassword = password.getText().toString();
            String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.]+";
            String mconfirmPassword = confirmPassword.getText().toString();
            if (!rmemail.matches(emailPattern)) {
                email.setError("Invalid email address");
                return;
            }
            if (isEmpty(username)) {
                name.setError("name required..");
                return;
            }
            if (rmpassword.length() < 6) {
                password.setError("minimum 6 charecters");
                return;
            }
            if (!rmpassword.equals(mconfirmPassword)) {
                confirmPassword.setError("passwords not matched!");
                return;
            }
            rDialog.setMessage("processing..");
            rDialog.show();
            mAuth.createUserWithEmailAndPassword(rmemail,rmpassword).addOnCompleteListener(RegistrationActivity.this, task -> {

                if(task.isSuccessful()){
                    rDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Registration successful..",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
                else{
                    rDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Registration failed!..",Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}