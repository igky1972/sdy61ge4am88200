package com.igkyrtis.sdy61ge4am88200;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Tab3_Fragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "Tab3Fragment";

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;


    LocationManager locationManager;

    LocationListener locationListener;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab3_fragment, container, false);
        mView = rootView;
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.mapTab3);
        if (mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(37.968490, 23.728538))
                .title("Μουσείο Ακροπόλεως, Διονυσίου Αρεοπαγίτου 15, Αθήνα 117 42")
                .snippet("5* Πρέπει να το επισκεφτείτε οπωσδήποτε!")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.museum))
        );

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(37.968274, 23.729454))
                .title("Εστιατόριο ΑΡΚΑΔΙΑ, Διονυσίου Αρεοπαγίτου 27, Αθήνα 117 42")
                .snippet("4.5* Πολύ καλή Ελληνική κουζίνα!")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant))
        );

        CameraPosition LibertyCam = CameraPosition.builder().target(new LatLng(37.968478, 23.729266)).zoom(18).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(LibertyCam));
    }

}
