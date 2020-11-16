package unal.edu.co.geolocalizacion;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

public class GPS {

    LocationManager locationManager;
    Context context;
    Location mLocation;

    public GPS( Context context ){
        this.context = context;
        locationManager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
    }

    public LatLng getLocation( ){
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener( ){
            @Override
            public void onLocationChanged( Location location ){
                mLocation = location;
            }

            @Override
            public void onStatusChanged( String provider, int status, Bundle extras ){ }

            @Override
            public void onProviderEnabled( String provider ){ }

            @Override
            public void onProviderDisabled( String provider ){ }
        };

        if( ActivityCompat.checkSelfPermission( context, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED  ){
            return new LatLng( 4.6097102, -74.081749 );
        }
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 1000, 0, locationListener );

        do {
            mLocation = locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
        }while( mLocation == null );

        return new LatLng( mLocation.getLatitude( ), mLocation.getLongitude( ) );
    }
}
