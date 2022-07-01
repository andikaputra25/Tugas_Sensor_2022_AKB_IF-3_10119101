package com.myapp.myrestaurant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
// 10119101
// Andika Putra
// IF3

public class HomeFragment extends Fragment {

    // Menginisialisasikan variabel
    FusedLocationProviderClient client;
    private GoogleMap map;
    ArrayList<LatLng> arrayList = new ArrayList<LatLng>();
    LatLng Marker1 = new LatLng(-6.8938315219694415, 107.62786577606063);
    LatLng Marker2 = new LatLng(-6.892198426390764, 107.62742118191335);
    LatLng Market3 = new LatLng(-6.898848792058013, 107.62606700367115);
    LatLng Marker4 = new LatLng(-6.896263709105517, 107.63084360213763);
    LatLng Marker5 = new LatLng(-6.893494385218582, 107.62994237990723);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inisialisasi view
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // inisialisasi map Fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        // Initialize location client
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        arrayList.add(Marker1);
        arrayList.add(Marker2);
        arrayList.add(Market3);
        arrayList.add(Marker4);
        arrayList.add(Marker5);

        //singkron map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                //dimana map berhasil dijalankan
                map = googleMap;
                map.addMarker(new MarkerOptions().position(Marker1).title("Warung Nasi Mama Daud"));
                map.addMarker(new MarkerOptions().position(Marker2).title("Oishi dimsum"));
                map.addMarker(new MarkerOptions().position(Market3).title("Sate taichan ka-iko"));
                map.addMarker(new MarkerOptions().position(Marker4).title("Fast food restaurant"));
                map.addMarker(new MarkerOptions().position(Marker5).title("Warung nasi Tegal"));
                for (int i=0;i<arrayList.size();i++){
                    map.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                    map.moveCamera(CameraUpdateFactory.newLatLng(arrayList.get(i)));
                }
            }
        });

        // cek kondisi
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //jika lokasi diizinkan
            // memanggil method
            getCurrentLocation();
        }
        else {
            // jika lokasi tidak diizinkan
            // memanggil metod
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, 100);
        }

        // Return view
        return view;
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation()
    {
        // inisialisasi map fragment
        SupportMapFragment mapFragment=(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        // inisialisasi lokasi manager
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        // cek kondisi
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            client.getLastLocation().addOnCompleteListener(
                    task -> {
                        // Menginisialisasi lokasi
                        Location location = task.getResult();
                        // Cek Kondisi
                        if (location != null) {
                            // When location result is not null set latitude and longitude
                            mapFragment.getMapAsync(googleMap -> {
                                LatLng lokasi = new LatLng(location.getLatitude(),location.getLongitude());
                                MarkerOptions options = new MarkerOptions().position(lokasi).title("Lokasi Anda");
                                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasi,17));
                                googleMap.addMarker(options);
                            });
                        }
                        else {
                            LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10000).setFastestInterval(1000).setNumUpdates(1);
                            LocationCallback locationCallback = new LocationCallback() {
                                @Override
                                public void
                                onLocationResult(@NonNull LocationResult locationResult)
                                {
                                    mapFragment.getMapAsync(googleMap -> {
                                        LatLng lokasi = new LatLng(location.getLatitude(),location.getLongitude());
                                        MarkerOptions options = new MarkerOptions().position(lokasi).title("Lokasi Sekarang");
                                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasi,17));
                                        googleMap.addMarker(options);
                                    });
                                }
                            };

                            // mengupdate lokasi
                            client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        }
                    });
        }
        else {
            startActivity(
                    new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}