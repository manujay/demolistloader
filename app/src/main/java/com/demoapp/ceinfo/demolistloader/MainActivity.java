package com.demoapp.ceinfo.demolistloader;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.demoapp.ceinfo.demolistloader.provider.location.LocationContentValues;
import com.demoapp.ceinfo.demolistloader.provider.location.LocationCursor;
import com.demoapp.ceinfo.demolistloader.provider.location.LocationSelection;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final long MIN_TIME_BW_UPDATES = 1000; // 1sec
    private static final float MIN_DISTANCE_BW_UPDATES = 0; //10 meters
    private static final int REQUEST_CHECK_SETTINGS = 01011;
    private static final int MY_PERMISSIONS_FINE_COARSE = 01012;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 010110;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean mRequestLocationUpdatesFused = false;
    private boolean mRequestLocationUpdatesNotFused = false;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    private ListView listView;
    private LocationAdapter locationAdapter;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;

    private LocationManager locationManager;
    private final android.location.LocationListener networkLocationListener =
            new android.location.LocationListener() {

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    switch (status) {
                        case LocationProvider.AVAILABLE:
                            Log.e(LOG_TAG, " @LOC_STATUS : Network location available again\n");
                            break;
                        case LocationProvider.OUT_OF_SERVICE:
                            Log.e(LOG_TAG, " @LOC_STATUS : Network location out of service\n");
                            break;
                        case LocationProvider.TEMPORARILY_UNAVAILABLE:
                            Log.e(LOG_TAG, " @LOC_STATUS : Network location temporarily unavailable\n");
                            break;
                    }
                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.e(LOG_TAG, " @LOC_STATUS : Network Provider Enabled\n");
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.e(LOG_TAG, " @LOC_STATUS : Network Provider Disabled\n");
                }

                @Override
                public void onLocationChanged(Location location) {
                    locationManager.removeUpdates(gpsLocationListener);

                    Log.e(LOG_TAG, " @LOC_STATUS : New network location: "
                            + String.format("%9.6f", location.getLatitude()) + ", "
                            + String.format("%9.6f", location.getLongitude()) + "\n");
                    UpdateUI(location);
                }
            };
    private final android.location.LocationListener gpsLocationListener = new android.location.LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.e(LOG_TAG, " @LOC_STATUS : GPS available again\n");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.e(LOG_TAG, " @LOC_STATUS : GPS out of service\n");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.e(LOG_TAG, " @LOC_STATUS : GPS temporarily unavailable\n");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(LOG_TAG, " @LOC_STATUS : GPS Provider Enabled\n");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(LOG_TAG, " @LOC_STATUS : GPS Provider Disabled\n");
        }

        @Override
        public void onLocationChanged(Location location) {
            locationManager.removeUpdates(networkLocationListener);
            Log.e(LOG_TAG, " @LOC_STATUS : New GPS location: "
                    + String.format("%9.6f", location.getLatitude()) + ", "
                    + String.format("%9.6f", location.getLongitude()) + "\n");
            UpdateUI(location);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mResolvingError = savedInstanceState != null
//                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
//        buildApiClient();
//        buildLocationRequest();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById();

        populateLoclist();
    }

    private void populateLoclist() {

        getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                LocationSelection selection = new LocationSelection();
                return selection.getCursorLoader(MainActivity.this);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                LocationCursor c = new LocationCursor(cursor);
                locationAdapter.swapCursor(c); // let the framework handle closing of the cursor
                MainActivity.this.scrollMyListViewToBottom();
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                locationAdapter.swapCursor(null);
            }
        });
    }

    private void findViewById() {
        listView = (ListView) findViewById(R.id.loc_listview);
    }

    private void scrollMyListViewToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(locationAdapter.getCount() - 1);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    private void buildApiClient() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    private void requestPermission() {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                showToast(" : Request location Permission Coarse : ");

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_FINE_COARSE);
            }
        }
    }

    private void requestSettings() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates states = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.

                        mRequestLocationUpdatesFused = true;
                        startLocationUpdates();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });


    }

    private void buildLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {

//        startLocationUpdatesNotFused();

        startLocationUpdatesFused();
    }

    private void startLocationUpdatesFused() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                showToast(" : Request location Permission Coarse : ");

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_FINE_COARSE);
            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    private void startLocationUpdatesNotFused() {
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {

            //nothing is available

        } else {

            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    showToast(" : Request location Permission Coarse : ");

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_FINE_COARSE);
                }
            }

            if (isGPSEnabled)

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES, MIN_DISTANCE_BW_UPDATES, gpsLocationListener);

            if (isNetworkEnabled)

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES, MIN_DISTANCE_BW_UPDATES, networkLocationListener);

        }
    }

    private void stopLocationUpdates() {

//        stopLocationUpdatesNotFused();

        stopLocationUpdatesFused();
    }

    private void stopLocationUpdatesFused() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    private void stopLocationUpdatesNotFused() {
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                showToast(" : Request location Permission Coarse : ");

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_FINE_COARSE);
            }
        }

        if (isGPSEnabled)

            locationManager.removeUpdates(gpsLocationListener);

        if (isNetworkEnabled)

            locationManager.removeUpdates(networkLocationListener);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_FINE_COARSE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (!mRequestLocationUpdatesFused) {
                        startLocationUpdates();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestSettings();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    protected void onStart() {
//        mGoogleApiClient.connect();
//        if (mGoogleApiClient.isConnected() && !mRequestLocationUpdatesFused) {
//            startLocationUpdates();
//        }
//        super.onStart();
//    }

//    protected void onStop() {
//stop location updates
//        if (mGoogleApiClient.isConnected() && mRequestLocationUpdatesFused)
//            stopLocationUpdates();
//disconnect after
//        mGoogleApiClient.disconnect();
//        super.onStop();
//    }

    @Override
    public void onLocationChanged(Location location) {

        UpdateUI(location);
    }

    private void UpdateUI(Location location) {

        Log.i(LOG_TAG, " @UpdateUI : newlocation : Lat : " + location.getLatitude() + " : Lng :" + location.getLongitude() + " : Speed : " + location.getSpeed() + " : Time :" + location.getTime() + " : Provider :" + location.getProvider());

        insertLocation(location.getLatitude(), location.getLongitude(), location.getSpeed(), location.getTime(), location.getProvider());

    }


    private long insertLocation(Double lat, Double lng, Float speed, long time, String provider) {
        LocationContentValues contentValues = new LocationContentValues();
        contentValues.putRLat(lat);
        contentValues.putRLong(lng);
        contentValues.putRSpeed(speed);
        contentValues.putRTime(time);
        contentValues.putRProvider(provider);

        Uri uri = contentValues.insert(this);

        return ContentUris.parseId(uri);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestPermission();

        if (mRequestLocationUpdatesFused) {
            startLocationUpdates();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }

    }

    // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        requestSettings();//keep asking if imp or do whatever
                        break;
                }
                break;

            case REQUEST_RESOLVE_ERROR:
                mResolvingError = false;
                if (resultCode == RESULT_OK) {
                    // Make sure the app is not already connected or attempting to connect
                    if (!mGoogleApiClient.isConnecting() &&
                            !mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.connect();
                    }
                }
                break;
        }
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MainActivity) getActivity()).onDialogDismissed();
        }
    }
}
