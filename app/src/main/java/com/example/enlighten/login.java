package com.example.enlighten;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {

    Button callSignUP, toLogin;
    ImageView image;
    TextView headerText, descText;
    TextInputLayout username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login2);

        callSignUP = findViewById(R.id.toSignUP);
        toLogin = findViewById(R.id.toLogin);
        image = findViewById(R.id.logoImage);
        headerText = findViewById(R.id.headerText);
        descText = findViewById(R.id.descText);
        username  = findViewById(R.id.username);
        password = findViewById(R.id.password);

        callSignUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, signup.class);

                Pair[] pairs = new Pair[7];

                pairs[0] = new Pair<View, String>(image, "logo_image");
                pairs[1] = new Pair<View, String>(headerText, "transition_text1");
                pairs[2] = new Pair<View, String>(descText, "transition_text2");
                pairs[3] = new Pair<View, String>(username, "usernameTransition");
                pairs[4] = new Pair<View, String>(password, "passwordTransition");
                pairs[5] = new Pair<View, String>(toLogin, "loginTransition");
                pairs[6] = new Pair<View, String>(callSignUP, "signupTransition");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(login.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() | !validatePassword()) {
                    return;
                }
                isUser();
            }
        });
    }

    private Boolean validateUsername() {
        String val = username.getEditText().getText().toString();

        if (val.isEmpty()) {
            username.setError("Field cannot be empty");
            Log.d(TAG, "Username validation failed: Field cannot be empty");
            return false;
        } else {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = password.getEditText().getText().toString();
        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            Log.d(TAG, "Password validation failed: Field cannot be empty");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    private void isUser() {
        String enteredUsername = username.getEditText().getText().toString();
        String enteredPassword = password.getEditText().getText().toString();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        Query checkUser = ref.orderByChild("username").equalTo(enteredUsername);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String dbPassword = dataSnapshot.child(enteredUsername).child("password").getValue(String.class);

                    username.setError(null);
                    username.setErrorEnabled(false);

                    if (dbPassword != null && dbPassword.equals(enteredPassword)) {
                        String dbName = dataSnapshot.child(enteredUsername).child("name").getValue(String.class);
                        String dbUsername = dataSnapshot.child(enteredUsername).child("username").getValue(String.class);
                        String dbEmail = dataSnapshot.child(enteredUsername).child("email").getValue(String.class);

                        Intent intent = new Intent(getApplicationContext(), profile.class);

                        intent.putExtra("name", dbName);
                        intent.putExtra("username", dbUsername);
                        intent.putExtra("password", dbPassword);
                        intent.putExtra("email", dbEmail);

                        startActivity(intent);
                    } else {
                        password.setError("Wrong Password");
                        password.requestFocus();
                    }
                } else {
                    username.setError("Username not found.");
                    username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }
}
