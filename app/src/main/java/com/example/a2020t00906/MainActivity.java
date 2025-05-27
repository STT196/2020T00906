//package com.example.a2020t00906;
//
//import android.content.Intent;
//import android.os.Bundle;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//public class MainActivity extends AppCompatActivity {
//
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        FirebaseApp.initializeApp(this);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
////
////        // Check if user is already logged in
////        if (currentUser == null) {
////            // Not logged in → go to Login activity
////            startActivity(new Intent(MainActivity.this, LoginActivity.class));
////            finish(); // prevent returning to MainActivity
////        } else {
////            // Already logged in → go to Home activity (or main content)
////            startActivity(new Intent(MainActivity.this, HomeActivity.class));
////            finish();
////        }
//    }
//}

package com.example.a2020t00906;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout newsContainer;

    ImageView avatarImageView;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // your main layout file

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        newsContainer = findViewById(R.id.newsContainer); // Make sure this ID is in your XML

        loadNews();

        avatarImageView = findViewById(R.id.profileAvatar);


        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

    }




    private void loadNews() {
        db.collection("news")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String title = document.getString("title");
                        String date = document.getString("date");
                        String content = document.getString("content");
                        String imageUrl = document.getString("imageUrl");

                        addNewsCard(title, date, content, imageUrl);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this, "Failed to load news", Toast.LENGTH_SHORT).show()
                );
    }

    private void addNewsCard(String title, String date, String content, String imageUrl) {
        LayoutInflater inflater = LayoutInflater.from(this);

        // Inflate the news_card layout as a View (or ViewGroup)
        View cardView = inflater.inflate(R.layout.news_card, newsContainer, false);

        TextView titleView = cardView.findViewById(R.id.newsTitle);
        TextView dateView = cardView.findViewById(R.id.newsDate);
        TextView contentView = cardView.findViewById(R.id.newsContent);
        ImageView imageView = cardView.findViewById(R.id.newsImage);

        titleView.setText(title);
        dateView.setText(date);
        contentView.setText(content);

        // Handle possible empty or null imageUrl
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(imageView);
        } else {
            // Optionally hide or clear image if no url provided
            imageView.setImageDrawable(null);
            imageView.setVisibility(View.GONE);
        }

        newsContainer.addView(cardView);
    }

}

