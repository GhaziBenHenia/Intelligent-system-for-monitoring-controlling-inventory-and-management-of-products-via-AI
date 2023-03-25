package com.navigation_bar.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethod;
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

public class LoginActivity extends AppCompatActivity {
    EditText xemail,xpassword;
    Button xloginbtn;
    TextView xregister,reset;
    FirebaseAuth fAuth;
    ProgressBar xprogressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        xemail = findViewById(R.id.email);
        xpassword = findViewById(R.id.password);
        xloginbtn = findViewById(R.id.loginbtn);
        xregister = findViewById(R.id.registerbtn2);
        reset = findViewById(R.id.reset);
        fAuth = FirebaseAuth.getInstance();
        xprogressBar = findViewById(R.id.progressBar2);

        xloginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = xemail.getText().toString().trim();
                String password = xpassword.getText().toString().trim();
                if(TextUtils.isEmpty(email)) {
                    xemail.setError("Invalid eamil");
                    return;}
                if(TextUtils.isEmpty(password)) {
                    xpassword.setError("Invalid password");
                    return;}
                if(password.length() < 8){
                    xpassword.setError("password must be at least 8 caracters long");
                    return;}
                xprogressBar.setVisibility(View.VISIBLE);
                closeKeyboard();

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            xprogressBar.setVisibility(View.GONE);
                        }else{
                            Toast.makeText(LoginActivity.this, "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            xprogressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        xregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ResetActivity.class));
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