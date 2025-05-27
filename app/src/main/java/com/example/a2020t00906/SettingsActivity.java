package com.example.a2020t00906;

import android.content.Intent;
import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class SettingsActivity extends AppCompatActivity {
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
//////        setContentView(R.layout.activity_settings); // Create this layout
////    }
//
//}


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    private ImageView backImageView;
    private TextView emailTextView,usernameTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Initialize back image
        backImageView = findViewById(R.id.back);

        // Handle back navigation
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to previous activity (MainActivity)
                finish(); // This closes the current activity and goes back
            }
        });
        emailTextView = findViewById(R.id.email2);
        usernameTextView = findViewById(R.id.username1);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String email = user.getEmail();
            String name = user.getDisplayName(); // May be null if not set

            emailTextView.setText("Email: "+email);
            usernameTextView.setText(name != null ? "Username: "+name : "No username");
        } else {
            emailTextView.setText("Not logged in");
            usernameTextView.setText("-");
        }
        View devInfo = findViewById(R.id.dev);


        devInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, DevActivity.class);
                startActivity(intent);
            }
        });
    }



    }


