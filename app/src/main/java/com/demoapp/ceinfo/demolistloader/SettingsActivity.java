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
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.demoapp.ceinfo.demolistloader.fragments.UserLocationFragment;
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
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ceinfo on 25-01-2017.
 */

public class SettingsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();
    private static final int INIT_LOADER_ID = 0;
    private static final float MIN_DISTANCE_BW_UPDATES = 0; //10 meters
    private static final String STATE_LOCATION_UPDATES = "requesting-location";
    private static final int REQUEST_CHECK_SETTINGS = 01011;
    private static final int MY_PERMISSIONS_FINE_COARSE = 01012;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 010110;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    private static long MIN_TIME_BW_UPDATES = 1000; // 1sec
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
//            locationManager.removeUpdates(networkLocationListener);
            Log.e(LOG_TAG, " @LOC_STATUS : New GPS location: "
                    + String.format("%9.6f", location.getLatitude()) + ", "
                    + String.format("%9.6f", location.getLongitude()) + "\n");
            UpdateUI(location);
        }
    };
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
//                    locationManager.removeUpdates(gpsLocationListener);

                    Log.e(LOG_TAG, " @LOC_STATUS : New network location: "
                            + String.format("%9.6f", location.getLatitude()) + ", "
                            + String.format("%9.6f", location.getLongitude()) + "\n");
                    UpdateUI(location);
                }
            };
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean mRequestLocationUpdates = false;
    private boolean mRequestLocationUpdatesFused = false;
    private boolean mRequestLocationUpdatesNotFused = false;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    private LocationManager locationManager;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    private boolean hasLocationSettings = false;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.start_loc:
                    if (!mRequestLocationUpdates)
                        startLocationUpdates();
                    break;
                case R.id.stop_loc:
                    if (mRequestLocationUpdates)
                        stopLocationUpdates();
                    break;
                default:
                    break;
            }
        }
    };
    private String[] times = new String[]{"1", "5", "10", "15", "20", "30", "40", "50", "100"};
    private ListView listView;
    private LocationAdapter locationAdapter;
    LoaderManager.LoaderCallbacks loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            LocationSelection selection = new LocationSelection();
            return selection.getCursorLoader(SettingsActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            LocationCursor c = new LocationCursor(cursor);
            locationAdapter.swapCursor(c);
            SettingsActivity.this.scrollMyListViewToBottom();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            locationAdapter.swapCursor(null);
        }
    };

    private static void updateLocationMin(int minintrvl) {
        MIN_TIME_BW_UPDATES = minintrvl;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
        mRequestLocationUpdates = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_LOCATION_UPDATES, false);

        buildApiClient();

        buildLocationRequest(10000, 5000); //interval, fst interval

        findViewById();

        onButtonClick();

        populateLoclist();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
        outState.putBoolean(STATE_LOCATION_UPDATES, mRequestLocationUpdates);
        super.onSaveInstanceState(outState);
    }

    private void findViewById() {
        findViewById(R.id.start_loc).setOnClickListener(onClickListener);
        findViewById(R.id.stop_loc).setOnClickListener(onClickListener);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_loc);
        final List<String> list = Arrays.asList(times);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                buildLocationRequest(Integer.parseInt(list.get(position)), Integer.parseInt(list.get(position)) / 2);

                if (mRequestLocationUpdates) {
                    stopLocationUpdates();
                }

//                updateLocationMin(Integer.parseInt(list.get(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listView = (ListView) findViewById(R.id.loc_listview);
        locationAdapter = new LocationAdapter(SettingsActivity.this, null);
        listView.setAdapter(locationAdapter);


        locationAdapter.setOnCursorItemClickListener(new LocationAdapter.OnCursorItemClickListener() {
            @Override
            public void OnCursorItemClick(Double latitude, Double longitude) {

                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mmi);

                if (null == fragment) {
                    getSupportFragmentManager()
                            .beginTransaction().add(R.id.mmi, UserLocationFragment.newInstance(latitude, longitude))
                            .addToBackStack(null)
                            .commit();
                } else {

                    getSupportFragmentManager()
                            .beginTransaction().replace(R.id.mmi, UserLocationFragment.newInstance(latitude, longitude))
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
    }

    private void populateLoclist() {

        if (null == getSupportLoaderManager().getLoader(INIT_LOADER_ID)) {
            getSupportLoaderManager().initLoader(INIT_LOADER_ID, null, loaderCallbacks);
        } else {
            getSupportLoaderManager().restartLoader(INIT_LOADER_ID, null, loaderCallbacks);
        }
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

    private void onButtonClick() {
        if (mRequestLocationUpdates) {
            (findViewById(R.id.start_loc)).setEnabled(false);
            (findViewById(R.id.stop_loc)).setEnabled(true);
        } else {
            (findViewById(R.id.start_loc)).setEnabled(true);
            (findViewById(R.id.stop_loc)).setEnabled(false);
        }
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

    private void showSettingsAlert() {

        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setTitle("GPS Settings")
                .setPositiveButton("Goto Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        alertDialog.show();
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
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.

                        hasLocationSettings = true;

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    SettingsActivity.this,
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

    private void buildLocationRequest(final int intrvl, final int fstintrvl) {
        if (null == mLocationRequest)
            mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(intrvl);
        mLocationRequest.setFastestInterval(fstintrvl);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void startLocationUpdatesFused() {

        requestSettings();

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

        mRequestLocationUpdatesFused = true; //requesting fused
    }

    private void startLocationUpdatesNotFused() {
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {

            showSettingsAlert();

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

            mRequestLocationUpdatesNotFused = !mRequestLocationUpdatesNotFused; //requesting not fused
        }
    }

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
        if (mRequestLocationUpdates) startLocationUpdates();
    }

    private void startLocationUpdates() {

        if (mGoogleApiClient.isConnected() && !mRequestLocationUpdatesFused)
            startLocationUpdatesFused();
//        else if (!mRequestLocationUpdatesNotFused)
//            startLocationUpdatesNotFused();

        mRequestLocationUpdates = !mRequestLocationUpdates && mRequestLocationUpdatesFused;
        onButtonClick();
        populateLoclist();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        // Destroys variables and references, and catches Exceptions
        try {
            getSupportLoaderManager().destroyLoader(INIT_LOADER_ID);
            if (locationAdapter != null) {
                locationAdapter.changeCursor(null);
                locationAdapter = null;
            }
        } catch (Throwable localThrowable) {
        }

        super.onDestroy();
    }

    private void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected() && mRequestLocationUpdatesFused)//stop location updates
            stopLocationUpdatesFused();
//        else
//        if (mRequestLocationUpdatesNotFused)
//            stopLocationUpdatesNotFused();
        mRequestLocationUpdates = false;
        onButtonClick();
    }

    private void stopLocationUpdatesFused() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        mRequestLocationUpdatesFused = false;
    }

    private void stopLocationUpdatesNotFused() {
//        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
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
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_FINE_COARSE);
            }
        }

        if (isGPSEnabled)

            locationManager.removeUpdates(gpsLocationListener);

        if (isNetworkEnabled)

            locationManager.removeUpdates(networkLocationListener);

        mRequestLocationUpdatesNotFused = false;
    }

    @Override
    public void onConnectionSuspended(int i) {

        if (mRequestLocationUpdates)
            stopLocationUpdates();

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
                        hasLocationSettings = true;
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
            ((SettingsActivity) getActivity()).onDialogDismissed();
        }
    }


}
