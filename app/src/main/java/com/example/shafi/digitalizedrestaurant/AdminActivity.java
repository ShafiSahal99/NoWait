package com.example.shafi.digitalizedrestaurant;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import MyObjects.Hotel;
import MyObjects.Order;

public class AdminActivity extends AppCompatActivity {
    private Button btnHotelName, btnChairs, btnMenu, btnSales, btnPanelPassword;
    private EditText inputHotelName;
    private TextView txtHotelName, txtChairsCount;
    private DatabaseReference mDatabase;
    private String email, uid, userType;
    private Hotel hotel;
    FirebaseUser user;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        if (getIntent().getExtras() != null){
            userType = getIntent().getExtras().getString("userType");
        }

        btnHotelName = findViewById(R.id.hotel_name_button);
        btnChairs = findViewById(R.id.chairs_button);
        btnMenu = findViewById(R.id.menu_button);
        btnSales = findViewById(R.id.sales_button);

        txtHotelName = findViewById(R.id.hotel_name_txt);
        txtChairsCount = findViewById(R.id.chairs_count_txt);
        hotel = new Hotel();

        btnSales.setVisibility(View.VISIBLE);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            email = user.getEmail();
            uid = user.getUid();
        }


        btnHotelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogue("Enter Hotel Name", "Hotel Name", 0);
            }
        });

        btnChairs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogue("Enter Total Chair Count", "Chair Count", 1);
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, CategoryDisplayActivity.class);
                startActivity(intent);
            }
        });

        btnSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, DateDisplayActivity.class);
                intent.putExtra("userType", "admin");
                startActivity(intent);
            }
        });


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String hotelName = "";
                int chairCount = 0;
                if(dataSnapshot.getValue() != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        hotelName = ds.child("HotelName").getValue(String.class);
                        //if(ds.child("ChairCount").getValue() != null) {
                            try {
                                chairCount = ds.child("ChairCount").getValue(Integer.class);
                            }catch (Exception e){e.printStackTrace();}
                        //}

                    }
                }
                txtHotelName.setText(hotelName);
                txtChairsCount.setText("Total Chair Count : " + String.valueOf(chairCount));
                Log.d("mylog", "called");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("mylog", "loadPost:onCancelled", databaseError.toException());
            }
        };

        mDatabase.child("users").child(uid).addValueEventListener(valueEventListener);


    /*    mDatabase.child("users").child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    if (dataSnapshot.getValue() != null){
                         hotel = dataSnapshot.getValue(Hotel.class);

                        if (hotel != null) {
                            txtHotelName.setText(hotel.getHotelName());
//                            Log.d("mylog", hotel.getHotelName());
                            txtChairsCount.setText("Total Chair count : " + String.valueOf(hotel.getChairCount()));
                        }

                    }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    if (dataSnapshot.getValue() != null){
                        hotel = dataSnapshot.getValue(Hotel.class);

                        if (hotel != null) {
                            txtHotelName.setText(hotel.getHotelName());
                            Log.d("mylog", hotel.getHotelName());
                            txtChairsCount.setText("Total Chair count : " + String.valueOf(hotel.getChairCount()));
                        }

                    }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }



    public void writeHotelName(Hotel hotel, int mode){
        String message = "";
        Map<String,Object> hotelMap = new HashMap<>();

        hotelMap.put("Hotel Name", hotel.getHotelName());
        hotelMap.put("Chair Count", hotel.getChairCount());

      //  mDatabase.child("users").child(uid).child("hotel").updateChildren(hotelMap);




        if (mode == 0) {
            message = "Hotel Name Set";
            mDatabase.child("users").child(uid).child("hotel").child("HotelName").setValue(hotel.getHotelName());
        }
        else if (mode == 1) {
            message = "Total Chair count set";
            mDatabase.child("users").child(uid).child("hotel").child("ChairCount").setValue(hotel.getChairCount());
        }
        Toast.makeText(AdminActivity.this, message, Toast.LENGTH_SHORT).show();

    }

    public void showDialogue(String message, String title, final int whichDialogue){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);

        if (whichDialogue == 1)
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        editText.setMaxLines(1);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setView(editText);

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               String text = editText.getText().toString();
               int mode = 0;

               if (whichDialogue == 0){
                   hotel.setHotelName(text);
                   hotel.setChairCount(hotel.getChairCount());
               } else if (whichDialogue == 1){
                 //  editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                   //editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
                   final int hotelChairCount = Integer.parseInt(text);
                   hotel.setChairCount(hotelChairCount);
                   hotel.setHotelName(hotel.getHotelName());
                   mode = 1;
               }

               writeHotelName(hotel, mode);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }



}
