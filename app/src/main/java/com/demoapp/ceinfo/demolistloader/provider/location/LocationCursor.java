package com.demoapp.ceinfo.demolistloader.provider.location;

// @formatter:off

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.demoapp.ceinfo.demolistloader.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code location} table.
 */
@SuppressWarnings({"WeakerAccess", "unused", "UnnecessaryLocalVariable"})
public class LocationCursor extends AbstractCursor implements LocationModel {
    public LocationCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    @Override
    public long getId() {
        Long res = getLongOrNull(LocationColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Latitude
     */
    @Override
    public double getRLat() {
        Double res = getDoubleOrNull(LocationColumns.R_LAT);
        if (res == null)
            throw new NullPointerException("The value of 'r_lat' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Longitude
     */
    @Override
    public double getRLong() {
        Double res = getDoubleOrNull(LocationColumns.R_LONG);
        if (res == null)
            throw new NullPointerException("The value of 'r_long' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code r_speed} value.
     */
    @Override
    public float getRSpeed() {
        Float res = getFloatOrNull(LocationColumns.R_SPEED);
        if (res == null)
            throw new NullPointerException("The value of 'r_speed' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code r_time} value.
     */
    @Override
    public long getRTime() {
        Long res = getLongOrNull(LocationColumns.R_TIME);
        if (res == null)
            throw new NullPointerException("The value of 'r_time' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Provider for the Location
     * Cannot be {@code null}.
     */
    @NonNull
    @Override
    public String getRProvider() {
        String res = getStringOrNull(LocationColumns.R_PROVIDER);
        if (res == null)
            throw new NullPointerException("The value of 'r_provider' in the database was null, which is not allowed according to the model definition");
        return res;
    }
}
