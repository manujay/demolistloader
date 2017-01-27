package com.demoapp.ceinfo.demolistloader.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demoapp.ceinfo.demolistloader.R;
import com.mmi.MapView;
import com.mmi.MapmyIndiaMapView;
import com.mmi.layers.Marker;
import com.mmi.layers.UserLocationOverlay;
import com.mmi.layers.location.GpsLocationProvider;
import com.mmi.util.GeoPoint;
import com.mmi.util.constants.MapViewConstants;

/**
 * Created by ceinfo on 27-01-2017.
 */

public class UserLocationFragment extends Fragment implements MapViewConstants {

    public static final String LOCATION_USER_LAT = "location-user-lat";
    public static final String LOCATION_USER_LNG = "location-user-lng";

    UserLocationOverlay mLocationOverlay;
    MapView mMapView;
    Handler locationFoundHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mMapView.setCenter(mLocationOverlay.getMyLocation());
                    mMapView.setZoom(mMapView.getMaxZoomLevel());
                    break;

            }
        }
    };

    public UserLocationFragment() {
    }

    public static UserLocationFragment newInstance(Double latitude, Double longitude) {

        Bundle args = new Bundle();
        args.putDouble(LOCATION_USER_LAT, latitude);
        args.putDouble(LOCATION_USER_LNG, longitude);
        UserLocationFragment fragment = new UserLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_mapview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (null == getArguments())
            return;

        Double lat = getArguments().getDouble(LOCATION_USER_LAT, 0.0);
        Double lng = getArguments().getDouble(LOCATION_USER_LNG, 0.0);


        mMapView = ((MapmyIndiaMapView) view.findViewById(R.id.map)).getMapView();
        GeoPoint geoPoint = new GeoPoint(lat, lng);
        mMapView.setCenter(geoPoint);

        Marker marker = new Marker(mMapView);
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(marker);
        mLocationOverlay = new UserLocationOverlay(new GpsLocationProvider(getActivity()), mMapView);
//        mLocationOverlay.setCurrentLocationResId(R.drawable.marker_default_1);
        mLocationOverlay.enableMyLocation();
        mMapView.getOverlays().add(mLocationOverlay);
        mLocationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                locationFoundHandler.sendEmptyMessage(1);
            }
        });


//        mMapView.invalidate();
//        mMapView.invalidate();
        mMapView.invalidate();
    }
}
