package com.example.facebookauth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;

public class DetailsPage extends AppCompatActivity {

    private ImageView profilePic;
    private TextView name;
    private TextView email;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private CallbackManager callbackManager;
    private Button signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_page);

        profilePic = findViewById(R.id.profilePic);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        signOut = findViewById(R.id.signOut);
        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    email.setText(user.getEmail());
                    name.setText(user.getDisplayName());
                    if (user.getPhotoUrl() != null) {
                        displayImage(user.getPhotoUrl());
                    } else {
                        email.setText("");
                        name.setText("");
                        profilePic.setImageResource(R.drawable.ic_person);
                    }
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
    }

    void displayImage(Uri imageUrl) {
        new DownloadImageTask((ImageView) findViewById(R.id.profilePic))
                .execute(imageUrl.toString());
    }

    public void logOut(View v) {
        if (v == signOut) {
            signout();
        }

    }

    public void signout() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        startActivity(new Intent(this,MainActivity.class));
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView viewById) {
            this.bmImage = viewById;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String urldisplay = strings[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }
}
