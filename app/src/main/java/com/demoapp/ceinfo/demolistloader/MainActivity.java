package com.demoapp.ceinfo.demolistloader;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.demoapp.ceinfo.demolistloader.fragments.UserLocationFragment;
import com.demoapp.ceinfo.demolistloader.provider.location.LocationCursor;
import com.demoapp.ceinfo.demolistloader.provider.location.LocationSelection;
import com.demoapp.ceinfo.demolistloader.service.LocationService;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int INIT_LOADER_ID = 0;

    private boolean mRequestLocationUpdates = false;
    private ListView listView;
    private LocationAdapter locationAdapter;
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
    private LoaderManager.LoaderCallbacks loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            LocationSelection selection = new LocationSelection();
            return selection.getCursorLoader(MainActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            LocationCursor c = new LocationCursor(cursor);
            locationAdapter.swapCursor(c);
            MainActivity.this.scrollMyListViewToBottom();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            locationAdapter.swapCursor(null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);

        findViewById();

        onButtonClick();

        populateLoclist();

        startLocationUpdates();

        LocationService.startService(MainActivity.this);
    }

    private void populateLoclist() {

        if (null == getSupportLoaderManager().getLoader(INIT_LOADER_ID)) {
            getSupportLoaderManager().initLoader(INIT_LOADER_ID, null, loaderCallbacks);
        } else {
            getSupportLoaderManager().restartLoader(INIT_LOADER_ID, null, loaderCallbacks);
        }
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
                if (mRequestLocationUpdates) {
                    stopLocationUpdates();
                }

                SharedPrefHelper.setMinInterval(MainActivity.this, 250 * (Integer.parseInt(list.get(position))));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listView = (ListView) findViewById(R.id.loc_listview);
        locationAdapter = new LocationAdapter(MainActivity.this, null);
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

    private void onButtonClick() {
        if (mRequestLocationUpdates) {
            (findViewById(R.id.start_loc)).setEnabled(false);
            (findViewById(R.id.stop_loc)).setEnabled(true);
        } else {
            (findViewById(R.id.start_loc)).setEnabled(true);
            (findViewById(R.id.stop_loc)).setEnabled(false);
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

    private void startLocationUpdates() {

        mRequestLocationUpdates = true;

        onButtonClick();

        SharedPrefHelper.setBooleanUpdates(MainActivity.this, mRequestLocationUpdates);
    }

    private void stopLocationUpdates() {

        mRequestLocationUpdates = false;

        onButtonClick();

        SharedPrefHelper.setBooleanUpdates(MainActivity.this, mRequestLocationUpdates);
    }
}
