package com.example.enlighten;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    FirebaseDatabase root;
    DatabaseReference reference;

    ImageView image;
    TextView headerText, descText;

    TextInputLayout regName, regUsername, regEmail, regPassword;
    Button regBtn, toLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Log.d(TAG, "Signup button clicked");
        regName = findViewById(R.id.nameCreate);
        regUsername = findViewById(R.id.usernameCreate);
        regEmail = findViewById(R.id.emailCreate);
        regPassword = findViewById(R.id.passwordCreate);

        regBtn = findViewById(R.id.btn_signup);
        toLoginBtn = findViewById(R.id.toLogform);

        // Initialize Firebase
        root = FirebaseDatabase.getInstance();
        reference = root.getReference("users");

        // Log the Firebase database URL
        String databaseUrl = root.getReference().toString();
        Log.d(TAG, "Firebase Database URL: " + databaseUrl);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle register button click
                if (!validateEmail() || !validateName() || !validatePassword() || !validateUsername()) {
                    return;
                }

                String name = regName.getEditText().getText().toString();
                String username = regUsername.getEditText().getText().toString();
                String password = regPassword.getEditText().getText().toString();
                String email = regEmail.getEditText().getText().toString();

                UserClass userClass = new UserClass(name, username, password, email);

                // Save user data to Firebase
                reference.setValue(userClass).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Registration successful
                        Intent intent = new Intent(signup.this, login.class);
                        Pair[] pairs = new Pair[7];
                        pairs[0] = new Pair<View, String>(image, "logo_image");
                        pairs[1] = new Pair<View, String>(headerText, "transition_text1");
                        pairs[2] = new Pair<View, String>(descText, "transition_text2");
                        pairs[3] = new Pair<View, String>(regUsername, "usernameTransition");
                        pairs[4] = new Pair<View, String>(regPassword, "passwordTransition");
                        pairs[5] = new Pair<View, String>(regBtn, "loginTransition");
                        pairs[6] = new Pair<View, String>(toLoginBtn, "passwordTransition");
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(signup.this, pairs);
                        startActivity(intent, options.toBundle());
                    } else {
                        // Registration failed
                        Toast.makeText(signup.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private Boolean validateName() {
        String val = regName.getEditText().getText().toString();
        if (val.isEmpty()) {
            regName.setError("Field cannot be empty");
            Log.d(TAG, "Name validation failed: Field cannot be empty");
            return false;
        } else {
            regName.setError(null);
            return true;
        }
    }

    private Boolean validateUsername() {
        String val = regUsername.getEditText().getText().toString();
        String noWhiteSpace = "^\\S+$";
        if (val.isEmpty()) {
            regUsername.setError("Field cannot be empty");
            Log.d(TAG, "Username validation failed: Field cannot be empty");
            return false;
        } else if (val.length() >= 15) {
            regUsername.setError("Username too long");
            Log.d(TAG, "Username validation failed: Username too long");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            regUsername.setError("Please remove whitespaces");
            Log.d(TAG, "Username validation failed: Please remove whitespaces");
            return false;
        } else {
            regUsername.setError(null);
            regUsername.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = regPassword.getEditText().getText().toString();
        if (val.isEmpty()) {
            regPassword.setError("Field cannot be empty");
            Log.d(TAG, "Password validation failed: Field cannot be empty");
            return false;
        } else {
            regPassword.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String val = regEmail.getEditText().getText().toString();
        String emailFormat = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            regEmail.setError("Field cannot be empty");
            Log.d(TAG, "Email validation failed: Field cannot be empty");
            return false;
        } else if (!val.matches(emailFormat)) {
            regEmail.setError("Invalid email format");
            Log.d(TAG, "Email validation failed: Invalid email format");
            return false;
        } else {
            regEmail.setError(null);
            return true;
        }

}}
