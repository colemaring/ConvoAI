package com.example.project;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Collections;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class MainActivity extends AppCompatActivity {

    public static final int DEFAULT_UPDATE_INTEVERAL = 5000;
    public static final int PERMISSIONS_FINE_LOCATION = 99;
    TextView tv_places, tv_response1, tv_response2, tv_response3, changeLocationText;
    Button generateButton, changeLocationButton, place1Button, place2Button, place3Button, place4Button, invisCloseMenu;
    ImageButton changeLocationPopupCloseButton, refreshLocationButton;
    View changeLocationPopup, changeLocationBarView;
    BlurView blurView, blurViewStart;
    Switch sw_gps;

    // config file for settings related to FusedLocationProviderClient
    LocationRequest locationRequest;

    LocationCallback locationCallBack;

    // Google's API for location services
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // give each UI variable a value
        tv_places = findViewById(R.id.tv_places);
        changeLocationText = findViewById(R.id.changeLocationText);
        place1Button = findViewById(R.id.place1Button);
        place2Button = findViewById(R.id.place2Button);
        place3Button = findViewById(R.id.place3Button);
        place4Button = findViewById(R.id.place4Button);
        changeLocationPopupCloseButton = findViewById(R.id.changeLocationPopupCloseButton);
        tv_response1 = findViewById(R.id.tv_response1);
        tv_response2 = findViewById(R.id.tv_response2);
        tv_response3 = findViewById(R.id.tv_response3);
        sw_gps = findViewById(R.id.sw_gps);
        generateButton = findViewById(R.id.generateButton);
        changeLocationPopup = findViewById(R.id.changeLocationPopup);
        changeLocationButton = findViewById(R.id.changeLocationButton);
        changeLocationBarView = findViewById(R.id.changeLocationBarView);
        refreshLocationButton = findViewById(R.id.refreshLocationButton);
        invisCloseMenu = findViewById(R.id.invisCloseMenu);
        blurView = findViewById(R.id.blurView);
        blurViewStart = findViewById(R.id.blurViewStart);

//        ShimmerFrameLayout container = (ShimmerFrameLayout) findViewById(R.id.shimmerview);
//        container.startShimmer();



        // set properties of LocationRequest
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, DEFAULT_UPDATE_INTEVERAL)
                .setMaxUpdateDelayMillis(1000)
                .build();
        // event that is triggered whenever the update interval is met
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
        };

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blurViewStart.setVisibility(View.INVISIBLE);
                Network networkTask1 = new Network(tv_response1, tv_places.getText().toString());
                Network networkTask2 = new Network(tv_response2, tv_places.getText().toString());
                Network networkTask3 = new Network(tv_response3, tv_places.getText().toString());
                networkTask1.execute();
                networkTask2.execute();
                networkTask3.execute();
            }
        });

        changeLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleChangeLocationPopup(place1Button.getText().toString(), place2Button.getText().toString(), place3Button.getText().toString(), place4Button.getText().toString());
                blurBackgound();
            }
        });

        invisCloseMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleChangeLocationPopup("dummy", "dummy", "dummy", "dummy");
            }
        });

        changeLocationPopupCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleChangeLocationPopup("dummy", "dummy", "dummy", "dummy");
            }
        });

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()) {
                    locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, DEFAULT_UPDATE_INTEVERAL)
                            .setMaxUpdateDelayMillis(1000)
                            .build();
                    // using device gps sensors;
                } else {
                    locationRequest = new LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, DEFAULT_UPDATE_INTEVERAL)
                            .setMaxUpdateDelayMillis(1000)
                            .build();
                    // using towers and wifi triangulation
                }
            }
        });

        place1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_places.setText(place1Button.getText().toString());
                toggleChangeLocationPopup("dummy", "dummy", "dummy", "dummy");
            }
        });

        place2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_places.setText(place2Button.getText().toString());
                toggleChangeLocationPopup("dummy", "dummy", "dummy", "dummy");
            }
        });

        place3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_places.setText(place3Button.getText().toString());
                toggleChangeLocationPopup("dummy", "dummy", "dummy", "dummy");
            }
        });

        place4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_places.setText(place4Button.getText().toString());
                toggleChangeLocationPopup("dummy", "dummy", "dummy", "dummy");
            }
        });

        refreshLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerLocationUpdate("NOTinitial");
            }
        });

        blurOnStart();
        updateGPS();
        triggerLocationUpdate("initial");


    } // end onCreate method

    private void blurOnStart() {
        float radius = 5f;

        View decorView = getWindow().getDecorView();
        // ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);

        // Optional:
        // Set drawable to draw in the beginning of each blurred frame.
        // Can be used in case your layout has a lot of transparent space and your content
        // gets a too low alpha value after blur is applied.
        Drawable windowBackground = decorView.getBackground();

        blurViewStart.setupWith(rootView, new RenderScriptBlur(this)) // or RenderEffectBlur
                .setFrameClearDrawable(windowBackground) // Optional
                .setBlurRadius(radius);

        blurViewStart.setVisibility(View.VISIBLE);
    }

    private void blurBackgound() {
        float radius = 5f;

        View decorView = getWindow().getDecorView();
        // ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);

        // Optional:
        // Set drawable to draw in the beginning of each blurred frame.
        // Can be used in case your layout has a lot of transparent space and your content
        // gets a too low alpha value after blur is applied.
        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView, new RenderScriptBlur(this)) // or RenderEffectBlur
                .setFrameClearDrawable(windowBackground) // Optional
                .setBlurRadius(radius);

        blurView.setVisibility(View.VISIBLE);
    }

    private void triggerLocationUpdate(String initCall) {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);

        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Collections.singletonList(Place.Field.NAME);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), "nope");

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        tv_places.setText("Loading...");

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    FindCurrentPlaceResponse response = task.getResult();
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                    }
                    if (initCall.equals("initial"))
                        tv_places.setText(response.getPlaceLikelihoods().get(0).getPlace().getName());
                    place1Button.setText(response.getPlaceLikelihoods().get(0).getPlace().getName());
                    place2Button.setText(response.getPlaceLikelihoods().get(1).getPlace().getName());
                    place3Button.setText(response.getPlaceLikelihoods().get(2).getPlace().getName());
                    place4Button.setText(response.getPlaceLikelihoods().get(3).getPlace().getName());

                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        } else {
            updateGPS();
        }

        updateGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    updateGPS();
                else{
                    Toast.makeText(this, "This app requires perms blahblah", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
    private void updateGPS(){
        // get permissions from the user to track GPS
        // get the current location from the fused client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // user provided the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // we got permissions, put values of location into the UI
                }
            });
        }
        else{
            // permission was not granted yet
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                requestPermissions(new String[] {ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    public void toggleChangeLocationPopup(String place1, String place2, String place3, String place4)
    {
        if (changeLocationPopup.getVisibility() == View.VISIBLE) {
            changeLocationPopup.setVisibility(View.GONE);
            place1Button.setVisibility(View.GONE);
            place2Button.setVisibility(View.GONE);
            place3Button.setVisibility(View.GONE);
            place4Button.setVisibility(View.GONE);
            changeLocationText.setVisibility(View.GONE);
            changeLocationPopupCloseButton.setVisibility(View.GONE);
            changeLocationBarView.setVisibility(View.GONE);
            refreshLocationButton.setVisibility(View.GONE);
            invisCloseMenu.setVisibility(View.GONE);
            blurView.setVisibility(View.INVISIBLE);

        } else {
            changeLocationPopup.setVisibility(View.VISIBLE);
            place1Button.setVisibility(View.VISIBLE);
            place1Button.setText(place1);
            place2Button.setVisibility(View.VISIBLE);
            place2Button.setText(place2);
            place3Button.setVisibility(View.VISIBLE);
            place3Button.setText(place3);
            place4Button.setVisibility(View.VISIBLE);
            place4Button.setText(place4);
            changeLocationText.setVisibility(View.VISIBLE);
            changeLocationPopupCloseButton.setVisibility(View.VISIBLE);
            changeLocationBarView.setVisibility(View.VISIBLE);
            refreshLocationButton.setVisibility(View.VISIBLE);
            invisCloseMenu.setVisibility(View.VISIBLE);
        }
    }
}