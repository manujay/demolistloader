package com.demoapp.ceinfo.demolistloader.provider.location;

// @formatter:off

import android.support.annotation.NonNull;

import com.demoapp.ceinfo.demolistloader.provider.base.BaseModel;

/**
 * Realtime Location Updates from device
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public interface LocationModel extends BaseModel {

    /**
     * Primary key.
     */
    long getId();

    /**
     * Latitude
     */
    double getRLat();

    /**
     * Longitude
     */
    double getRLong();

    /**
     * Get the {@code r_speed} value.
     */
    float getRSpeed();

    /**
     * Get the {@code r_time} value.
     */
    long getRTime();

    /**
     * Provider for the Location
     * Cannot be {@code null}.
     */
    @NonNull
    String getRProvider();
}
