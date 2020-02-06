package edu.itu.csc.quakenweather.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.itu.csc.quakenweather.R;
import edu.itu.csc.quakenweather.models.Quake;
import edu.itu.csc.quakenweather.settings.SettingsActivity;
import edu.itu.csc.quakenweather.utilities.Utility;


public class QuakeMapActivity extends AppCompatActivity {
    private AdView mAdView;

    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quake_map);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#324857")));

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception exception) {
            Log.d(MainActivity.APP_TAG, "MapsInitializer Exception: " + exception.toString());
            exception.printStackTrace();
        }
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        drawMap(this, mMapView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mini, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh:
                drawMap(this, mMapView);
                break;
            case R.id.action_settings:
                startActivity(new Intent(QuakeMapActivity.this, SettingsActivity.class));
                break;
            default:
                Toast.makeText(this, "Böyle Bir Eylem Desteklenmiyor!", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * AsyncTask to fetch quake data from USGS.
     */
    public static class FetchQuakeMapDataTask extends AsyncTask<String, Void, List<Quake>> {
        private Context context;
        private MapView mMapView;
        private GoogleMap googleMap;
        private List<Quake> mResult;
        private ProgressDialog dialog;
        private Map<String, Quake> quakeInfoMap;

        public FetchQuakeMapDataTask(Context context, MapView mMapView) {
            this.context = context;
            this.mMapView = mMapView;
            quakeInfoMap = new HashMap<String, Quake>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "Harita Bilgisi", "USGS'den deprem verileri ile harita çizimi ... ", false);
        }

        @Override
        protected List<Quake> doInBackground(String... data) {
            String url = Utility.urlType.get("today");
            if (data != null
                    && data.length > 2) {
                return Utility.getQuakeData("MapActivity", Utility.urlType.get(data[1]), data[0], data[1], data[2], context);
            }
            return Utility.getQuakeData("MapActivity", url, Utility.DEFAULT_MAGNITUDE, Utility.DEFAULT_DURATION, Utility.DEFAULT_DISTANCE, context);
        }

        @Override
        protected void onPostExecute(List<Quake> result) {
            super.onPostExecute(result);
            mResult = result;
            if (dialog != null
                    && dialog.isShowing()) {
                dialog.dismiss();
            }

            if (mResult != null) {
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap) {
                        googleMap = mMap;

                        googleMap.clear();

                        // For showing a move to my location button
                        googleMap.setMyLocationEnabled(true);

                        googleMap.getUiSettings().setZoomControlsEnabled(true);

                        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                handleOnClickInfoWindowEvent(marker, context);
                            }
                        });

                        if (mResult != null && mResult.size() > 0) {
                            quakeInfoMap.clear();
                            for (Quake earthquakeInfo : mResult) {
                                quakeInfoMap.put(earthquakeInfo.getTitle(), earthquakeInfo);
                                try {
                                    long timestamp = Long.parseLong(earthquakeInfo.getTime());
                                    Marker marker = googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(earthquakeInfo.getLatitude(), earthquakeInfo.getLongitude()))
                                            .title(earthquakeInfo.getTitle())
                                            .snippet(earthquakeInfo.getMagnitude() + " magnitude : " + Utility.getDateTime(timestamp))
                                            .icon(BitmapDescriptorFactory.defaultMarker(Utility.getMarkerColorFromMagnitude(earthquakeInfo.getMagnitude()))));
                                } catch (Exception exception) {
                                    Log.e(MainActivity.APP_TAG, "getMapAsync Exception: " + exception.toString());
                                }
                            }
                        } else {
                            Toast.makeText(context, "Böyle bir veri bulunamadı", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(context, "İnternet Bağlantınızı Kontrol Ediniz!", Toast.LENGTH_LONG).show();
            }
        }

        /**
         * Display Quake Details when clicked on Info Window of Marker.
         *
         * @param marker
         * @param context
         */
        public void handleOnClickInfoWindowEvent(Marker marker, final Context context) {
            if (quakeInfoMap != null && quakeInfoMap.size() > 0) {
                Quake quakeInfo = quakeInfoMap.get(marker.getTitle());
                if (quakeInfo != null) {
                    LayoutInflater inflater = LayoutInflater.from(context);
                    final View detailedView = inflater.inflate(R.layout.activity_quake_details, null);

                    TextView title = (TextView) detailedView.findViewById(R.id.title_data);
                    title.setText(quakeInfo.getTitle());

                    TextView location = (TextView) detailedView.findViewById(R.id.location_data);
                    location.setText(quakeInfo.getFormattedPlace());

                    TextView coordinates = (TextView) detailedView.findViewById(R.id.coordinates_data);
                    coordinates.setText(quakeInfo.getFormattedCoordinates());

                    TextView time = (TextView) detailedView.findViewById(R.id.time_data);
                    time.setText(quakeInfo.getFormattedTime());

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    String distance = prefs.getString("Mesafe", null);
                    TextView depth = (TextView) detailedView.findViewById(R.id.depth_data);
                    depth.setText(Utility.getFormattedDepth(Utility.getConvertedDepth(quakeInfo.getDepth(), distance), distance));



                    TextView significance = (TextView) detailedView.findViewById(R.id.significance_data);
                    significance.setText(quakeInfo.getSignificance());


                    TextView urlLinkData = (TextView) detailedView.findViewById(R.id.url_link_data);
                    urlLinkData.setVisibility(View.GONE);
                    TextView urlLinkText = (TextView) detailedView.findViewById(R.id.url_link_text);
                    urlLinkText.setVisibility(View.GONE);

                    final double longitude = quakeInfo.getLongitude();
                    final double latitude = quakeInfo.getLatitude();



                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    final View titleView = inflater.inflate(R.layout.activity_title, null);
                    builder.setCustomTitle(titleView).setPositiveButton("OK", null);
                    builder.setView(detailedView);
                    AlertDialog dialogBox = builder.create();
                    dialogBox.show();
                }
            }
        }
    }


    private void drawMap(Context context, MapView mMapView) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String magnitude = preferences.getString(context.getString(R.string.preference_magnitude_key), null);
        String duration = preferences.getString(context.getString(R.string.preference_duration_key), null);
        String distance = preferences.getString(context.getString(R.string.preference_distance_key), null);
        new FetchQuakeMapDataTask(context, mMapView).execute(magnitude, duration, distance);
    }
}
