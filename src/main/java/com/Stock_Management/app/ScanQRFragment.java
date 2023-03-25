package com.navigation_bar.app;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;

public class ScanQRFragment extends Fragment {

    private static final String TAG = "TAG";
    CodeScanner codeScanner;
    CodeScannerView scanqr;
    TextView prodI;
    String msg,UserId,prodII,path;
    FirebaseDatabase database;
    FirebaseAuth fAuth;
    Integer cp = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan_q_r, container, false);
        scanqr = view.findViewById(R.id.scan);
        codeScanner = new CodeScanner(view.getContext(), scanqr);
        prodI = view.findViewById(R.id.prod_details);
        fAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        UserId = fAuth.getCurrentUser().getUid();

        DatabaseReference xRef = database.getReference(UserId).child("Categories/All products/");
        xRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
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

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prodII = result.getText().toString();

                        Log.i("prd",prodII);
                        Intent intent = new Intent(view.getContext(),AfterScanActivity.class);
                        intent.putExtra("prod2",prodII);
                        intent.putExtra("cp",String.valueOf(cp));
                        startActivity(intent);
                    }
                });
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        cam_request();
    }

    private void cam_request() {
        Dexter.withContext(getContext()).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                codeScanner.startPreview();
            }
            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(getContext(), "ENABLE THE CAMERA", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
}