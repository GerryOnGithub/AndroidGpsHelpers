package ca.gerry;

import java.util.Observable;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

// Based on https://developer.android.com/training/location/receive-location-updates.html
public class GpsWrapper extends Observable implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
  private static final String TAG = "ca.gerry.gps";

  protected GoogleApiClient mGoogleApiClient;
  protected LocationRequest mLocationRequest;
  protected Location lastLocation;
  protected int updateInterval;
  protected int fastestUpdateInterval;

  public GpsWrapper(Context context, int updateIntervalMs, int fastestUpdateIntervalMs) {
    super();
    updateInterval = updateIntervalMs;
    fastestUpdateInterval = fastestUpdateIntervalMs;
    buildGoogleApiClient(context);
  }

  private synchronized void buildGoogleApiClient(Context context) {
    mGoogleApiClient = new GoogleApiClient.Builder(context)
      .addConnectionCallbacks(this)
      .addOnConnectionFailedListener(this)
      .addApi(LocationServices.API)
      .build();

    createLocationRequest();
    mGoogleApiClient.connect();
  }

  /**
   * Sets up the location request. Android has two location request settings:
   * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
   * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
   * the AndroidManifest.xml.
   * <p/>
   * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
   * interval (5 seconds), the Fused Location Provider API returns location updates that are
   * accurate to within a few feet.
   * <p/>
   * These settings are appropriate for mapping applications that show real-time location
   * updates.
   */
  protected void createLocationRequest() {
    mLocationRequest = new LocationRequest();

    // Sets the desired interval for active location updates. This interval is
    // inexact. You may not receive updates at all if no location sources are available, or
    // you may receive them slower than requested. You may also receive updates faster than
    // requested if other applications are requesting location at a faster interval.
    mLocationRequest.setInterval(updateInterval);

    // Sets the fastest rate for active location updates. This interval is exact, and your
    // application will never receive updates faster than this value.
    mLocationRequest.setFastestInterval(fastestUpdateInterval);

    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  public void startLocationUpdates() {
    // The final argument to {@code requestLocationUpdates()} is a LocationListener
    // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
  }

  public void stopLocationUpdates() {
    // It is a good practice to remove location requests when the activity is in a paused or
    // stopped state. Doing so helps battery performance and is especially
    // recommended in applications that request frequent location updates.

    // The final argument to {@code requestLocationUpdates()} is a LocationListener
    // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
  }

  /**
   @return last (acceptable) location, which could be null
   */
  public Location location() {
    return lastLocation;
  }

  @Override // ConnectionCallbacks
  public void onConnected(Bundle bundle) {
    Log.d(TAG, "onConnected()");
    startLocationUpdates();
  }

  @Override // ConnectionCallbacks
  public void onConnectionSuspended(int i) {
    Log.e(TAG, "onConnectionSuspended unexpected");
  }

  @Override // OnConnectionFailedListener
  public void onConnectionFailed(ConnectionResult connectionResult) {
    Log.e(TAG, "onConnectionFailed(): " + connectionResult);
  }

  @Override // LocationListener
  public void onLocationChanged(Location location) {
    Log.d(TAG, "onLocationChanged(): " + location);
    if (DistanceCalculator.betterLocation(lastLocation, location)) {
      setChanged();
      lastLocation = location;
      notifyObservers(lastLocation);
    }
  }
}
