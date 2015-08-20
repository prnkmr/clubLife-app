package praveenkumar.clublife;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Praveen kumar on 16/08/2015.
 */
public class Locator extends AsyncTask implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation,mCurrentLocation;
    private double lat,lon;
    private LocationRequest mLocationRequest;
    Context ctx;
    LocatorListener listener;
    boolean isListener=true;

    Locator(Context ctx){
        this(ctx, null);
        isListener=false;
    }
    Locator(Context ctx,LocatorListener listener){
        this.ctx=ctx;
        this.listener=listener;
        execute();
    }




    @Override
    protected Object doInBackground(Object[] params) {
        mGoogleApiClient = new GoogleApiClient.Builder(ctx).addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        int ctr=10;
        while (mLastLocation==null&&ctr-->0)
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if(mLastLocation!=null) {
            SharedPreferences pref = ctx.getSharedPreferences(AppData.SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("lat", String.valueOf(mLastLocation.getLatitude()));
            editor.putString("lon", String.valueOf(mLastLocation.getLongitude()));
            editor.commit();
        }
        if(isListener){
            listener.onLocated(mLastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
