package com.example.shafi.digitalizedrestaurant;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText inputEmail;
    private Button btnReset;
    private TextView txtChangePassword, txtEmailRequest;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private String activity = "resetPassword", message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        if (getIntent().getExtras() != null){
            activity = getIntent().getExtras().getString("activity");
        }

        inputEmail = findViewById(R.id.email);
        btnReset = findViewById(R.id.reset_password_button);
        progressBar = findViewById(R.id.progressBar);
        txtChangePassword = findViewById(R.id.forgot_password_txt);
        txtEmailRequest = findViewById(R.id.email_request_txt);

        if (activity.equals("changePassword")){
            txtChangePassword.setText("Change Password");
            btnReset.setText("Change Password");
            txtEmailRequest.setText("Provide your registered Email id to change password");
        }


        auth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter your registered Email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(activity.equals("resetPassword"))
                    message = "Password reset link sent to your email";
                else
                    message = "Password change link sent to your email";

                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(ResetPasswordActivity.this, "Password reset link sent to your email", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(ResetPasswordActivity.this, "Failed to sent password reset link", Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });

    }
}
