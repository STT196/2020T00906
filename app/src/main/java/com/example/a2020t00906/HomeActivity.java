package com.example.a2020t00906;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    private Button logoutButton;
    private TextView usernameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // Not logged in, go to LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main); // set the layout only if logged in

//        logoutButton = findViewById(R.id.logoutButton);
//        usernameTextView = findViewById(R.id.usernameTextView); // Assuming you have a TextView with this ID
//        logoutButton.setOnClickListener(v -> logoutUser());

        // Display username
        if (user != null) {
            String username = user.getDisplayName();
            if (username != null && !username.isEmpty()) {
                usernameTextView.setText("Welcome, " + username);
            } else {
                usernameTextView.setText("Welcome!"); // Fallback if display name is not set
            }
        }
    }

    private void logoutUser() {
        // Sign out from Firebase Auth
        FirebaseAuth.getInstance().signOut();

        // Optionally clear local shared preferences
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        // Navigate to Login activity
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}
