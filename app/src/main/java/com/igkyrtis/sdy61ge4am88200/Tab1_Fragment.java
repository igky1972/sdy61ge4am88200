package com.igkyrtis.sdy61ge4am88200;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

//public class Tab1_Fragment extends Fragment implements OnMapReadyCallback {
public class Tab1_Fragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "Tab1Fragment";

    final private int REQUEST_COURSE_ACCESS = 123;
    boolean permissionGranted = false;
    private GoogleMap mGoogleMap;
    private Button btnShowLocation;
    private LatLng pointLatLon;
    private double latitude;
    private double longitude;

    private LocationManager lm;
    private LocationListener locationListener;
    private Location mloc;
    private MapView mMapView;
    private View mView;
    private Geocoder geoCoder;


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1_fragment, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapTab1);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

        btnShowLocation = (Button) rootView.findViewById(R.id.buttonDeviceLocation);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String addr = "Leof. Al. Panagouli 141,\nIlioupoli 163 43, Greece,\n";
                addr = addr + "Lat: 37.935199,\n";
                addr = addr + "Lon: 23.763854,\n";
                addr = addr + "Acccuracy: 10m, \n";
                addr = addr + "Speed: - m/s.";
                Toast.makeText(getActivity().getBaseContext(), addr, Toast.LENGTH_SHORT).show();

            }
        });

        GPSTracker gps = new GPSTracker(this.getContext());

        // check if GPS enabled
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            // \n is for new line
            Toast.makeText(getActivity().getBaseContext(), "Your Location is - \nLat: "
                    + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        mView = rootView;
        return rootView;
    }

    @NonNull
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        pointLatLon = new LatLng(37.935264, 23.763796);

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Geocoder geoCoder = new Geocoder(getActivity().getBaseContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    String addr = "";
                    if (addresses.size() > 0) {
                        for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++)
                            addr += addresses.get(0).getAddressLine(i) + ",\n";
                    }
                    addr = addr + "Lat: " + String.valueOf(latLng.latitude) + ",\n";
                    addr = addr + "Lon: " + String.valueOf(latLng.longitude) + ",\n";
                    addr = addr + "Acccuracy: 10m, \n";
                    addr = addr + "Speed: - m/s.";

                    Toast.makeText(getActivity().getBaseContext(), addr, Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        googleMap.addMarker(new MarkerOptions().position(new LatLng(37.935264, 23.763796)).title("Home Sweet Home").snippet("Nothing like home."));
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(super.getActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COURSE_ACCESS);
                return;
            } else {
                permissionGranted=true;
            }
        if (permissionGranted) {
            //update location every 10sec in 10m radius with both provider GPS and Network.
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
            if (mloc == null) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,10, locationListener);
                Log.d("GPS Enabled", "GPS Enabled");
                if (lm!= null) {
                    mloc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (mloc!= null) {
                        latitude = mloc.getLatitude();
                        longitude = mloc.getLongitude();
                    }
                }
            }
        }

        CameraPosition HomeCam = CameraPosition.builder().target(new LatLng(37.935264, 23.763796)).zoom(16).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(HomeCam));
    }


    private class MyLocationListener implements LocationListener{

        public void onLocationChanged(Location loc) {
            if (loc != null) {
                Toast.makeText(getActivity().getBaseContext(),"Location changed : Lat:" + String.valueOf(loc.getLatitude()) + " Lng: " + String.valueOf(loc.getLongitude()), Toast.LENGTH_SHORT).show();
            }
            LatLng p = new LatLng(loc.getLatitude(),loc.getLongitude());
            pointLatLon =p;
            mloc = loc;
            String sLatLong = "Lat: " + String.valueOf(p.latitude) + ", Lon:" + String.valueOf(p.longitude);
            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(),loc.getLongitude())).title(""));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(p));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {
        }

    }

}
