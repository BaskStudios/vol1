package com.bask.studios.depremBilgi.activities;

import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import androidx.appcompat.app.AppCompatActivity;

import com.bask.studios.depremBilgi.R;

public class whatshoulddo extends AppCompatActivity {
    private AdView mAdView;

    @Override

    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.whatshoulddo);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        mAdView = findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

}
