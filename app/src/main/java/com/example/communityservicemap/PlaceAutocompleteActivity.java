package com.example.communityservicemap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;

public class PlaceAutocompleteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_autocomplete);
//        Intent intent = new PlaceAutocomplete.IntentBuilder();
//                .accessToken(PUBLIC_ACCESS_TOKEN)
    }
}