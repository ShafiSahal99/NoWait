package com.example.shafi.digitalizedrestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import Adapters.OrderAdapter;
import MyObjects.Order;

public class BillDisplayActivity extends AppCompatActivity {
    private List<Order> orderList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private OrderAdapter orderAdapter;
    private String uid, key, userType;
    private TextView txtBill;
    private Button btnPaid;
    private int bill = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_display);

        if (getIntent().getExtras() != null){
            key = getIntent().getExtras().getString("key");
            userType = getIntent().getExtras().getString("userType");
        }

        recyclerView = findViewById(R.id.bill_counter_recycler_view);
        txtBill = findViewById(R.id.bill_counter_total_amount_txt);
        btnPaid = findViewById(R.id.paid_button);

        orderAdapter = new OrderAdapter(orderList, this, userType);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(orderAdapter);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            uid = user.getUid();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("hotel/counter");

        btnPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(key).removeValue();
                onBackPressed();
            }
        });

        mDatabase.child(key).child("orders").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue() != null){
                    Order order = dataSnapshot.getValue(Order.class);
                    if(order != null) {
                        orderList.add(order);
                        bill += order.getPrice() * order.getQuantity();
                        orderAdapter.notifyDataSetChanged();
                    }
                    txtBill.setText("Total Amount : Rs " + bill);

                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
        });



        /*for(int i = 0; i < orderList.size(); i++){
            bill += (orderList.get(i).getPrice()*orderList.get(i).getQuantity());
            Log.d("mylog", orderList.get(i).getName());
        }*/


    }
}
