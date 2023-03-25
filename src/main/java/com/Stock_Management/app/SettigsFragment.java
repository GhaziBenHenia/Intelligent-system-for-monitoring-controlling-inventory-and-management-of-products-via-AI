package com.navigation_bar.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SettigsFragment extends Fragment {
    EditText cap,new_name;
    Button apply1,apply2,button;
    FirebaseAuth fAuth;
    FirebaseDatabase database;
    String UserId,User;
    Integer cp = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settigs, container, false);
        cap = view.findViewById(R.id.cap);
        new_name = view.findViewById(R.id.new_name);
        apply1 = view.findViewById(R.id.apply1);
        apply2 = view.findViewById(R.id.apply2);
        button = view.findViewById(R.id.button);
        fAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        UserId = fAuth.getCurrentUser().getUid();

        apply1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                User = new_name.getText().toString().trim();
                if(TextUtils.isEmpty(User)) {
                    new_name.setError("Invalid Username");
                    return;}
                DatabaseReference Ref = database.getReference(UserId).child("Name");
                Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Ref.setValue(User).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getContext(), "Changes applied", Toast.LENGTH_SHORT).show();

                                }else{
                                    Toast.makeText(getContext(), "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }//onDataChange
                    @Override
                    public void onCancelled(DatabaseError error) {
                    }//onCancelled
                });
            }
        });
        DatabaseReference xRef = database.getReference(UserId).child("Categories/All products/");
        xRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                //***************************************************************************************
                if(task.getResult().exists()) {
                    xRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                int s = 0;
                                for (DataSnapshot d2 : dataSnapshot.getChildren()) {
                                    s++;
                                    cp = cp + Integer.parseInt(String.valueOf(d2.getValue()));
                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });
                }
            }
        });
        apply2.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                String c = cap.getText().toString().trim();
                if (TextUtils.isEmpty(c) ) {
                    cap.setError("Invalid Capacity");
                    return;
                }
                else if (cp > Integer.parseInt(c)){
                    cap.setError("Invalid Capacity");
                    return;
                }
                DatabaseReference newRef = database.getReference(UserId).child("capacity");
                newRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        newRef.setValue(Integer.parseInt(c)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getContext(), "Changes applied", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getContext(), "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }//onDataChange

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }//onCancelled
                });
            }

        });
        return view;

    }
    private void closeKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

}