package com.example.shafi.digitalizedrestaurant;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import MyObjects.Order;
import Adapters.OrderAdapter;

public class OrderDisplayActivity extends AppCompatActivity {
    private List<Order> orderList = new ArrayList<>();
    private List<String> keyList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private String uid, key, userType;
    private OrderAdapter orderAdapter;
    private RecyclerView recyclerView;
    private Order order;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);

        if (getIntent().getExtras() != null){
            key = getIntent().getExtras().getString("key");
            userType = getIntent().getExtras().getString("userType");
        }

        order = new Order();
        recyclerView = findViewById(R.id.kitchen_recycler_view);
        orderAdapter = new OrderAdapter(orderList, this, userType);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(orderAdapter);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
            uid = user.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("hotel/customer");

        mDatabase.child(key).child("orders").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue() != null){
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null){
                        orderList.add(order);
                        orderAdapter.notifyDataSetChanged();
                    }
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


    }
}