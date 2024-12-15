package com.example.triapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static double userLatitude = Double.NaN;
    public static double userLongitude = Double.NaN;


    private TextView locationTextView;

    private Map<String, List<String>> wasteMap;
    private List<String> wasteTypes;
    private ListView wasteListView;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        requestLocationPermissionIfNeeded();
        initWasteList();
        setupSearchFilter();
    }

    private void initViews() {
        locationTextView = findViewById(R.id.locationTextView);
        wasteListView = findViewById(R.id.listViewDechets);
        searchEditText = findViewById(R.id.searchEditText);
    }

    private void requestLocationPermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        } else {
            getUserLocation();
        }
    }

    private void initWasteList() {
        wasteMap = CsvReader.readWasteFromCsv(getResources(), R.raw.waste_data);
        wasteTypes = new ArrayList<>(wasteMap.keySet());
        WasteAdapter adapter = new WasteAdapter(this, wasteTypes, wasteMap);
        wasteListView.setAdapter(adapter);
    }

    private void setupSearchFilter() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterWasteTypes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void filterWasteTypes(String query) {
        List<String> filteredWasteTypes = new ArrayList<>();
        for (String wasteType : wasteTypes) {
            if (wasteType.toLowerCase().contains(query.toLowerCase())) {
                filteredWasteTypes.add(wasteType);
            }
        }

        WasteAdapter filteredAdapter = new WasteAdapter(MainActivity.this, filteredWasteTypes, wasteMap);
        wasteListView.setAdapter(filteredAdapter);
    }

    private void getUserLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    1,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            updateLocationText(latitude, longitude);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) { }

                        @Override
                        public void onProviderEnabled(@NonNull String provider) { }

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onProviderDisabled(@NonNull String provider) {
                            locationTextView.setText("Localisation: GPS désactivé");
                        }
                    }
            );
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateLocationText(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            userLatitude = latitude;
            userLongitude = longitude;
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String city = address.getLocality();
                if (city != null && !city.isEmpty()) {
                    locationTextView.setText("Vous êtes actuellement à " + city);
                } else {
                    locationTextView.setText("Ville inconnue");
                }
            } else {
                locationTextView.setText("Aucune adresse trouvée");
            }
        } catch (IOException e) {
            locationTextView.setText("Erreur lors de la récupération de la commune");
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getUserLocation();
        } else {
            locationTextView.setText("Localisation: Permission refusée");
        }
    }

}
