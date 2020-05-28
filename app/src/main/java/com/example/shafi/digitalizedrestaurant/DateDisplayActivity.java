package com.example.shafi.digitalizedrestaurant;

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

import Adapters.CategoryAdapter;
import Adapters.DateAdapter;
import MyObjects.Order;

public class DateDisplayActivity extends AppCompatActivity {
    private List<String> dateList = new ArrayList<>();
    private List<Order> orderList = new ArrayList<>();
    private TextView txtDate;
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private String uid, date;
    private DateAdapter dateAdapter;
    private Button btnClear;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_display_activity);

        txtDate = findViewById(R.id.date_txt);
        btnClear = findViewById(R.id.clear_date_button);
        recyclerView = findViewById(R.id.date_recycler_view);
        dateAdapter = new DateAdapter(dateList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(dateAdapter);

        btnClear.setVisibility(View.GONE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            uid = user.getUid();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("hotel/sales/orders");

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.removeValue();
            }
        });

        final ChildEventListener orderEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue() != null){
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null){
                        int count = 0;



                        Log.d("mylog", order.getName());
                        Log.d("mylog", order.getDate());

                       for (int i = 0; i < dateList.size(); i++) {
                           if (order.getDate().equals(orderList.get(i).getDate())) {
                               Log.d("mylog", "duplicate");
                               Log.d("mylog", "order" + order.getDate());
                               Log.d("mylog", "list" + orderList.get(i).getDate());

                                   /* orderList.remove(i);
                                    dateList.remove(i);
                                    dateAdapter.notifyDataSetChanged();
                                    dateAdapter.notifyItemRemoved(i);
                                    dateAdapter.changeDataSet(dateList);*/
                               return;

                           }
                       }

                        orderList.add(order);
                                    dateList.add(order.getDate());
                                    dateAdapter.notifyDataSetChanged();
                                   // dateAdapter.changeDataSet(dateList);



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
        };

        mDatabase.addChildEventListener(orderEventListener);

    }


}
