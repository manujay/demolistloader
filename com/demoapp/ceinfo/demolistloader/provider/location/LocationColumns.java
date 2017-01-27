package com.demoapp.ceinfo.demolistloader.provider.location;

// @formatter:off
import android.net.Uri;
import android.provider.BaseColumns;

import com.demoapp.ceinfo.demolistloader.provider.SampleProvider;
import com.demoapp.ceinfo.demolistloader.provider.base.AbstractSelection;
import com.demoapp.ceinfo.demolistloader.provider.location.LocationColumns;
import com.demoapp.ceinfo.demolistloader.provider.person.PersonColumns;

/**
 * Realtime Location Updates from device
 */
@SuppressWarnings("unused")
public class LocationColumns implements BaseColumns {
    public static final String TABLE_NAME = "location";
    public static final Uri CONTENT_URI = Uri.parse(SampleProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * Latitude
     */
    public static final String R_LAT = "r_lat";

    /**
     * Longitude
     */
    public static final String R_LONG = "r_long";

    public static final String R_SPEED = "r_speed";

    public static final String R_TIME = "r_time";

    /**
     * Provider for the Location
     */
    public static final String R_PROVIDER = "r_provider";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." + R_LAT + ","
            + TABLE_NAME + "." + R_LONG + ","
            + TABLE_NAME + "." + R_SPEED + ","
            + TABLE_NAME + "." + R_TIME + ","
            + TABLE_NAME + "." + R_PROVIDER + AbstractSelection.DESC;

    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            R_LAT,
            R_LONG,
            R_SPEED,
            R_TIME,
            R_PROVIDER
    };

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(R_LAT) || c.contains("." + R_LAT)) return true;
            if (c.equals(R_LONG) || c.contains("." + R_LONG)) return true;
            if (c.equals(R_SPEED) || c.contains("." + R_SPEED)) return true;
            if (c.equals(R_TIME) || c.contains("." + R_TIME)) return true;
            if (c.equals(R_PROVIDER) || c.contains("." + R_PROVIDER)) return true;
        }
        return false;
    }

}
