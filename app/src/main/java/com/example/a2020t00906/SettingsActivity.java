package com.example.a2020t00906;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private ImageView backImageView;
    private ListenerRegistration userInfoListener;
    private TextView emailTextView, usernameTextView;
    private LinearLayout userInfoLayout; // Reference to anchor popup above

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        backImageView = findViewById(R.id.back);
        emailTextView = findViewById(R.id.email2);
        usernameTextView = findViewById(R.id.username1);
        userInfoLayout = findViewById(R.id.user_info_layout); // Initialize user info layout

        backImageView.setOnClickListener(v -> finish());

        // Get user info
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            String name = user.getDisplayName();
            emailTextView.setText("Email: " + email);
            usernameTextView.setText(name != null ? "Username: " + name : "No username");
        } else {
            emailTextView.setText("Not logged in");
            usernameTextView.setText("-");
        }

        // Developer info
        findViewById(R.id.dev).setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, DevActivity.class);
            startActivity(intent);
        });

        // Sign out
        LinearLayout signOutLayout = findViewById(R.id.signout);
        signOutLayout.setOnClickListener(v -> showSignOutPopup(userInfoLayout)); // Anchor popup above user info

        // Edit info
        LinearLayout editInfo = findViewById(R.id.edit_info);
        editInfo.setOnClickListener(v -> showEditProfilePopup(v));
    }

private void showEditProfilePopup(View anchor) {
    View popupView = LayoutInflater.from(this).inflate(R.layout.activity_edit_profile, null);
    final PopupWindow popupWindow = new PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
    );

    popupWindow.setOutsideTouchable(true);
    popupWindow.setFocusable(true);
    popupWindow.setElevation(20);

    // Measure content
    popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
    int popupHeight = popupView.getMeasuredHeight();

    // Get anchor location
    int[] location = new int[2];
    anchor.getLocationOnScreen(location);
    int anchorY = location[1];

    // Show popup centered above the anchor
    popupWindow.showAtLocation(anchor, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, anchorY - popupHeight);

    EditText editUsername = popupView.findViewById(R.id.editUsername);
    LinearLayout btnYes = popupView.findViewById(R.id.btnConfirm);
    LinearLayout btnCancel = popupView.findViewById(R.id.btnDeny);

    btnYes.setOnClickListener(v -> {
        String newUsername = editUsername.getText().toString().trim();
        if (!newUsername.isEmpty()) {
            updateUser(newUsername);
            popupWindow.dismiss();
        } else {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
        }
    });

    btnCancel.setOnClickListener(v -> popupWindow.dismiss());
}


    private void updateUser(String username) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        Map<String, Object> updates = new HashMap<>();
        if (!username.isEmpty()) {
            updates.put("username", username);
        }

        if (updates.isEmpty()) {
            Toast.makeText(this, "No changes to update", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance().collection("users").document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Username updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update username: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void signOutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showSignOutPopup(View anchor) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.activity_signout_confirm, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setElevation(20);

        // Measure content
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupHeight = popupView.getMeasuredHeight();

        // Get anchor location
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        int anchorY = location[1];

        // Show popup centered horizontally above the anchor
        popupWindow.showAtLocation(anchor, Gravity.TOP | Gravity.CENTER_HORIZONTAL,
                0,
                anchorY - popupHeight);

        // Setup buttons
        LinearLayout btnYes = popupView.findViewById(R.id.btnYes);
        LinearLayout btnCancel = popupView.findViewById(R.id.btnCancel);

        btnYes.setOnClickListener(v -> {
            popupWindow.dismiss();
            signOutUser();
        });

        btnCancel.setOnClickListener(v -> popupWindow.dismiss());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            userInfoListener = FirebaseFirestore.getInstance().collection("users")
                    .document(user.getUid())
                    .addSnapshotListener((documentSnapshot, error) -> {
                        if (error != null) {
                            Toast.makeText(this, "Listen failed.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String email = user.getEmail();
                            String username = documentSnapshot.getString("username");

                            emailTextView.setText("Email: " + (email != null ? email : "-"));
                            usernameTextView.setText("Username: " + (username != null ? username : "-"));
                        }
                    });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (userInfoListener != null) {
            userInfoListener.remove();
        }
    }
}
