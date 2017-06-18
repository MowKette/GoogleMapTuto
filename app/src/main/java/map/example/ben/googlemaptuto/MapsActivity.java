package map.example.ben.googlemaptuto;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.androidmapsextensions.*;
import com.google.android.gms.vision.text.Text;


import android.Manifest;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements com.androidmapsextensions.OnMapReadyCallback {

    //private GoogleMap mMap;
    private com.androidmapsextensions.GoogleMap mMap;


    LocationManager locationManager;
    PendingIntent pendingIntent;
    SharedPreferences sharedPreferences;

    int locationCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getExtendedMapAsync(this);



        /*final Button buttonGo = (Button) findViewById(R.id.buttonGo);
        buttonGo.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                for(int i=0; i<allMarkers.size(); i++) {
                    Toast.makeText(getBaseContext(), "This is allMarkers'ID = " + allMarkers.get())
                }

            }
        });*/

        final ToggleButton buttonAdd = (ToggleButton) findViewById(R.id.buttonMarker);
        buttonAdd.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                if(buttonAdd.isChecked()) {
                    mMap.setOnMapClickListener(new com.androidmapsextensions.GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng point) {

                            if(mMap != null) {

                                locationCount++;

                                drawMarker(point);
                                drawCircle(point);

                                // This intent will call the activity ProximityActivity
                                //Intent proximityIntent = new Intent("map.example.ben.googlemaptuto.activity.proximity");
                                Intent proximityIntent = new Intent(MapsActivity.this, ProximityActivity.class);


                                // Passing latitude and longitude to the PendingActivity
                                proximityIntent.putExtra("lat", point.latitude);
                                proximityIntent.putExtra("lng", point.longitude);
                                proximityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                // Creating a pending intent which will be invoked by LocationManager when the specified region is
                                // entered or exited
                                pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, proximityIntent, 0);

                                if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    locationManager.addProximityAlert(point.latitude, point.longitude, 50, -1, pendingIntent);
                                } else {
                                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                                    if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                        locationManager.addProximityAlert(point.latitude, point.longitude, 50, -1, pendingIntent);
                                    }
                                }

                                /* Opening the editor object to write data to sharedPreferences */
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                // Storing the latitude for the i-th location
                                editor.putString("lat"+ Integer.toString((locationCount-1)), Double.toString(point.latitude));

                                // Storing the longitude for the i-th location
                                editor.putString("lng"+ Integer.toString((locationCount-1)), Double.toString(point.longitude));

                                // Storing the count of locations or marker count
                                editor.putInt("locationCount", locationCount);

                                /* Storing the zoom level to the shared preferences */
                                editor.putString("zoom", Float.toString(mMap.getCameraPosition().zoom));

                                /* Saving the values stored in the shared preferences */
                                Boolean editResult = editor.commit();

                                if(editResult == true) {
                                    Toast.makeText(getBaseContext(), "Proximity Alert is added", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getBaseContext(), "Proximity Alert not added", Toast.LENGTH_SHORT).show();
                                }
                            }

                            System.out.println(point.latitude+"---"+point.longitude);

                        }
                    });
                }
                else {
                    System.out.println("Don't do anything");
                    mMap.setOnMapClickListener(null);
                }

            }
        });
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(com.androidmapsextensions.GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            Log.d("MyApp","success");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                Log.d("MyApp", "Granted");
            }
        }


        // Getting LocationManager object from System Service LOCATION_SERVICE
        //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Opening the sharedPreferences object
        sharedPreferences = getSharedPreferences("location", 0);

        // Getting number of locations already stored
        locationCount = sharedPreferences.getInt("locationCount", 0);

        // Getting stored zoom level if exists else return 0
        String zoom = sharedPreferences.getString("zoom", "0");

        // If locations are already saved
        if(locationCount!=0){

            Toast.makeText(getBaseContext(), "I counted locations", Toast.LENGTH_SHORT).show();

            String lat = "";
            String lng = "";

            // Iterating through all the locations stored
            for(int i=0;i<locationCount;i++){

                // Getting the latitude of the i-th location
                lat = sharedPreferences.getString("lat"+i,"0");

                // Getting the longitude of the i-th location
                lng = sharedPreferences.getString("lng"+i,"0");

                if(mMap != null) {

                    //Toast.makeText(getBaseContext(), "mMap isn't null", Toast.LENGTH_SHORT).show();

                    // Drawing marker on the map
                    drawMarker(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));

                    // Drawing circle on the map
                    drawCircle(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
                }

            }

            if(mMap != null) {
                // Moving CameraPosition to last clicked position
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))));

                // Setting the zoom level in the map on last position  is clicked
                mMap.animateCamera(CameraUpdateFactory.zoomTo(Float.parseFloat(zoom)));
            }

        }

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoContents(Marker marker) {

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.info_window, null);

                // Getting the position from the marker
                LatLng latLng = marker.getPosition();

                // Getting reference to the TextView to set latitude
                TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);

                // Getting reference to the TextView to set longitude
                TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);

                TextView tvTitle = (TextView) v.findViewById(R.id.tv_title);
                final TextView tvRange = (TextView) v.findViewById(R.id.tv_range);
                TextView tvHour = (TextView) v.findViewById(R.id.tv_hour);
                TextView tvMinute = (TextView) v.findViewById(R.id.tv_minute);

                // Setting the latitude
                tvLat.setText("Latitude:" + latLng.latitude);

                // Setting the longitude
                tvLng.setText("Longitude:"+ latLng.longitude);

                tvTitle.setText(marker.getTitle());

                MarkerInfo markerInfo = (MarkerInfo) marker.getTag();
                int range = markerInfo.getRange();
                int hour = markerInfo.getHour();
                int minute = markerInfo.getMinute();

                String title = markerInfo.getTitle();

                if(markerInfo != null) {

                    tvTitle.setText(title);
                    tvRange.setText(String.valueOf(range));
                    tvHour.setText(String.valueOf(hour));
                    tvMinute.setText(String.valueOf(minute));
                }

                return v;
            }

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }
        });

        mMap.setOnMapLongClickListener(new com.androidmapsextensions.GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                //Intent proximityIntent = new Intent("map.example.ben.googlemaptuto.activity.proximity");
                Intent proximityIntent = new Intent(MapsActivity.this, ProximityActivity.class);
                proximityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, proximityIntent, 0);

                // Removing the proximity alert
                if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.removeProximityAlert(pendingIntent);
                    Toast.makeText(getBaseContext(), "Proximity Alert is removed", Toast.LENGTH_SHORT).show();
                } else {
                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                    if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.removeProximityAlert(pendingIntent);
                        Toast.makeText(getBaseContext(), "Proximity Alert is removed", Toast.LENGTH_SHORT).show();
                    }
                }

                if(mMap != null) {
                    // Removing the marker and circle from the Google Map
                    mMap.clear();
                }


                // Opening the editor object to delete data from sharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Clearing the editor
                editor.clear();

                // Committing the changes
                editor.commit();

                //Toast.makeText(getBaseContext(), "Proximity Alert is removed", Toast.LENGTH_LONG).show();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                /*final Button buttonTest = (Button) findViewById(R.id.buttonTest);
                buttonTest.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        MarkerInfo markerInfo = (MarkerInfo) marker.getTag();
                        markerInfo.setRange(100);
                    }
                });*/

                return false;
            }
        });

    }

    public void onMapSearch(View view) {
        System.out.print("Working so far 4");
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        List<Address>addressList = null;

        System.out.print("Working so far 3");

        if (!location.equals("")) {
            System.out.print("Working so far 2");
            Geocoder geocoder = new Geocoder(this);
            try {
                System.out.print("Working so far");
                addressList = geocoder.getFromLocationName(location, 1);
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                System.out.print("Still working");
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Wrong address", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, MapsActivity.class));
                finish();
            }

            /*Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));*/
        }
    }


    public void onCircleClick(CircleOptions circleOptions){

    }

    public void drawCircle(LatLng point){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(50);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLUE);

        // Fill color of the circle
        //circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);
    }

    public void drawMarker(LatLng point){
        // Creating an instance of MarkerOptions
        com.androidmapsextensions.MarkerOptions markerOptions = new com.androidmapsextensions.MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);

        // Adding InfoWindow title
        markerOptions.title("Marker");

        // Adding InfoWindow contents
        markerOptions.snippet(Double.toString(point.latitude) + "," + Double.toString(point.longitude));

        // Adding marker on the Google Map
        Marker marker = mMap.addMarker(markerOptions);
        MarkerInfo markerInfo = new MarkerInfo();
        markerInfo.setHour(0);
        markerInfo.setMinute(0);
        markerInfo.setId(marker.getId());
        markerInfo.setRange(50);
        markerInfo.setTitle("Marker");
        marker.setTag(markerInfo);
        //allMarkers.put(marker.getId(), markerOptions);
    }

    public static class MarkerInfo implements Serializable {
        public int hour, minute, range;
        public String title, id;

        public void setId(String id) {
            this.id = id;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        public void setRange(int range){
            this.range = range;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getId() {
            return id;
        }

        public int getHour() {
            return hour;
        }

        public int getMinute() {
            return  minute;
        }

        public int getRange() { return range; }

        public String getTitle() {
            return title;
        }
    }
}
