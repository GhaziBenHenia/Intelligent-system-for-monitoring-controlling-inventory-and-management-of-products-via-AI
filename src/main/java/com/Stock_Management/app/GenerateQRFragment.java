package com.navigation_bar.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


public class GenerateQRFragment extends Fragment {

    EditText ref,brand,price,date;
    Button generate;
    ImageView qrcode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_generate_q_r, container, false);
        ref = view.findViewById(R.id.ref);
        brand = view.findViewById(R.id.brand);
        price = view.findViewById(R.id.price);
        generate = view.findViewById(R.id.generate);
        date = view.findViewById(R.id.date);
        qrcode = view.findViewById(R.id.qrcode);

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                String data1 = ref.getText().toString().trim();
                String data2 = brand.getText().toString();
                String data3 = price.getText().toString();
                String data4 = date.getText().toString();
                String data = "Name: " + data1+" |Brand: "+data2+" | Exp_D: "+ data4+" | Price: "+data3+"|";
                if(TextUtils.isEmpty(data1)) {
                    ref.setError("Invalid");
                    return;}
                //**generate_qr**
                MultiFormatWriter mWriter = new MultiFormatWriter();
                try {
                    BitMatrix mMatrix = mWriter.encode(data, BarcodeFormat.QR_CODE, 300, 300);
                    BarcodeEncoder mEncoder = new BarcodeEncoder();
                    Bitmap mBitmap = mEncoder.createBitmap(mMatrix);
                    qrcode.setImageBitmap(mBitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
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