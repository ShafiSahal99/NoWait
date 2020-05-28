package com.example.shafi.digitalizedrestaurant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Adapters.FoodAdapter;
import MyObjects.Chair;
import MyObjects.Food;
import MyObjects.Hotel;
import MyObjects.Order;
import Adapters.OrderAdapter;

public class BasketActivity extends AppCompatActivity {
    private Button btnClear, btnOrder;
    private TextView txtBill;
    private RecyclerView basketRecyclerView;
    private List<Order> orderList = new ArrayList<>();
    private List<Food> foodList = new ArrayList<>();
    private List<String> keyList;
    private OrderAdapter orderAdapter;
    private FoodAdapter foodAdapter;
    private int bill = 0;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private String uid, chairKey;
    private Hotel hotel1;
    private List<Chair> chairList = new ArrayList<>();
    private Context context;

    public BasketActivity(){}

    public BasketActivity(Context context) {
        this.context = context;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);


        btnClear = findViewById(R.id.clear_list_button);
        btnOrder = findViewById(R.id.order_button);
        basketRecyclerView = findViewById(R.id.basket_recycler_view);
        txtBill = findViewById(R.id.bill_txt);
        hotel1 = new Hotel();



        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            uid = user.getUid();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("hotel/customer");



        SharedPreferences sharedPreferences = BasketActivity.this.getSharedPreferences("BasketFile", Context.MODE_PRIVATE);
        String orderAsString = sharedPreferences.getString("OrderDetails", null);
      // sharedPreferences.edit().clear().commit();
        if(orderAsString!=null) {
            Log.d("mylog", orderAsString);
        }

        Type listType = new TypeToken<List<Order>>(){}.getType();

        Gson gson = new Gson();
        if(orderAsString != null) {
            orderList = gson.fromJson(orderAsString, listType);
            if (orderList!=null) {
                orderAdapter = new OrderAdapter(orderList, this, txtBill);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                basketRecyclerView.setLayoutManager(layoutManager);
                basketRecyclerView.setItemAnimator(new DefaultItemAnimator());
                basketRecyclerView.setAdapter(orderAdapter);
                orderAdapter.notifyDataSetChanged();
                orderAdapter.changeDataSet(orderList);
               // orderAdapter.notifyDataSetChanged();
            }

            for(int i = 0; i < orderList.size(); i++){
                bill += orderList.get(i).getPrice()*orderList.get(i).getQuantity();
            }
        }



        txtBill.setText("Total Amount : Rs " + bill);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearBasketDialogue();
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(orderList.size() == 0){
                    Toast.makeText(BasketActivity.this, "No food added to process order", Toast.LENGTH_LONG).show();
                    return;
                }

                writeOrders(orderList);
            }
        });


        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String hotelName = "";
                int chairCount = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    hotelName = ds.child("HotelName").getValue(String.class);
                    chairCount = ds.child("ChairCount").getValue(Integer.class);

                }
                hotel1.setHotelName(hotelName);
                hotel1.setChairCount(chairCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        database.addValueEventListener(valueEventListener);


       /* database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getValue() != null){
                    Hotel hotel = dataSnapshot.getValue(Hotel.class);
                    if (hotel != null){
                        hotel1.setHotelName(hotel.getHotelName());
                        hotel1.setChairCount(hotel.getChairCount());
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
        });*/

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getValue() != null){
                    Chair chair = dataSnapshot.getValue(Chair.class);
                    chairList.add(chair);
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

    public void showClearBasketDialogue(){
        AlertDialog.Builder alert  = new AlertDialog.Builder(BasketActivity.this);
        alert.setMessage("Are you sure you want to clear the basket?");
        alert.setTitle("Confirm Action");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = BasketActivity.this.getSharedPreferences("BasketFile", Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();

                orderList.clear();
                orderAdapter.changeDataSet(orderList);
                orderAdapter.notifyDataSetChanged();


                txtBill.setText("Total Amount : Rs 0");
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

    public void writeOrders(final List<Order> orderList){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        final Chair chair = new Chair();
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setMaxLines(1);
        alert.setMessage("Enter Chair Number to process order.");
        alert.setTitle("Chair Number");
        alert.setView(editText);



        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(editText.getText().toString().isEmpty()){
                    Toast.makeText(BasketActivity.this, "Enter Chair Number", Toast.LENGTH_LONG).show();
                    return;
                }

                if(Integer.parseInt(editText.getText().toString()) > hotel1.getChairCount()){
                    Toast.makeText(BasketActivity.this, "Enter a valid Chair Number", Toast.LENGTH_LONG).show();
                    Log.d("mylog", String.valueOf(hotel1.getChairCount()));
                    return;
                }





                int chairNumber = Integer.parseInt(editText.getText().toString());

                for (int i = 0; i < chairList.size(); i++){
                    if (chairNumber == chairList.get(i).getChairNumber()){
                        Toast.makeText(BasketActivity.this, "You cannot place another order until\nlast order is processed", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                chair.setChairNumber(chairNumber);
                chairKey = mDatabase.push().getKey();
                mDatabase.child(chairKey).setValue(chair);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("hotel/counter");
                String counterChairKey = databaseReference.push().getKey();
                databaseReference.child(counterChairKey).setValue(chair);

                DatabaseReference salesReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("hotel/sales");

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
                Date date = new Date();
                String dateInString = sdf.format(date);

                for(int i = 0; i < orderList.size(); i++){
                    orderList.get(i).setDate(dateInString);
                    mDatabase.child(chairKey).child("orders").push().setValue(orderList.get(i));
                    databaseReference.child(counterChairKey).child("orders").push().setValue(orderList.get(i));
                    salesReference.child("orders").push().setValue(orderList.get(i));
                }





                Toast.makeText(BasketActivity.this, "Order Placed", Toast.LENGTH_LONG).show();
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

    public void setOrderList(List<Order> orderList){
        this.orderList = orderList;
    }

    public void refresh(){
        finish();

    }
}
    