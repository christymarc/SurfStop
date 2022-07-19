package com.example.surfstop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    final String TAG = SignupActivity.class.getSimpleName();
    EditText etUsername;
    EditText etPassword;
    EditText etPasswordConfirm;
    EditText etEmail;
    Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        etEmail = findViewById(R.id.etEmail);
        signupButton = findViewById(R.id.signupButton);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String password_confirm = etPasswordConfirm.getText().toString();
                if (password.equals(password_confirm)) {
                    String username = etUsername.getText().toString();
                    String email = etEmail.getText().toString();
                    signupUser(username, password, email);
                }
                else {
                    Snackbar.make(signupButton, R.string.no_password_match, Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void signupUser(String username, String password, String email) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    goMainActivity();
                } else {
                    //TODO: Do better error handling here (capture e)
                    if (e.toString().contains("Account already exists.")) {
                        Snackbar.make(signupButton, R.string.account_exists, Snackbar.LENGTH_SHORT)
                                .show();
                    }
                    else {
                        Snackbar.make(signupButton, "Sign up error.", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}