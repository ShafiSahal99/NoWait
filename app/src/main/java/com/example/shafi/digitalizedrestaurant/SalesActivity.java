package com.example.shafi.digitalizedrestaurant;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Adapters.OrderAdapter;
import MyObjects.Order;

public class SalesActivity extends AppCompatActivity {
    private List<Order> orderList = new ArrayList<>();
    private TextView txtSales;
    private Button btnRefresh;
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private String uid, userType, date;
    private int totalSales = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_display);

        if (getIntent().getExtras() != null){
            userType = getIntent().getExtras().getString("userType");
            date = getIntent().getExtras().getString("date");
        }

        txtSales = findViewById(R.id.bill_counter_total_amount_txt);
        btnRefresh = findViewById(R.id.paid_button);
        recyclerView = findViewById(R.id.bill_counter_recycler_view);

        orderAdapter = new OrderAdapter(orderList, this, userType);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(orderAdapter);

        btnRefresh.setText("CLEAR");
        btnRefresh.setVisibility(View.GONE);
        txtSales.setText("Total Sales : Rs 0");

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            uid = user.getUid();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("hotel/sales");

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearSalesDialogue();

            }
        });

        mDatabase.child("orders").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue() != null){
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null){


                        for(int i = 0; i < orderList.size(); i++ ){
                            if (order.getName().equals(orderList.get(i).getName())){
                                Order newOrder = orderList.get(i);

                                newOrder.setQuantity(newOrder.getQuantity() + order.getQuantity());
                                return;
                            }

                        }

                        if (date.equals(order.getDate())) {
                            orderList.add(order);


                            sortOrderList(orderList);
                            orderAdapter.notifyDataSetChanged();

                            totalSales += order.getPrice() * order.getQuantity();
                            txtSales.setText("Total Sales : Rs " + String.valueOf(totalSales));
                        }

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

    public List<Order> sortOrderList(List<Order> orderList){
        Collections.sort(orderList, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                Integer quantity1 = o1.getQuantity();
                Integer quantity2 = o2.getQuantity();
                return quantity2.compareTo(quantity1);
            }
        });

        return orderList;
    }

    public void showClearSalesDialogue(){
        AlertDialog.Builder alert = new AlertDialog.Builder(SalesActivity.this);
        alert.setMessage("Are your sure you want to clear the list?");
        alert.setTitle("Confirm Action");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabase.child("orders").removeValue();
                orderList.clear();
                txtSales.setText("Total Sales : Rs 0");
                orderAdapter.notifyDataSetChanged();
                orderAdapter.changeDataSet(orderList);
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();

    }
}
