package com.example.shafi.digitalizedrestaurant;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapters.FoodAdapter;
import MyObjects.Category;
import MyObjects.Food;

public class FoodDisplayActivity extends AppCompatActivity {
    private List<Food> foodList = new ArrayList<>();
    private List<String> keyList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private FloatingActionButton fab;
    private FoodAdapter foodAdapter;
    private String uid, categoryKey, categoryname, userType;
    private Boolean duplicate = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_display);

        if (getIntent().getExtras() != null){
            categoryKey = getIntent().getExtras().getString("categoryKey");
            categoryname = getIntent().getExtras().getString("categoryName");
            userType = getIntent().getExtras().getString("userType");
        }

        recyclerView = findViewById(R.id.food_recycler_view);
        fab = findViewById(R.id.display_food_fab);

        if (userType.equals("customer")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_shopping_basket_black_24dp));
            }
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            uid = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("hotel/menu/category");
        foodAdapter = new FoodAdapter(foodList, this, keyList, userType);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(foodAdapter);
        foodAdapter.notifyDataSetChanged();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(userType.equals("admin"))
                    showFoodAddDialogue();
                else{
                    Intent intent = new Intent(FoodDisplayActivity.this, BasketActivity.class);
                    startActivity(intent);
                }
            }
        });

        mDatabase.child(categoryKey).child("food").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        Food food = dataSnapshot.getValue(Food.class);
                        if (food != null) {

                            // String categoryId =  mDatabase.child("users").child(uid).child("menu").child("category").push().getKey();
                            //  Log.d("mylog", categoryId);

                            foodList.add(food);
                            keyList.add(dataSnapshot.getKey());
                            foodAdapter.setKey(keyList, categoryKey, categoryname);
                        }
                    }
                }catch (Exception e){e.printStackTrace();}
                foodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        Food food = dataSnapshot.getValue(Food.class);

                        for (int i = 0; i < foodList.size(); i++) {
                            if (food.getName().equals(foodList.get(i).getName()))
                                foodList.set(i, food);
                        }
                        foodAdapter.notifyDataSetChanged();
                    }
                }catch (Exception e){e.printStackTrace();}
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        Food food = dataSnapshot.getValue(Food.class);
                        Log.d("mylog", food.getName());

                        for (int i = 0; i < foodList.size(); i++) {
                            if (food.getName().equals(foodList.get(i).getName()))
                                foodList.remove(i);
                        }

                        // categoryList.remove(category);
                        foodAdapter.notifyDataSetChanged();
                    }
                }catch (Exception e){e.printStackTrace();}
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void writeFoodName(Food food){
        if (foodList != null) {
            for (int i = 0; i < foodList.size(); i++) {
                if (food.getName().equals(foodList.get(i).getName())) {
                    Log.d("mylog", food.getName());
                    Toast.makeText(FoodDisplayActivity.this, "Category already exist", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

       /* if(isDuplicateFood(food)){
            Toast.makeText(FoodDisplayActivity.this, "Category already exist", Toast.LENGTH_LONG).show();
            return;
        }*/
        mDatabase.child(categoryKey).child("food").push().setValue(food);
        Toast.makeText(FoodDisplayActivity.this, "Food Added", Toast.LENGTH_LONG).show();

    }

    public boolean isDuplicateFood(final Food foodToWrite){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Category category = snapshot.getValue(Category.class);
                        String key = snapshot.getKey();
                        Log.d("mylog", category.getName());
                        Log.d("mylog", key);
                        if (key != null) {
                            mDatabase.child(key).child("food").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                        Food food = snapshot1.getValue(Food.class);
                                        if (food != null) {
                                            Log.d("mylog", food.getName());
                                            if (foodToWrite.getName().equals(food.getName()))
                                                duplicate = true;

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                    }
                }catch (Exception e){e.printStackTrace();}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return duplicate;
    }

    public void showFoodAddDialogue(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final EditText food_name_txt = new EditText(this);
        food_name_txt.setHint("Food Name");
        food_name_txt.setMaxLines(1);
        linearLayout.addView(food_name_txt);

        final EditText price_txt = new EditText(this);
        price_txt.setHint("Food Price");
        price_txt.setMaxLines(1);
        price_txt.setInputType(InputType.TYPE_CLASS_NUMBER);
        linearLayout.addView(price_txt);

        alert.setMessage("Enter Food Details");
        alert.setTitle("Food");
        alert.setView(linearLayout);

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (food_name_txt.getText().toString().isEmpty()) {
                    Toast.makeText(FoodDisplayActivity.this, "Enter Food Name", Toast.LENGTH_LONG).show();

                } else if (price_txt.getText().toString().isEmpty()) {
                    Toast.makeText(FoodDisplayActivity.this, "Enter Food Price", Toast.LENGTH_LONG).show();

                } else {

                    String name = food_name_txt.getText().toString();
                    int price = Integer.parseInt(price_txt.getText().toString());


                    Food food = new Food();
                    food.setName(name);
                    food.setPrice(price);
                    if (food.getCategory() == null)
                        food.setCategory(categoryname);

                    writeFoodName(food);

                }
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
