package com.example.shafi.digitalizedrestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
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

public class SignUpActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private Button btnRegister, btnLogin, btnResetPassword, btnMainLogin;
    private ProgressBar progressBar;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private String register = "no register";
    private String myPREFERNCES = "myPreferene", uid;
    private Boolean isAppOpenBefore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().getExtras() != null){
             register = getIntent().getExtras().getString("register");
        }



        auth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            uid = user.getUid();
        }


      /*  if (uid != null){
            startActivity(new Intent(SignUpActivity.this, PanelActivity.class));
            finish();
            return;
        }*/

        setContentView(R.layout.activity_sign_up);

        btnRegister = findViewById(R.id.register_button);
        btnLogin = findViewById(R.id.login_button);
        btnResetPassword = findViewById(R.id.forgot_password_button);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);

        SharedPreferences sharedPreferences = SignUpActivity.this.getSharedPreferences("isAppOpenBefore",Context.MODE_PRIVATE);
      /*  isAppOpenBefore = sharedPreferences.getBoolean("isAppOpenBefore", false);

        if (isAppOpenBefore || !register.equals("register")){
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        }*/

        Log.d("mylog", "signup continued");

   /*     FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user != null){
                    sendVerificationLink();
                }
            }
        };*/


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6){
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

               register(email, password);

                progressBar.setVisibility(View.VISIBLE);
                //create user

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

    }

    public void register(String email, String password){

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(SignUpActivity.this, "Registered"+task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this, "Registration failed" + task.getException(),Toast.LENGTH_SHORT).show();
                        }else {
                            SharedPreferences sharedPreferences = SignUpActivity.this.getSharedPreferences("isAppOpenBefore",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isAppOpenBefore", true);
                            editor.commit();
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            sendVerificationLink(user);

                        }
                    }
                });

    }

    public void sendVerificationLink(FirebaseUser user){
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this, " Email Verification link sent", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class ));
                            finish();
                        }else{
                            Toast.makeText(SignUpActivity.this, "Email Verification failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }


}
