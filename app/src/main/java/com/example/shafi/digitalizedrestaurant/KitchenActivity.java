package com.example.shafi.digitalizedrestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import Adapters.KitchenAdapter;
import MyObjects.Chair;
import MyObjects.Order;

public class KitchenActivity extends AppCompatActivity {
    private List<Chair> chairList = new ArrayList<>();
    private List<String> keyList = new ArrayList<>();
    private List<Order> orderList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private KitchenAdapter kitchenAdapter;
    private RecyclerView recyclerView;
    private String uid, chairKey, userType = "kitchen";
    private Chair chair1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);

        if (getIntent().getExtras() != null){
            userType = getIntent().getExtras().getString("userType");
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            uid = user.getUid();

        chair1 = new Chair();
        kitchenAdapter = new KitchenAdapter(chairList, this, userType);
        recyclerView = findViewById(R.id.kitchen_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(kitchenAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("hotel/customer");

        CategoryDisplayActivity categoryDisplayActivity = new CategoryDisplayActivity(this);
        categoryDisplayActivity.writeLockStatus(this);

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getValue() != null){
                    Chair chair = dataSnapshot.getValue(Chair.class);
                    if(chair != null){
                        chairList.add(chair);
                        chair1 = chair;
                        keyList.add(dataSnapshot.getKey());
                        kitchenAdapter.setKey(keyList);
                        kitchenAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.getValue() != null){
                    Chair chair = dataSnapshot.getValue(Chair.class);
                    if (chair != null){
                        for(int i = 0; i < chairList.size(); i++){
                            if (chair.getChairNumber() == chairList.get(i).getChairNumber()){
                                chairList.remove(i);
                                chairKey = keyList.get(i);
                                keyList.remove(i);
                                kitchenAdapter.setKey(keyList);
                                kitchenAdapter.notifyDataSetChanged();
                                kitchenAdapter.notifyItemRemoved(i);
                                kitchenAdapter.changeDataSet(chairList);
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

            getMenuInflater().inflate(R.menu.menu_customer, menu);

        return super.onCreateOptionsMenu(menu);

    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.panel_button){
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            Intent intent = new Intent(KitchenActivity.this, LoginActivity.class);
            intent.putExtra("AuthState", "signOut");
            startActivity(intent);

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){

    }

}
