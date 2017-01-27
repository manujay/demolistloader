package com.demoapp.ceinfo.demolistloader.provider;

// @formatter:off
import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.demoapp.ceinfo.demolistloader.BuildConfig;
import com.demoapp.ceinfo.demolistloader.provider.location.LocationColumns;
import com.demoapp.ceinfo.demolistloader.provider.person.PersonColumns;

public class SampleSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = SampleSQLiteOpenHelper.class.getSimpleName();

    public static final String DATABASE_FILE_NAME = "sample.db";
    private static final int DATABASE_VERSION = 1;
    private static SampleSQLiteOpenHelper sInstance;
    private final Context mContext;
    private final SampleSQLiteOpenHelperCallbacks mOpenHelperCallbacks;

    public static final String SQL_CREATE_TABLE_LOCATION = "CREATE TABLE IF NOT EXISTS "
            + LocationColumns.TABLE_NAME + " ( "
            + LocationColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + LocationColumns.R_LAT + " REAL NOT NULL, "
            + LocationColumns.R_LONG + " REAL NOT NULL, "
            + LocationColumns.R_SPEED + " REAL NOT NULL, "
            + LocationColumns.R_TIME + " INTEGER NOT NULL DEFAULT 0, "
            + LocationColumns.R_PROVIDER + " TEXT NOT NULL DEFAULT 'Unknown' "
            + " );";

    public static final String SQL_CREATE_TABLE_PERSON = "CREATE TABLE IF NOT EXISTS "
            + PersonColumns.TABLE_NAME + " ( "
            + PersonColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PersonColumns.FIRST_NAME + " TEXT NOT NULL, "
            + PersonColumns.LAST_NAME + " TEXT NOT NULL, "
            + PersonColumns.AGE + " INTEGER NOT NULL, "
            + PersonColumns.BIRTH_DATE + " INTEGER, "
            + PersonColumns.HAS_BLUE_EYES + " INTEGER NOT NULL DEFAULT 0, "
            + PersonColumns.HEIGHT + " REAL, "
            + PersonColumns.GENDER + " INTEGER NOT NULL, "
            + PersonColumns.COUNTRY_CODE + " TEXT NOT NULL "
            + ", CONSTRAINT unique_name UNIQUE (first_name, last_name) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_PERSON_LAST_NAME = "CREATE INDEX IDX_PERSON_LAST_NAME "
            + " ON " + PersonColumns.TABLE_NAME + " ( " + PersonColumns.LAST_NAME + " );";


    public static SampleSQLiteOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static SampleSQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */
    private static SampleSQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new SampleSQLiteOpenHelper(context);
    }

    private SampleSQLiteOpenHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mOpenHelperCallbacks = new SampleSQLiteOpenHelperCallbacks();
    }


    /*
     * Post Honeycomb.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static SampleSQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new SampleSQLiteOpenHelper(context, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private SampleSQLiteOpenHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new SampleSQLiteOpenHelperCallbacks();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_LOCATION);
        db.execSQL(SQL_CREATE_TABLE_PERSON);
        db.execSQL(SQL_CREATE_INDEX_PERSON_LAST_NAME);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
