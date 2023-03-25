package com.navigation_bar.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DashBoardFragment extends Fragment {
    ExpandableListView list;
    TextView user,prd,perc,qtt,qtt2,txt;
    FirebaseAuth fAuth;
    FirebaseDatabase database ;
    String msg,UserId,msg2,msg3,x;
    ProgressBar pb,pbload;
    Integer cp,c,cpx,n ;
    int p;
    ImageView home,warn;
    private int y;
    ArrayList<String> arrayList ;
    ArrayList<String> categories;
    HashMap<String,ArrayList<String>> listChild = new HashMap<>();
    MainAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dash_board, container, false);
        user = view.findViewById(R.id.User);
        prd = view.findViewById(R.id.prod_list);
        list = view.findViewById(R.id.listofcat);
        txt = view.findViewById(R.id.prod_list);
        pb = view.findViewById(R.id.pb);
        perc = view.findViewById(R.id.perc);
        qtt = view.findViewById(R.id.qtt);
        qtt2 = view.findViewById(R.id.qtt2);
        warn = view.findViewById(R.id.warnning);
        fAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        UserId = fAuth.getCurrentUser().getUid();
        cp = 0;
        c = 0;
        cpx = 0;
        msg2 = "Your stock is:\n";
        msg3 = "\n";

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
        DatabaseReference nRef = database.getReference(UserId).child("Name");
        nRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user.setText("User: "+ String.valueOf(dataSnapshot.getValue()));
            }//onDataChange
            @Override
            public void onCancelled(DatabaseError error) {
            }//onCancelled
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
                                p = (cp * 100) / n;
                                Log.i("p",String.valueOf(p));
                                qtt.setText("Number of products: " + String.valueOf(cp) + "/" + String.valueOf(n));
                                y = p;
                                pb.setProgress(y);
                                msg = "%" + String.valueOf(p) + " of the inventory is filled";
                                perc.setText(msg);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });
                }
            }
        });

        DatabaseReference myRef = database.getReference(UserId).child("Categories");
        myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if ((task.getResult().exists())) {
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                int i = 0;
                                categories = new ArrayList<>();
                                for(DataSnapshot d : dataSnapshot.getChildren()) {
                                    Log.i("d",d.getKey());
                                    myRef.child(d.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                int j = 0;
                                                arrayList = new ArrayList<>();
                                                cpx = 0;
                                                for (DataSnapshot d1 : dataSnapshot.getChildren()) {
                                                    j++;
                                                    c = Integer.parseInt(String.valueOf(d1.getValue()));
                                                    String ch = d1.getKey()+"\nQtt:("+c+")";
                                                    String z = "";
                                                    int x = 0;
                                                    for (int a = 0; a < ch.length(); a++) {
                                                        char p = ch.charAt(a);
                                                        if (p == '|' && x == 0){
                                                            z = z + '\n';
                                                            x++;
                                                        }
                                                        else {
                                                            z = z + p;
                                                        }
                                                    }
                                                    arrayList.add(z);
                                                    Log.i("d1",d1.getKey());
                                                    cpx = (cpx+c);
                                                    if ((c<=5) && (d.getKey().equals("All products"))){
                                                        msg3 = msg3 + d1.getKey()+"\nQtt:("+c+")\n\n";
                                                        warn.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                                listChild.put(d.getKey(), arrayList);
                                                cpx = (cpx * 100)/cp;
                                                msg2 = msg2 + "\n" + String.valueOf(cpx) + "% " + d.getKey();
                                                if(msg2.equals("Your stock is:\n\n100% All products")){
                                                    msg2 = "Your stock is:\n";
                                                }

                                            }
                                            Log.i("cpx",cpx.toString());
                                        }//onDataChange
                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                        }//onCancelled
                                    });
                                    i++;
                                    categories.add(d.getKey());
                                    //qtt2.setText("Number of Categories:" + String.valueOf(i));
                                }
                                adapter = new MainAdapter(categories,listChild);
                                list.setAdapter(adapter);
                                qtt2.setText("Number of Categories: " + String.valueOf(i-1));

                            }
                        }//onDataChange
                        @Override
                        public void onCancelled(DatabaseError error) {
                        }//onCancelled
                    });
                }
                else {
                    msg = "%0 YOUR INVENTORY IS EMPTY";
                    perc.setText(msg);
                    msg2 = "You don't have any products";
                    qtt2.setText("Number of Categories:" + "0");
                    qtt.setText("Number of products:" + "0" + "/" + String.valueOf(n));
                }
            }

        });
        warn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("These products are almost out of stock:\n"+msg3);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });

        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                if (msg2.equals("Your stock is:\n")){ x = "Set your categories with AI";}
                else {x=msg2;}
                builder.setMessage(x);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });
        return view;
    }
}