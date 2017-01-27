package com.demoapp.ceinfo.demolistloader;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.demoapp.ceinfo.demolistloader.provider.location.LocationColumns;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ceinfo on 24-01-2017.
 */

public class LocationAdapter extends CursorAdapter {

    private OnCursorItemClickListener onCursorItemClickListener;

    public LocationAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    public void setOnCursorItemClickListener(OnCursorItemClickListener onCursorItemClickListener) {
        this.onCursorItemClickListener = onCursorItemClickListener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.layout_single_item_location, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tv_rid = (TextView) view.findViewById(R.id.loc_id);
        TextView tv_rlat = (TextView) view.findViewById(R.id.loc_lat);
        TextView tv_rlng = (TextView) view.findViewById(R.id.loc_lng);
        TextView tv_rspeed = (TextView) view.findViewById(R.id.loc_speed);
        TextView tv_rprovider = (TextView) view.findViewById(R.id.loc_provider);
        TextView tv_rtime = (TextView) view.findViewById(R.id.loc_time);

        Integer rid = cursor.getInt(cursor.getColumnIndexOrThrow(LocationColumns._ID));
        final Double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(LocationColumns.R_LAT));
        final Double lng = cursor.getDouble(cursor.getColumnIndexOrThrow(LocationColumns.R_LONG));
        Float speed = cursor.getFloat(cursor.getColumnIndexOrThrow(LocationColumns.R_SPEED));
        Long time = cursor.getLong(cursor.getColumnIndexOrThrow(LocationColumns.R_TIME));
        String provider = cursor.getString(cursor.getColumnIndexOrThrow(LocationColumns.R_PROVIDER));

        tv_rid.setText(String.format(Locale.getDefault(), "%d ", rid));
        tv_rlat.setText(String.format(Locale.getDefault(), "%g ", lat));
        tv_rlng.setText(String.format(Locale.getDefault(), "%g ", lng));
        tv_rspeed.setText(String.format(Locale.getDefault(), "%f ", speed));
        tv_rprovider.setText(String.format(Locale.getDefault(), "%s ", provider));
        tv_rtime.setText(String.format(Locale.getDefault(), "%s ", parseDate(new Date(time))));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onCursorItemClickListener)
                    onCursorItemClickListener.OnCursorItemClick(lat, lng);
            }
        });


    }

    @NonNull
    private String parseDate(Date date) {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        return sdf.format(date);
    }

    public interface OnCursorItemClickListener {
        void OnCursorItemClick(Double latitude, Double longitude);
    }
}
