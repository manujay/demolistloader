package com.demoapp.ceinfo.demolistloader.provider.location;

// @formatter:off

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.demoapp.ceinfo.demolistloader.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code location} table.
 */
@SuppressWarnings({"ConstantConditions", "unused"})
public class LocationContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return LocationColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where           The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable LocationSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param context The context to use.
     * @param where   The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable LocationSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Latitude
     */
    public LocationContentValues putRLat(double value) {
        mContentValues.put(LocationColumns.R_LAT, value);
        return this;
    }


    /**
     * Longitude
     */
    public LocationContentValues putRLong(double value) {
        mContentValues.put(LocationColumns.R_LONG, value);
        return this;
    }


    public LocationContentValues putRSpeed(float value) {
        mContentValues.put(LocationColumns.R_SPEED, value);
        return this;
    }


    public LocationContentValues putRTime(long value) {
        mContentValues.put(LocationColumns.R_TIME, value);
        return this;
    }


    /**
     * Provider for the Location
     */
    public LocationContentValues putRProvider(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("rProvider must not be null");
        mContentValues.put(LocationColumns.R_PROVIDER, value);
        return this;
    }

}
