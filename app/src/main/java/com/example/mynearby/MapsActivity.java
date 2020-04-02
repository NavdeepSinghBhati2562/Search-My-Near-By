package com.example.mynearby;

import androidx.fragment.app.FragmentActivity;

import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button myLoc;
    EditText searchEdt;
    String search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        myLoc=findViewById(R.id.myLoc);
        searchEdt=findViewById(R.id.searchEdt);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    myLoc.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        mMap.clear();
        graphicsDisplay();

        SmartLocation.with(MapsActivity.this).location()
                .continuous()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {

                        LatLng myLatlng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(myLatlng).title("me"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatlng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatlng, 18f));

                        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.addCircle(new CircleOptions().fillColor(R.color.myLoc).strokeColor(Color.DKGRAY).center(latLng).radius(1500));



                        search=searchEdt.getText().toString();

                        AndroidNetworking.initialize(MapsActivity.this);
                        AndroidNetworking.get("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+location.getLatitude()+","+location.getLongitude()+"&radius=1500&type="+search+"&key=AIzaSyClfG1_3UgxE97yN7DE5CUlgLhORvTSQoU")
                                .setPriority(Priority.HIGH)
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try {
                                            JSONArray jsonArray=response.getJSONArray("results");

                                            for (int i=0;i<jsonArray.length();i++)
                                            {
                                                JSONObject objResult = jsonArray.getJSONObject(i);
                                                String name = objResult.getString("name");
                                                JSONObject  objGeometry=objResult.getJSONObject("geometry");
                                                JSONObject objLoc=objGeometry.getJSONObject("location");
                                              //  String lat=String.valueOf(objLoc.getDouble("lat")),lng=String.valueOf(objLoc.getDouble("lng"));

                                                LatLng myLatlng = new LatLng(objLoc.getDouble("lat"), objLoc.getDouble("lng"));
                                                mMap.addMarker(new MarkerOptions().position(myLatlng).title(name));


//                                                Log.d("name", name);

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        Toast.makeText(MapsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }
                 });
                }
            });



    }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void graphicsDisplay() {

        LatLng latLng=new LatLng(36.7783,119.4179);
        mMap.addCircle(new CircleOptions().fillColor(Color.BLUE).strokeColor(Color.GRAY).center(latLng).radius(90000));

    }
}
