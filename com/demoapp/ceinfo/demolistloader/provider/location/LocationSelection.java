package com.demoapp.ceinfo.demolistloader.provider.location;

// @formatter:off
import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.demoapp.ceinfo.demolistloader.provider.base.AbstractSelection;

/**
 * Selection for the {@code location} table.
 */
@SuppressWarnings({"unused", "WeakerAccess", "Recycle"})
public class LocationSelection extends AbstractSelection<LocationSelection> {
    @Override
    protected Uri baseUri() {
        return LocationColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code LocationCursor} object, which is positioned before the first entry, or null.
     */
    public LocationCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new LocationCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public LocationCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code LocationCursor} object, which is positioned before the first entry, or null.
     */
    public LocationCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new LocationCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public LocationCursor query(Context context) {
        return query(context, null);
    }


    public LocationSelection id(long... value) {
        addEquals("location." + LocationColumns._ID, toObjectArray(value));
        return this;
    }

    public LocationSelection idNot(long... value) {
        addNotEquals("location." + LocationColumns._ID, toObjectArray(value));
        return this;
    }

    public LocationSelection orderById(boolean desc) {
        orderBy("location." + LocationColumns._ID, desc);
        return this;
    }

    public LocationSelection orderById() {
        return orderById(false);
    }

    public LocationSelection rLat(double... value) {
        addEquals(LocationColumns.R_LAT, toObjectArray(value));
        return this;
    }

    public LocationSelection rLatNot(double... value) {
        addNotEquals(LocationColumns.R_LAT, toObjectArray(value));
        return this;
    }

    public LocationSelection rLatGt(double value) {
        addGreaterThan(LocationColumns.R_LAT, value);
        return this;
    }

    public LocationSelection rLatGtEq(double value) {
        addGreaterThanOrEquals(LocationColumns.R_LAT, value);
        return this;
    }

    public LocationSelection rLatLt(double value) {
        addLessThan(LocationColumns.R_LAT, value);
        return this;
    }

    public LocationSelection rLatLtEq(double value) {
        addLessThanOrEquals(LocationColumns.R_LAT, value);
        return this;
    }

    public LocationSelection orderByRLat(boolean desc) {
        orderBy(LocationColumns.R_LAT, desc);
        return this;
    }

    public LocationSelection orderByRLat() {
        orderBy(LocationColumns.R_LAT, false);
        return this;
    }

    public LocationSelection rLong(double... value) {
        addEquals(LocationColumns.R_LONG, toObjectArray(value));
        return this;
    }

    public LocationSelection rLongNot(double... value) {
        addNotEquals(LocationColumns.R_LONG, toObjectArray(value));
        return this;
    }

    public LocationSelection rLongGt(double value) {
        addGreaterThan(LocationColumns.R_LONG, value);
        return this;
    }

    public LocationSelection rLongGtEq(double value) {
        addGreaterThanOrEquals(LocationColumns.R_LONG, value);
        return this;
    }

    public LocationSelection rLongLt(double value) {
        addLessThan(LocationColumns.R_LONG, value);
        return this;
    }

    public LocationSelection rLongLtEq(double value) {
        addLessThanOrEquals(LocationColumns.R_LONG, value);
        return this;
    }

    public LocationSelection orderByRLong(boolean desc) {
        orderBy(LocationColumns.R_LONG, desc);
        return this;
    }

    public LocationSelection orderByRLong() {
        orderBy(LocationColumns.R_LONG, false);
        return this;
    }

    public LocationSelection rSpeed(float... value) {
        addEquals(LocationColumns.R_SPEED, toObjectArray(value));
        return this;
    }

    public LocationSelection rSpeedNot(float... value) {
        addNotEquals(LocationColumns.R_SPEED, toObjectArray(value));
        return this;
    }

    public LocationSelection rSpeedGt(float value) {
        addGreaterThan(LocationColumns.R_SPEED, value);
        return this;
    }

    public LocationSelection rSpeedGtEq(float value) {
        addGreaterThanOrEquals(LocationColumns.R_SPEED, value);
        return this;
    }

    public LocationSelection rSpeedLt(float value) {
        addLessThan(LocationColumns.R_SPEED, value);
        return this;
    }

    public LocationSelection rSpeedLtEq(float value) {
        addLessThanOrEquals(LocationColumns.R_SPEED, value);
        return this;
    }

    public LocationSelection orderByRSpeed(boolean desc) {
        orderBy(LocationColumns.R_SPEED, desc);
        return this;
    }

    public LocationSelection orderByRSpeed() {
        orderBy(LocationColumns.R_SPEED, false);
        return this;
    }

    public LocationSelection rTime(long... value) {
        addEquals(LocationColumns.R_TIME, toObjectArray(value));
        return this;
    }

    public LocationSelection rTimeNot(long... value) {
        addNotEquals(LocationColumns.R_TIME, toObjectArray(value));
        return this;
    }

    public LocationSelection rTimeGt(long value) {
        addGreaterThan(LocationColumns.R_TIME, value);
        return this;
    }

    public LocationSelection rTimeGtEq(long value) {
        addGreaterThanOrEquals(LocationColumns.R_TIME, value);
        return this;
    }

    public LocationSelection rTimeLt(long value) {
        addLessThan(LocationColumns.R_TIME, value);
        return this;
    }

    public LocationSelection rTimeLtEq(long value) {
        addLessThanOrEquals(LocationColumns.R_TIME, value);
        return this;
    }

    public LocationSelection orderByRTime(boolean desc) {
        orderBy(LocationColumns.R_TIME, desc);
        return this;
    }

    public LocationSelection orderByRTime() {
        orderBy(LocationColumns.R_TIME, false);
        return this;
    }

    public LocationSelection rProvider(String... value) {
        addEquals(LocationColumns.R_PROVIDER, value);
        return this;
    }

    public LocationSelection rProviderNot(String... value) {
        addNotEquals(LocationColumns.R_PROVIDER, value);
        return this;
    }

    public LocationSelection rProviderLike(String... value) {
        addLike(LocationColumns.R_PROVIDER, value);
        return this;
    }

    public LocationSelection rProviderContains(String... value) {
        addContains(LocationColumns.R_PROVIDER, value);
        return this;
    }

    public LocationSelection rProviderStartsWith(String... value) {
        addStartsWith(LocationColumns.R_PROVIDER, value);
        return this;
    }

    public LocationSelection rProviderEndsWith(String... value) {
        addEndsWith(LocationColumns.R_PROVIDER, value);
        return this;
    }

    public LocationSelection orderByRProvider(boolean desc) {
        orderBy(LocationColumns.R_PROVIDER, desc);
        return this;
    }

    public LocationSelection orderByRProvider() {
        orderBy(LocationColumns.R_PROVIDER, false);
        return this;
    }
}
