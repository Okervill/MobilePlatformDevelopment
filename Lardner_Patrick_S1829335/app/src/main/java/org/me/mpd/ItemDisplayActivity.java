//Patrick Lardner S1829335

package org.me.mpd;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class ItemDisplayActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MarkerOptions marker;
    private LatLng coords;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get display TextViews
        TextView item_title_display = findViewById(R.id.item_title);
        TextView item_description_display = findViewById(R.id.item_description);
        TextView item_link_display = findViewById(R.id.item_link);
        TextView item_date_display = findViewById(R.id.item_date);

        //Get Item details from Bundle
        Item i = getItemFromBundle();

        //Set display to Item details
        item_title_display.setText(i.getTitle());
        item_description_display.setText(i.getDescription());
        item_link_display.setText(i.getLink());

        //Format the date
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = formatter.format(i.getDate());

        item_date_display.setText("Date: " + formattedDate);

        Log.d("item", i.getType());

        marker = new MarkerOptions();
        coords = new LatLng(Double.parseDouble(i.getLongitude()), Double.parseDouble(i.getLatitude()));
        marker.position(coords);

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public Item getItemFromBundle() {

        //Get parameters from bundle individually since it's not a complex object
        Intent intent = getIntent();
        String item_title = intent.getStringExtra("title");
        String item_description = intent.getStringExtra("description");
        String item_link = intent.getStringExtra("link");
        String item_latitude = intent.getStringExtra("latitude");
        String item_longitude = intent.getStringExtra("longitude");
        String item_date_string = intent.getStringExtra("date");
        String item_type = intent.getStringExtra("type");

        String[] item_description_holder = item_description.split("<br />");
        item_description = String.join("\n", item_description_holder);

        //Thu Apr 07 00:00:00 GMT+01:00 2022
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        Date item_date = null;
        try {
            item_date = formatter.parse(item_date_string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("date", item_date.toString());
        Item item = new Item(item_title, item_description, item_link, item_latitude, item_longitude, item_date, item_type);

        return item;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Back button set up
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //Add marker to map
        googleMap.addMarker(marker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 13));
    }
}
