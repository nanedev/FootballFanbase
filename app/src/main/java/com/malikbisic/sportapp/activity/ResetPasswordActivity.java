package com.malikbisic.sportapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.malikbisic.sportapp.R;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText resetPwEmail;
    private Button resetPasswordBtn;
    FirebaseAuth mAuth;
    String emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        resetPwEmail = (EditText) findViewById(R.id.input_email_password_reset);
        resetPasswordBtn = (Button) findViewById(R.id.resetPasswordBtn);
        mAuth = FirebaseAuth.getInstance();


        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emailAddress = resetPwEmail.getText().toString();
                if (TextUtils.isEmpty(emailAddress)) {
                    Toast.makeText(ResetPasswordActivity.this, "Field can not be empty!", Toast.LENGTH_LONG).show();
                } else {


                    mAuth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("proba", "Email sent.");

                                        Toast.makeText(ResetPasswordActivity.this, "Check your email for further instructions", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e.getMessage().equals(getString(R.string.error_reset_pass_email_doesnt_exists))) {
                                Toast.makeText(ResetPasswordActivity.this, "Email does not exists", Toast.LENGTH_LONG).show();
                            } else if (e.getMessage().equals(getString(R.string.error_reset_pass_blank_email_field))) {
                                Toast.makeText(ResetPasswordActivity.this, "Please enter valid email address", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent goToLogin = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        goToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goToLogin);
    }
}
