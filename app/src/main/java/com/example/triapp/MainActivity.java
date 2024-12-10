package com.example.triapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import android.location.Address;
import android.location.Geocoder;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private TextView locationTextView;
    private LocationManager locationManager;

    private Map<String, List<String>> dechetsMap;
    private ListView listViewDechets;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the location text view
        locationTextView = findViewById(R.id.locationTextView);

// Request location permissions if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getUserLocation();
        }

        // Initialiser les vues
        listViewDechets = findViewById(R.id.listViewDechets);
        searchEditText = findViewById(R.id.searchEditText);

        // Charger les données depuis le fichier CSV
        dechetsMap = CSVReader.readDechetsFromCSV(getResources(), R.raw.dechets);

        // Extraire les types de déchets
        List<String> typesDechets = new ArrayList<>(dechetsMap.keySet());

        // Configurer l'adaptateur personnalisé
        DechetAdapter adapter = new DechetAdapter(this, typesDechets, dechetsMap);
        listViewDechets.setAdapter(adapter);

        // Ajout d'un écouteur pour rechercher dynamiquement
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Filtrer les types de déchets
                List<String> filteredDechets = new ArrayList<>();
                for (String typeDechet : typesDechets) {
                    if (typeDechet.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredDechets.add(typeDechet);
                    }
                }

                // Mettre à jour l'adapter
                DechetAdapter filteredAdapter = new DechetAdapter(MainActivity.this, filteredDechets, dechetsMap);
                listViewDechets.setAdapter(filteredAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    public void afficherPointsCollecte(String typeDechet) {
        // Obtenir les points de collecte pour ce type de déchet
        List<String> pointsCollecte = dechetsMap.get(typeDechet);

        // Grouper les points de collecte par commune
        Map<String, List<String>> pointsParCommune = grouperParCommune(pointsCollecte);

        // Configurer le RecyclerView dans une boîte de dialogue
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = LayoutInflater.from(this).inflate(R.layout.dialog_poubelle_info, null);
        builder.setView(customView);

        RecyclerView recyclerView = customView.findViewById(R.id.recyclerViewCollecte);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new PointCollecteAdapter(this, pointsParCommune));

        builder.setPositiveButton("Fermer", null);
        builder.show();
    }

    private Map<String, List<String>> grouperParCommune(List<String> pointsCollecte) {
        Map<String, List<String>> map = new HashMap<>();
        for (String point : pointsCollecte) {
            String[] parts = point.split(", Commune : "); // Assurez-vous que ce format correspond à vos données
            String adresse = parts[0].trim();
            String commune = parts.length > 1 ? parts[1].trim() : "Inconnue";

            if (!map.containsKey(commune)) {
                map.put(commune, new ArrayList<>());
            }
            map.get(commune).add(adresse);
        }
        return map;
    }
    private void getUserLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    // Use Geocoder to get the nearest city
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (addresses != null && !addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            String city = address.getLocality(); // Get the city name
                            if (city != null && !city.isEmpty()) {
                                locationTextView.setText("Vous êtes à " + city);
                            } else {
                                locationTextView.setText("Ville inconnue");
                            }
                        } else {
                            locationTextView.setText("Aucune adresse trouvée");
                        }
                    } catch (IOException e) {
                        locationTextView.setText("Erreur lors de la récupération de la ville");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) { }

                @Override
                public void onProviderEnabled(@NonNull String provider) { }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                    locationTextView.setText("Localisation : GPS désactivé");
                }
            });
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                locationTextView.setText("Localisation : Permission refusée");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
