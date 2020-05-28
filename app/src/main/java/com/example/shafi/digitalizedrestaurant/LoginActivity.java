package com.example.shafi.digitalizedrestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ProgressBar progressBar;
    private Button btnRegister, btnLogin, btnResetPassword;
    private Boolean isEmailVerified = false;
    private String authState = "signIn";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null){
            authState = getIntent().getExtras().getString("AuthState");
        }

        auth = FirebaseAuth.getInstance();

      /*  if (authState.equals("signOut")){
            auth.signOut();
        }*/

      /*  if(auth.getCurrentUser() != null) {
            auth.getCurrentUser().reload();
        }*/

        if (auth.getCurrentUser() != null/* && auth.getCurrentUser().isEmailVerified()*/){
            startActivity(new Intent(LoginActivity.this, PanelActivity.class));
            finish();
        }

        Log.d("mylog", "login continued");

        setContentView(R.layout.activity_login);


        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        btnRegister = findViewById(R.id.register_button);
        btnLogin = findViewById(R.id.login_button);
        btnResetPassword = findViewById(R.id.forgot_password_button);

        auth = FirebaseAuth.getInstance();



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Boolean isEmailVerified = isUserEmailVerified();
                   /* if(auth.getCurrentUser() != null) {
                        auth.getCurrentUser().reload();


                        if (!auth.getCurrentUser().isEmailVerified()) {
                            Toast.makeText(LoginActivity.this, "Email not verified", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }*/

                    String email = inputEmail.getText().toString().trim();
                    final String password = inputPassword.getText().toString().trim();

                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(getApplicationContext(), "Enter email address", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(password)) {
                        Toast.makeText(getApplicationContext(), "Enter password1", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    progressBar.setVisibility(View.VISIBLE);

                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences("PanelPermission", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean("LockStatus", false);
                                        editor.commit();

                                        Intent intent = new Intent(LoginActivity.this, PanelActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
            //    }catch (Exception e){e.printStackTrace();}

            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                intent.putExtra("register", "register");
                startActivity(intent);

            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

   /* public Boolean isUserEmailVerified(){


            if (auth.getCurrentUser().isEmailVerified()) {
                return true;
            } else {
                Toast.makeText(LoginActivity.this, "Email not verified", Toast.LENGTH_LONG).show();
                return false;
            }


    }*/



    @Override
    public  void onBackPressed(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
            finishAffinity();
        }else {
            finish();
        }

    }


}