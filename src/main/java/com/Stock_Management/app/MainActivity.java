package com.navigation_bar.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    MeowBottomNavigation bot_nav;
    FirebaseAuth fAuth;
    Integer c;
    String UserId;
    FirebaseDatabase database ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fAuth = FirebaseAuth.getInstance();
        fAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        bot_nav = findViewById(R.id.bot_nav);
        if(fAuth.getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();}
        else {
            UserId = fAuth.getCurrentUser().getUid();
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
                                    for(DataSnapshot d : dataSnapshot.getChildren()) {
                                        myRef.child(d.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    int j = 0;
                                                    for (DataSnapshot d1 : dataSnapshot.getChildren()) {
                                                        j++;
                                                        c = Integer.parseInt(String.valueOf(d1.getValue()));
                                                    }
                                                }
                                            }//onDataChange
                                            @Override
                                            public void onCancelled(DatabaseError error) {
                                            }//onCancelled
                                        });
                                        i++;
                                    }
                                }
                            }//onDataChange
                            @Override
                            public void onCancelled(DatabaseError error) {
                            }//onCancelled
                        });
                    }
                }
            });
        }
        bot_nav.add(new MeowBottomNavigation.Model(1,R.drawable.ic_dashboard));
        bot_nav.add(new MeowBottomNavigation.Model(2,R.drawable.ic_qr_code));
        bot_nav.add(new MeowBottomNavigation.Model(3,R.drawable.ic_qr_code_scanner));
        bot_nav.add(new MeowBottomNavigation.Model(4,R.drawable.ic_settings));

        bot_nav.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                Fragment fragment = null;

                switch (item.getId()){
                    case 1:
                        fragment = new DashBoardFragment();
                        break;
                    case 2:
                        fragment = new GenerateQRFragment();
                        break;
                    case 3:
                        fragment = new ScanQRFragment();
                        break;
                    case 4:
                        fragment = new SettigsFragment();
                        break;
                }

                loadFragment(fragment);
            }
        });
        bot_nav.show(1,true);

        bot_nav.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {

            }
        });
        bot_nav.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });
    }

    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout,fragment).commit();
    }
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }
}