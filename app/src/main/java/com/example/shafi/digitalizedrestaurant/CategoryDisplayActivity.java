package com.example.shafi.digitalizedrestaurant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import Adapters.CategoryAdapter;
import MyObjects.Category;

public class CategoryDisplayActivity extends AppCompatActivity {
    private List<Category> categoryList = new ArrayList<>();
    private List<String> keyList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private FloatingActionButton fab;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private String uid, hashedPIN = "", userType = "admin";
    private Context context;

    public CategoryDisplayActivity(){}

    public CategoryDisplayActivity(Context context){
        this.context = context;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_display);

        if (getIntent().getExtras() != null){
            //checks if the entered user is admin or customer
            userType = getIntent().getExtras().getString("userType");
        }

        recyclerView = findViewById(R.id.category_recycler_view);
        fab = findViewById(R.id.display_menu_fab);
        categoryAdapter = new CategoryAdapter(categoryList, this, keyList, userType);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(categoryAdapter);

        if (userType.equals("customer")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_shopping_basket_black_24dp));
            }

        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userType.equals("admin"))
                    showCategoryAddDialogue();
                else{
                    Intent intent = new Intent(CategoryDisplayActivity.this, BasketActivity.class);
                    startActivity(intent);
                }
            }
        });

        if(userType.equals("customer")) {
            writeLockStatus(this);
        }

        mDatabase.child("users").child(uid).child("hotel/menu").child("category").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getValue() != null) {
                    Category category = dataSnapshot.getValue(Category.class);
                   // String categoryId =  mDatabase.child("users").child(uid).child("menu").child("category").push().getKey();
                  //  Log.d("mylog", categoryId);

                    keyList.add(dataSnapshot.getKey());
                    categoryList.add(category);
                    categoryAdapter.setKey(keyList);
                    categoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getValue() != null) {
                    Category category = dataSnapshot.getValue(Category.class);

                    for(int i = 0; i < categoryList.size(); i++){
                        if (category.getName().equals(categoryList.get(i).getName()))
                            categoryList.set(i, category);
                    }
                    categoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Category category = dataSnapshot.getValue(Category.class);
                    Log.d("mylog", category.getName());

                    for(int i = 0; i < categoryList.size(); i++){
                        if (category.getName().equals(categoryList.get(i).getName()))
                            categoryList.remove(i);
                    }

                   // categoryList.remove(category);
                    categoryAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        categoryAdapter.notifyDataSetChanged();
    }

    public void writeCategoryName(Category category){
        if (categoryList != null) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (category.getName().equals(categoryList.get(i).getName())) {
                    Toast.makeText(CategoryDisplayActivity.this, "Category already exist", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
        mDatabase.child("users").child(uid).child("hotel/menu").child("category").push().setValue(category);
        Toast.makeText(CategoryDisplayActivity.this, "Category Added", Toast.LENGTH_LONG).show();
    }

    public void showCategoryAddDialogue(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        alert.setMessage("Enter Category Name");
        alert.setTitle("Category");
        alert.setView(editText);

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(editText.getText().toString().isEmpty()){
                    Toast.makeText(CategoryDisplayActivity.this, "Enter Category Name", Toast.LENGTH_LONG).show();
                    return;
                }


                String text = editText.getText().toString();
                editText.setMaxLines(1);
                Category category = new Category();
                category.setName(text);
                if (category.getCategoryId() == null)
                     category.setCategoryId(UUID.randomUUID().toString());

                    writeCategoryName(category);
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

    public void writeLockStatus(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("PanelPermission", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("LockStatus", true);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(userType.equals("customer")) {
            getMenuInflater().inflate(R.menu.menu_customer, menu);
        }
            return super.onCreateOptionsMenu(menu);

    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.panel_button){
            Intent intent = new Intent(CategoryDisplayActivity.this, LoginActivity.class);
            intent.putExtra("AuthState", "signOut");
            startActivity(intent);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if (userType.equals("admin")){
            super.onBackPressed();
        }

    }

}
