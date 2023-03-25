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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {
    EditText xemail;
    Button xreset;
    FirebaseAuth fAuth;
    ProgressBar xprogressBar;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        xemail = findViewById(R.id.email);
        xreset = findViewById(R.id.resetbtn);
        fAuth = FirebaseAuth.getInstance();
        xprogressBar = findViewById(R.id.progressBar2);
        back = findViewById(R.id.imageView2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
        xreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                closeKeyboard();
                resetpassword();
            }
        });
    }
    private void resetpassword(){
        String email = xemail.getText().toString().trim();
        if(TextUtils.isEmpty(email)) {
            xemail.setError("Invalid eamil");
            xemail.requestFocus();
            return;}
        xprogressBar.setVisibility(View.VISIBLE);
        fAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ResetActivity.this,"Check your email to reset your password!", Toast.LENGTH_LONG).show();
                    xprogressBar.setVisibility(View.GONE);
                }
                else{
                    Toast.makeText(ResetActivity.this,"Try Again!", Toast.LENGTH_LONG).show();
                    xprogressBar.setVisibility(View.GONE);
                }
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