package com.demoapp.ceinfo.demolistloader.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.demoapp.ceinfo.demolistloader.SharedPrefHelper;
import com.demoapp.ceinfo.demolistloader.provider.location.LocationContentValues;

/**
 * Created by ceinfo on 21-02-2017.
 */

public class LocationUpdateJobService extends JobService {

    private static final String LOG_TAG = LocationUpdateJobService.class.getSimpleName();
    private static final float MIN_DISTANCE_IN_MTRS = 0;
    private static long MIN_INTERVAL_IN_MILS = 1000;
    private static int kid = 0;
    private ServiceThread thread = null;

    public LocationUpdateJobService() {
    }

    public static void startJobService(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName serviceComponent = new ComponentName(context, LocationUpdateJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(kid++, serviceComponent);
        builder.setMinimumLatency(5 * 1000); // wait at least
        builder.setOverrideDeadline(50 * 1000); // maximum delay
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        builder.setRequiresDeviceIdle(true); // device should be idle
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        jobScheduler.schedule(builder.build());
    }

    public static void cancelAllJobs(Context context) {
        JobScheduler tm = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancelAll();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        if (thread == null) {
            return true;
        }
        thread = new ServiceThread();
        thread.setContext(getApplicationContext());
        thread.start();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        if (thread == null)
            return true;

        thread.signalStop();
        return true;
    }


    private class ServiceThread extends Thread {

        private Context context = null;
        private Handler handler = null;
        private LocationManager locationManager = null;
        private boolean isGPSEnabled;
        private boolean isNetworkEnabled;
        private SharedPreferences pref;
        private LocationListener networklistener = new LocationListener() {
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

                Log.e(LOG_TAG, " @LOC_STATUS : New network location: "
                        + String.format("%9.6f", location.getLatitude()) + ", "
                        + String.format("%9.6f", location.getLongitude()) + "\n");
                UpdateUI(location);
            }
        };
        private LocationListener gpslocationlistener = new LocationListener() {
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

                Log.e(LOG_TAG, " @LOC_STATUS : New GPS location: "
                        + String.format("%9.6f", location.getLatitude()) + ", "
                        + String.format("%9.6f", location.getLongitude()) + "\n");
                UpdateUI(location);
            }
        };
        private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {

                if (key.equals(SharedPrefHelper.BOOLEAN_REQUESTING_UPDATES)) {
                    if (pref.getBoolean(SharedPrefHelper.BOOLEAN_REQUESTING_UPDATES, false))
                        startLocationUpdates();
                    else
                        stopLocationUpdates();
                }

                if (key.equals(SharedPrefHelper.LONG_MIN_INTERVAL_UPDATES)) {
                    updateInterval(pref.getLong(SharedPrefHelper.LONG_MIN_INTERVAL_UPDATES, 0));
                }
            }
        };

        private void updateInterval(long intrvl) {
            MIN_INTERVAL_IN_MILS = intrvl;
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

            Uri uri = contentValues.insert(this.context);

            return ContentUris.parseId(uri);
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void signalStop() {
            if (handler != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Looper.myLooper().quit();
                    }
                });
            }
        }

        private void requestLocationUpdates() {

            if (!isGPSEnabled && !isNetworkEnabled) {

                signalStop();

            } else {

                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_INTERVAL_IN_MILS, MIN_DISTANCE_IN_MTRS, gpslocationlistener);
                }

                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_INTERVAL_IN_MILS, MIN_DISTANCE_IN_MTRS, networklistener);
                }
            }
        }

        private void removeLocationUpdates() {

            if (isGPSEnabled) {
                locationManager.removeUpdates(gpslocationlistener);
            }

            if (isNetworkEnabled) {
                locationManager.removeUpdates(networklistener);
            }
        }

        public void startLocationUpdates() {
            if (null == handler) {
                handler = new Handler();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    requestLocationUpdates();
                }
            });
        }

        public void stopLocationUpdates() {
            if (null == handler) {
                handler = new Handler();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    removeLocationUpdates();
                }
            });
        }

        @Override
        public void run() {
            super.run();

            Log.e(LOG_TAG, " @mky:Thread Init ");

            Looper.prepare();

            Log.e(LOG_TAG, " @mky:Starting Location Updates");
            handler = new Handler();
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            pref = SharedPrefHelper.getDefaultSharedPref(context);
            pref.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

            startLocationUpdates();

            Looper.loop();

            Log.e(LOG_TAG, " @mky:Stoping Location Updates");

            stopLocationUpdates();

            pref.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        }
    }
}
