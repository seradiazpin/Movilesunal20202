package unal.edu.co.geolocalizacion;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;
    private LatLng latLng;
    private String radius;
    TextView tvProgressLabel;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager( ).findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );
        radius = "500";
        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        int progress = seekBar.getProgress();
        tvProgressLabel = findViewById(R.id.textView);
        tvProgressLabel.setText("Radius: " + progress);
    }


    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            tvProgressLabel.setText("Raduis: " + progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
            mMap.clear( );
            radius = Integer.toString(seekBar.getProgress());
            onMapReady( mMap );
            tvProgressLabel.setText("Raduis: " + seekBar.getProgress());
        }
    };
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                    break;
                }
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }
    @Override
    public void onMapReady( GoogleMap googleMap ){
        mMap = googleMap;
        enableMyLocation();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location locationGPS) {
                        // Got last known location. In some rare situations this can be null.
                        if (locationGPS != null) {
                            LatLng pos = new LatLng(locationGPS.getLatitude(),locationGPS.getLongitude());
                            latLng = new LatLng(locationGPS.getLatitude(), locationGPS.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 20));
                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(locationGPS.getLatitude(), locationGPS.getLongitude()))
                                    .radius(Double.parseDouble(radius))
                                    .strokeColor(ContextCompat.getColor(getContext(), R.color.circleBorder))
                                    .fillColor(ContextCompat.getColor(getContext(), R.color.circleFill)));

                            MapsController mc = new MapsController( );

                            String location = new StringBuilder( ).append( latLng.latitude ).append( "," ).append( latLng.longitude ).toString( );
                            String[] types = {"restaurant", "hospital", "museum"};

                            RequestQueue queue = Volley.newRequestQueue( getContext() );
                            JsonObjectRequest jsonRequest = new JsonObjectRequest( mc.getURL( location, radius, types[0] ), null,
                                    new Response.Listener<JSONObject>( ){
                                        @Override
                                        public void onResponse( JSONObject response ){
                                            stractJSON( response, BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_ORANGE ) );
                                        }
                                    }, new Response.ErrorListener( ){
                                @Override
                                public void onErrorResponse( VolleyError error ){
                                    error.printStackTrace( );
                                }
                            });
                            queue.add( jsonRequest );

                            jsonRequest = new JsonObjectRequest( mc.getURL( location, radius, types[1] ), null,
                                    new Response.Listener<JSONObject>( ){
                                        @Override
                                        public void onResponse( JSONObject response ){
                                            stractJSON( response, BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE ) );
                                        }
                                    }, new Response.ErrorListener( ){
                                @Override
                                public void onErrorResponse( VolleyError error ){
                                    error.printStackTrace( );
                                }
                            });
                            queue.add( jsonRequest );

                            jsonRequest = new JsonObjectRequest( mc.getURL( location, radius, types[2] ), null,
                                    new Response.Listener<JSONObject>( ){
                                        @Override
                                        public void onResponse( JSONObject response ){
                                            stractJSON( response, BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_GREEN ) );
                                        }
                                    }, new Response.ErrorListener( ){
                                @Override
                                public void onErrorResponse( VolleyError error ){
                                    error.printStackTrace( );
                                }
                            });
                            queue.add( jsonRequest );


                            mMap.addMarker( new MarkerOptions( ).position( latLng ).title( "Your Location" ) );
                        }
                    }
                });


    }

    public void stractJSON( JSONObject response, BitmapDescriptor bitmapDescriptor ){
        try{
            JSONArray jsonArray = response.getJSONArray( "results" );
            for( int i = 0; i < jsonArray.length( ); i++ ){
                JSONObject json = jsonArray.getJSONObject( i ).getJSONObject( "geometry" ).getJSONObject( "location" );
                LatLng latLng = new LatLng( json.getDouble( "lat" ), json.getDouble( "lng" ) );
                mMap.addMarker( new MarkerOptions( ).position( latLng ).title( jsonArray.getJSONObject( i )
                        .getString( "name" ) ).icon( bitmapDescriptor ) );
            }
        }catch( Exception e ){
            e.printStackTrace( );
        }
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng, 13f ) );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ){
        super.onCreateOptionsMenu( menu );

        MenuInflater inflater = getMenuInflater( );
        inflater.inflate( R.menu.options_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.radius:
                return  true;
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case R.id.zoom_world:
                mMap.moveCamera(CameraUpdateFactory.zoomTo(1));
                return true;
            case R.id.zoom_continent:
                mMap.moveCamera(CameraUpdateFactory.zoomTo(5));
                return true;
            case R.id.zoom_city:
                mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
                return true;
            case R.id.zoom_street:
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                return true;
            case R.id.zoom_building:
                mMap.moveCamera(CameraUpdateFactory.zoomTo(20));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public Context getContext( ){
        return this;
    }



}