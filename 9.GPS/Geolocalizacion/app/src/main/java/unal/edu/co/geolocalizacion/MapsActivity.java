package unal.edu.co.geolocalizacion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;
    private LatLng latLng;
    private String radius;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager( ).findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );
        radius = "1500";
    }

    @Override
    public void onMapReady( GoogleMap googleMap ){

        latLng = new GPS( this ).getLocation( );

        MapsController mc = new MapsController( );

        String location = new StringBuilder( ).append( latLng.latitude ).append( "," ).append( latLng.longitude ).toString( );
        String[] types = {"restaurant", "hospital", "museum"};

        RequestQueue queue = Volley.newRequestQueue( this );
        JsonObjectRequest jsonRequest = new JsonObjectRequest( mc.getURL( location, radius, types[0] ), null,
            new Response.Listener<JSONObject>( ){
                @Override
                public void onResponse( JSONObject response ){
                    stractJSON( response, BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_YELLOW ) );
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
                        stractJSON( response, BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_MAGENTA ) );
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

        mMap = googleMap;
        mMap.addMarker( new MarkerOptions( ).position( latLng ).title( "Your Location" ) );
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
    public boolean onOptionsItemSelected( MenuItem item ){
        AlertDialog alertDialog = new AlertDialog.Builder( this ).create( );
        alertDialog.setTitle( "Radius Diameter" );
        alertDialog.setMessage( "Enter the search distance (Kilometers)!" );
        final EditText editText = new EditText( this );
        editText.setInputType( InputType.TYPE_CLASS_NUMBER );
        alertDialog.setView( editText );
        alertDialog.setButton( AlertDialog.BUTTON_POSITIVE, "Send",
            new DialogInterface.OnClickListener( ){
                public void onClick( DialogInterface dialog, int which ){
                    radius = editText.getText( ).toString( ) + "000";
                    dialog.dismiss( );
                    mMap.clear( );
                    onMapReady( mMap );
                }
            });
        alertDialog.show( );
        return true;
    }

    public Context getContext( ){
        return this;
    }


}