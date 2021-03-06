package com.bask.studios.depremBilgi.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bask.studios.depremBilgi.R;
import com.bask.studios.depremBilgi.adapters.QuakeAdapter;
import com.bask.studios.depremBilgi.models.Quake;
import com.bask.studios.depremBilgi.settings.SettingsActivity;
import com.bask.studios.depremBilgi.utilities.Utility;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String APP_TAG = "SWE690_CAPSTONE_PROJECT";

    private static ArrayAdapter<Quake> quakeAdapter = null;

    private TextView updateTime;
    private TextView updateFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getString(R.string.title_activity_main));



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateTime = (TextView) findViewById(R.id.update_time);
        updateFilter = (TextView) findViewById(R.id.update_filter);

        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preference_general, false);

        quakeAdapter = new QuakeAdapter(this, R.layout.list_item_quake, new ArrayList<Quake>());

        // Get a reference to the ListView and attach this adapter to it.
        ListView listView = (ListView) findViewById(R.id.listview_quake);
        listView.setAdapter(quakeAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (quakeAdapter != null
                        && quakeAdapter.getItem(i) != null) {
                    Quake quake = quakeAdapter.getItem(i);
                    Log.d(MainActivity.APP_TAG, "Quake Selected : " + quake);
                    Intent intent = new Intent(MainActivity.this, QuakeDetailsActivity.class);
                    intent.putExtra("Başlık", quake.getTitle());
                    intent.putExtra("Lokasyon", quake.getFormattedPlace()); // lokasyon burdan geliyor
                    intent.putExtra("Koordinat", quake.getFormattedCoordinates());
                    intent.putExtra("Zaman", quake.getFormattedTime());
                    intent.putExtra("Derinlik", quake.getDepth());
                    intent.putExtra("eventid", quake.getEventId());
                    intent.putExtra("Önem", quake.getSignificance());
                    intent.putExtra("Durum", quake.getStatus());
                    intent.putExtra("url", quake.getUrl());
                    intent.putExtra("Boylam", quake.getLongitude());
                    intent.putExtra("Enlem", quake.getLatitude());
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                fetchLatestQuakes(this);
                break;
            case R.id.action_map:
                startActivity(new Intent(MainActivity.this, QuakeMapActivity.class));
                break;
            case R.id.action_statistics:
                startActivity(new Intent(MainActivity.this, QuakeStatisticsActivity.class));
                break;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.action_search:
                startActivity(new Intent(MainActivity.this, QuakeSearchActivity.class));
                break;
            default:
                Toast.makeText(this, "Böyle Bir Eylem Desteklenmiyor!", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_about) {
            startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Dene " + getString(R.string.app_name) + "!!");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "[Type your message here]");
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        } else if (id == R.id.nav_feedback) {
            Toast.makeText(getApplicationContext(), "Veri paylaşım izinleri için Ayarlar'ı kontrol edin", Toast.LENGTH_LONG).show();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean canShareDeviceInformation = prefs.getBoolean(getResources().getString(R.string.preference_device_information_key), true);
            boolean canShareErrorInformation = prefs.getBoolean(getResources().getString(R.string.preference_error_information_key), true);
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.email_address), null));
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " : Feedback (" + formatter.format(new Date()) + ")");
            intent.putExtra(Intent.EXTRA_TEXT, "[Type your message here]" + Utility.getDebuggingInformation(getApplicationContext(), canShareDeviceInformation, canShareErrorInformation));
            startActivity(Intent.createChooser(intent, "Choose an Email client : "));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchLatestQuakes(this);
        Utility.updateLastViewed(MainActivity.class.getSimpleName(), this);
    }


    /**
     * Fetch latest quake data.
     *
     * @param context
     */
    private void fetchLatestQuakes(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String magnitude = prefs.getString(context.getString(R.string.preference_magnitude_key), null);
        String duration = prefs.getString(context.getString(R.string.preference_duration_key), null);
        String distance = prefs.getString(context.getString(R.string.preference_distance_key), null);
        new FetchLatestQuakeTask(context).execute(magnitude, duration, distance);
        updateFooter();
    }

    /**
     * Update footer with user filters and last updated time.
     *
     */
    private void updateFooter() {
        Date date = new Date();
        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String magnitude = prefs.getString(this.getString(R.string.preference_magnitude_key), null);
        String duration = prefs.getString(this.getString(R.string.preference_duration_key), null);
        updateTime.setText(("Son Güncelleme: " + timeFormatter.format(date)).toString());
    }

    /**
     * AsyncTask to fetch latest quake data from USGS.
     */
    public static class FetchLatestQuakeTask extends AsyncTask<String, Void, List<Quake>> {

        private ProgressDialog dialog;
        private Context context;

        public FetchLatestQuakeTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(context, "En Son Deprem Verileri", "USGS'den son deprem verileri ... ", false);
        }

        @Override
        protected List<Quake> doInBackground(String... data) {
            String url = Utility.urlType.get("today");
            if (data != null
                    && data.length > 2) {
                return Utility.getQuakeData("MainActivity", Utility.urlType.get(data[1]), data[0], data[1], data[2], context);
            }
            return Utility.getQuakeData("MainActivity", url, Utility.DEFAULT_MAGNITUDE, Utility.DEFAULT_DURATION, Utility.DEFAULT_DISTANCE, context);
        }

        @Override
        protected void onPostExecute(List<Quake> result) {
            super.onPostExecute(result);
            if (dialog != null
                    && dialog.isShowing()) {
                dialog.dismiss();
            }

            if (result != null
                    && quakeAdapter != null) {
                quakeAdapter.clear();
                if (result.size() > 0) {
                    for (Quake quakeInfo : result) {
                        quakeAdapter.add(quakeInfo);
                    }
                } else {
                    Toast.makeText(context, "Verilen filtrelerle veri bulunamadı!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "İnternet Bağlantınızı Kontrol edin ve Tekrar Deneyin", Toast.LENGTH_LONG).show();
            }
        }
    }


}

