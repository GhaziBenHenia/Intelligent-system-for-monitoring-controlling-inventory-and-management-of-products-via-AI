package com.navigation_bar.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AfterScanActivity extends AppCompatActivity {
    private static final String TAG = "TAG";

    TextView prodI;
    ImageView addbtn,delbtn,canbtn;
    String msg,UserId,prodII,path,b,x;
    FirebaseAuth fAuth;
    FirebaseDatabase database ;
    Integer cp = 0,n =0;
    EditText plus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_scan);
        delbtn = findViewById(R.id.delbtn);
        addbtn = findViewById(R.id.addbtn);
        canbtn = findViewById(R.id.canbtn);
        prodI = findViewById(R.id.prod_details);
        fAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        UserId = fAuth.getCurrentUser().getUid();
        plus = findViewById(R.id.plus);
        prodII = getIntent().getStringExtra("prod2");
        x = getIntent().getStringExtra("cp");
        cp = Integer.parseInt(x);

        msg="";
        for (int i = 0; i < prodII.length(); i++) {
            char c = prodII.charAt(i);

            if (c == '|'){
                msg = msg + '\n';
            }
            else {
                msg = msg + c;
            }
        }
        msg = "\nProduct details:\n\n" + msg;

        path = UserId + "/Categories/All products/" + prodII;

        prodI.setText("Product scanned successfully"+ "\n" +msg);


        database = FirebaseDatabase.getInstance();
        DatabaseReference Ref = database.getReference(UserId).child("capacity");
        Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                n = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                Log.i("n",n.toString());
            }//onDataChange
            @Override
            public void onCancelled(DatabaseError error) {
            }//onCancelled
        });

        canbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                String b = plus.getText().toString().trim();

                if(TextUtils.isEmpty(b)) {
                    plus.setError("Invalid");
                    return;}
                if ((cp + Integer.parseInt(b)) <= n){

                    DatabaseReference myRef = database.getReference(path);
                    myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!(task.getResult().exists()) && (Integer.parseInt(b) <= n ) ) {
                                myRef.setValue(Integer.parseInt(b));
                                cp = cp + Integer.parseInt(b);
                                AlertDialog.Builder builder = new AlertDialog.Builder(AfterScanActivity.this);
                                builder.setMessage("Product is added successfully");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                            }
                            else {
                                myRef.setValue((Integer.parseInt(String.valueOf(task.getResult().getValue())) + Integer.parseInt(b)));
                                cp = cp + Integer.parseInt(b);
                                AlertDialog.Builder builder = new AlertDialog.Builder(AfterScanActivity.this);
                                builder.setMessage("Product is added successfully" );
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                            }
                        }
                    });
                }
                else if ((cp + Integer.parseInt(b)) > n) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AfterScanActivity.this);
                    builder.setMessage("Your don't have enough space");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                }
                else if (cp >= n) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AfterScanActivity.this);
                    builder.setMessage("Your inventory is full");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                }
            }
        });

        delbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                b = plus.getText().toString().trim();
                if(TextUtils.isEmpty(b)) {
                    plus.setError("Invalid");
                    return;}
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(path);
                myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!(task.getResult().exists())) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AfterScanActivity.this);
                            builder.setMessage("This Product does not exist in you inventory" );
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                        } else if (Integer.parseInt(String.valueOf(task.getResult().getValue())) == 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(AfterScanActivity.this);
                            builder.setMessage("This Product is no longer available" );
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                        }
                        else if (Integer.parseInt(String.valueOf(task.getResult().getValue())) < Integer.parseInt(b) ){
                            AlertDialog.Builder builder = new AlertDialog.Builder(AfterScanActivity.this);
                            builder.setMessage("You don't have enough of this product in your inventory");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                        }
                        else {
                            myRef.setValue((Integer.parseInt(String.valueOf(task.getResult().getValue())) - Integer.parseInt(b)));
                            AlertDialog.Builder builder = new AlertDialog.Builder(AfterScanActivity.this);
                            builder.setMessage("Product is deleted successfully" );
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    dialogInterface.dismiss();
                                }
                            }).show();
                        }
                    }
                });
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