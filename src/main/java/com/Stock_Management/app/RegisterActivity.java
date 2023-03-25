package com.navigation_bar.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText xemail,xpassword,cap,name;
    Button xregister;
    TextView xLogin;
    FirebaseAuth fAuth;
    FirebaseDatabase database;
    ProgressBar xprogressBar;
    String UserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        xemail = findViewById(R.id.email);
        xpassword = findViewById(R.id.password);
        xregister = findViewById(R.id.registerb);
        xLogin = findViewById(R.id.ctext);
        cap = findViewById(R.id.cap);
        name = findViewById(R.id.Name);
        fAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        xprogressBar = findViewById(R.id.progressBar);
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        xregister.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                String email = xemail.getText().toString().trim();
                String password = xpassword.getText().toString().trim();
                String c = cap.getText().toString().trim();
                String User = name.getText().toString().trim();
                if(TextUtils.isEmpty(User)){
                    name.setError("Invalid User Name");
                    return;}
                if(TextUtils.isEmpty(email)) {
                    xemail.setError("Invalid email");
                    return;}
                if(TextUtils.isEmpty(password)) {
                    xpassword.setError("Invalid password");
                    return;}
                if(password.length() < 8){
                    xpassword.setError("password must be at least 8 characters long");
                    return;}
                if(TextUtils.isEmpty(c)){
                    cap.setError("Invalid capacity");
                    return;}
                xprogressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Account created ", Toast.LENGTH_SHORT).show();
                            UserId = fAuth.getCurrentUser().getUid();
                            DatabaseReference myRef = database.getReference(UserId);
                            myRef.child("capacity").setValue(Integer.parseInt(c));
                            myRef.child("Name").setValue(User);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            xprogressBar.setVisibility(View.GONE);
                        }else {
                            Toast.makeText(RegisterActivity.this, "ERROR" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            xprogressBar.setVisibility(View.GONE);
                        }
                    }
                });


            }
        });
        xLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }
    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
}