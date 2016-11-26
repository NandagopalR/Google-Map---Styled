package com.nanda.googlemapanimation.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.nanda.googlemapanimation.R;
import com.nanda.googlemapanimation.base.BaseActivity;
import com.nanda.googlemapanimation.helpers.GoogleMapRipple;
import com.nanda.googlemapanimation.helpers.LocationHelper;

/**
 * Created by nandagopal on 11/26/16.
 */
public class MapActivity extends BaseActivity
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener, LocationHelper.OnLocationCompleteListener, OnMapReadyCallback {

  protected Location mLastLocation;
  protected GoogleMap map;
  private GoogleApiClient mGoogleApiClient;
  private static final long INTERVAL = 1000 * 20;
  private static final long FASTEST_INTERVAL = 1000 * 10;
  private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
  private LocationHelper locationHelper;
  private GoogleMapRipple googleMapRipple;
  private int mSelectedStyleId = R.string.style_label_retro;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();

    int hasGetLocationPermission =
        ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
    if (hasGetLocationPermission != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
          new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
          REQUEST_CODE_ASK_PERMISSIONS);
    } else {
      locationHelper = new LocationHelper(this, this);
    }

    SupportMapFragment mapFragment =
        (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
  }

  @Override public void onMapReady(GoogleMap googleMap) {
    map = googleMap;
    if (!map.isMyLocationEnabled()) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
          return;
        } else {
          if (map != null) {
            map.setMyLocationEnabled(true);
          }
        }
      } else {
        map.setMyLocationEnabled(true);
      }
    }
    setSelectedStyle();
  }

  @Override public void getLocationUpdate(Location location) {

  }

  @Override public void onError(ConnectionResult connectionResult, Status status, String error) {
    if (connectionResult != null) {
      if (connectionResult.hasResolution()) {
        try {
          connectionResult.startResolutionForResult(this,
              LocationHelper.CONNECTION_FAILURE_RESOLUTION_REQUEST);
        } catch (IntentSender.SendIntentException e) {
          e.printStackTrace();
        }
      }
    } else if (status != null) {
      // Location is not available, but we can ask permission from users
      try {
        status.startResolutionForResult(this, LocationHelper.REQUEST_CHECK_SETTINGS);
      } catch (IntentSender.SendIntentException e) {
        e.printStackTrace();
      }
    }

    if (error != null) {
      if (error.equals("User choose not to make required location settings changes.")) {
        this.finish();
      }
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    if (requestCode != REQUEST_CODE_ASK_PERMISSIONS) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      return;
    }
    if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      // we have permission,
      locationHelper = new LocationHelper(this, this);
      return;
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    locationHelper.onActivityResult(requestCode, resultCode, data);
  }

  @Override public void onStart() {
    super.onStart();
    if (mGoogleApiClient != null) mGoogleApiClient.connect();
  }

  @Override public void onStop() {
    super.onStop();
    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
  }

  @Override public void onConnectionFailed(ConnectionResult result) {

  }

  @Override public void onConnected(Bundle connectionHint) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
          != PackageManager.PERMISSION_GRANTED
          && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
          != PackageManager.PERMISSION_GRANTED) {
        return;
      }
    }
    createLocationRequest();
    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    LatLng latLng = null;
    if (mLastLocation != null) {
      latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
      googleMapRipple = new GoogleMapRipple(map, latLng, MapActivity.this);
      googleMapRipple.withNumberOfRipples(3);
      googleMapRipple.withFillColor(Color.parseColor("#FF82CAFF"));
      googleMapRipple.withStrokeColor(Color.parseColor("#FFAB47BC"));
      googleMapRipple.withStrokewidth(10);      // 10dp
      googleMapRipple.withDistance(500);      // 2000 metres radius
      googleMapRipple.withRippleDuration(8000);    //12000ms
      googleMapRipple.withTransparency(0.5f);
      googleMapRipple.startRippleMapAnimation();
    }
  }

  private void setSelectedStyle() {
    MapStyleOptions style;
    // Sets the retro style via raw resource JSON.
    style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_retro);
    map.setMapStyle(style);
  }

  private void createLocationRequest() {
    LocationRequest mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(INTERVAL);
    mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
        this);
  }

  @Override public void onConnectionSuspended(int cause) {

  }

  @Override public void onLocationChanged(Location location) {
    if (location != null) {
      //Toast.makeText(this, " - " + location.getLatitude(), Toast.LENGTH_SHORT).show();
    }
  }
}