package com.demoapp.ceinfo.demolistloader;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ceinfo on 01-02-2017.
 */

public class SharedPrefHelper {

    private static final String BOOLEAN_REQUESTING_UPDATES = "key-requesting-updates";
    private static SharedPreferences pref = null;

    private static SharedPreferences getDefaultSharedPref(Context context) {

        if (pref == null)
            pref = context.getSharedPreferences("com.demoapp.ceinfo.demolistloader", Context.MODE_PRIVATE);

        return pref;
    }

    public static void setBooleanUpdates(Context context, boolean isrequest) {
        pref = getDefaultSharedPref(context);
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean(BOOLEAN_REQUESTING_UPDATES, isrequest);
        edit.commit();
    }

    public static boolean getBooleanUpdates(Context context) {
        pref = getDefaultSharedPref(context);
        return pref.getBoolean(BOOLEAN_REQUESTING_UPDATES, false);
    }

}
