package com.example.communityservicemap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    /*
    DO NOT DELETE.
     */
    private static final String PUBLIC_ACCESS_TOKEN = "pk.eyJ1Ijoia2Vsam91IiwiYSI6ImNrbmh2ZGViaTBwMzYyb3FoM3Nud25lb2kifQ.Weyh5b0R8XUuA6JxTaeH9w";
    private static final String SECRET_ACCESS_TOKEN = "sk.eyJ1Ijoia2Vsam91IiwiYSI6ImNrcnp4NDFlNDFlbWsyd283Z2k1N2Vha2cifQ.H0Mi7zLQsZ3CrT2znoN3Hg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirestoreActions firestoreActions = new FirestoreActions();
        firestoreActions.create();
    }
}