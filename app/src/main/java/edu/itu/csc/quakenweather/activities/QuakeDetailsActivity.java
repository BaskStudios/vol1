package edu.itu.csc.quakenweather.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import edu.itu.csc.quakenweather.R;
import edu.itu.csc.quakenweather.utilities.Utility;


public class QuakeDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quake_details);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#324857")));

        Intent intent = getIntent();

        TextView title = (TextView) findViewById(R.id.title_data);
        title.setText(intent.getStringExtra("Başlık"));

        TextView location = (TextView) findViewById(R.id.location_data);
        location.setText(intent.getStringExtra("Lokasyon"));

        TextView coordinates = (TextView) findViewById(R.id.coordinates_data);
        coordinates.setText(intent.getStringExtra("Koordinat"));

        TextView time = (TextView) findViewById(R.id.time_data);
        time.setText(intent.getStringExtra("Zaman"));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String distance = prefs.getString(this.getString(R.string.preference_distance_key), null);
        TextView depth = (TextView) findViewById(R.id.depth_data);
        depth.setText(Utility.getFormattedDepth(Utility.getConvertedDepth(intent.getDoubleExtra("Derinlik", 0), distance), distance));



        TextView significance = (TextView) findViewById(R.id.significance_data);
        significance.setText(intent.getStringExtra("Önem"));


        String url = intent.getStringExtra("url");
        TextView urlLink = (TextView) findViewById(R.id.url_link_data);
        urlLink.setMovementMethod(LinkMovementMethod.getInstance());
        urlLink.setText(Html.fromHtml(getResources().getString(R.string.link).replace("HREF", url)));

        final double longitude = intent.getDoubleExtra("Boylam", 0.0);
        final double latitude = intent.getDoubleExtra("Enlem", 0.0);


    }

}
