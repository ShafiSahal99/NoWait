package com.example.shafi.digitalizedrestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PanelActivity extends AppCompatActivity {
    private Button btnAdmin, btnBillCounter, btnKitchen, btnCustomer;
    private FirebaseUser user;
    private FirebaseAuth auth;
    public String usertype;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);

        btnAdmin = findViewById(R.id.admin_button);
        btnBillCounter = findViewById(R.id.bill_counter_button);
        btnKitchen= findViewById(R.id.kitchen_button);
        btnCustomer = findViewById(R.id.customer_button);


        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            Intent intent = new Intent(PanelActivity.this, SignUpActivity.class);
            startActivity(intent);
        }

        SharedPreferences sharedPreferences = PanelActivity.this.getSharedPreferences("PanelPermission", Context.MODE_PRIVATE);
        Boolean isLocked = sharedPreferences.getBoolean("LockStatus", false);

        if (isLocked){

            auth.signOut();
            Intent intent = new Intent(PanelActivity.this, LoginActivity.class);
            startActivity(intent);

        }


        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PanelActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });

        btnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PanelActivity.this, CategoryDisplayActivity.class);
                intent.putExtra("userType", "customer");
                startActivity(intent);
                finish();
            }
        });



        btnKitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PanelActivity.this, KitchenActivity.class);
                startActivity(intent);
            }
        });

        btnBillCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PanelActivity.this, BillCounerActivity.class);
                intent.putExtra("userType", "counter");
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.sign_out_button) {
            auth.signOut();
            Intent intent = new Intent(PanelActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if (id == R.id.change_password_button){
            Intent intent = new Intent(PanelActivity.this, ResetPasswordActivity.class);
            intent.putExtra("activity", "changePassword");
            startActivity(intent);
        }



        return super.onOptionsItemSelected(item);
    }
}
