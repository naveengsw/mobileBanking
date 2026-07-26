package com.example.mobilebanking.android;

import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class BranchLocateActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_locate);
        
        Log.d("BranchLocate", "onCreate: Adding SupportMapFragment programmatically");
        
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.map_container, mapFragment)
                .commit();
        
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("BranchLocate", "onMapReady: Map is ready");
        mMap = googleMap;

        // Force UI updates to see if anything changes
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // Add some dummy branches
        LatLng branch1 = new LatLng(37.7749, -122.4194); // San Francisco
        LatLng branch2 = new LatLng(34.0522, -118.2437); // Los Angeles
        LatLng branch3 = new LatLng(40.7128, -74.0060);  // New York

        mMap.addMarker(new MarkerOptions().position(branch1).title("SF Main Branch"));
        mMap.addMarker(new MarkerOptions().position(branch2).title("LA Downtown Branch"));
        mMap.addMarker(new MarkerOptions().position(branch3).title("NYC Wall St Branch"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(branch1, 10.0f));
    }
}
