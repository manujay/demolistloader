package com.demoapp.ceinfo.demolistloader.provider.person;

// @formatter:off

import android.net.Uri;
import android.provider.BaseColumns;

import com.demoapp.ceinfo.demolistloader.provider.SampleProvider;
import com.demoapp.ceinfo.demolistloader.provider.base.AbstractSelection;

/**
 * A human being which is part of a team.
 */
@SuppressWarnings("unused")
public class PersonColumns implements BaseColumns {
    public static final String TABLE_NAME = "person";
    public static final Uri CONTENT_URI = Uri.parse(SampleProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    /**
     * First name of this person. For instance, John.
     */
    public static final String FIRST_NAME = "first_name";

    /**
     * Last name (a.k.a. Given name) of this person. For instance, Smith.
     */
    public static final String LAST_NAME = "last_name";

    public static final String AGE = "age";

    public static final String BIRTH_DATE = "birth_date";

    /**
     * If {@code true}, this person has blue eyes. Otherwise, this person doesn't have blue eyes.
     */
    public static final String HAS_BLUE_EYES = "has_blue_eyes";

    public static final String HEIGHT = "height";

    public static final String GENDER = "gender";

    public static final String COUNTRY_CODE = "country_code";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." + FIRST_NAME + ","
            + TABLE_NAME + "." + LAST_NAME + ","
            + TABLE_NAME + "." + AGE + AbstractSelection.DESC;

    public static final String[] ALL_COLUMNS = new String[]{
            _ID,
            FIRST_NAME,
            LAST_NAME,
            AGE,
            BIRTH_DATE,
            HAS_BLUE_EYES,
            HEIGHT,
            GENDER,
            COUNTRY_CODE
    };

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(FIRST_NAME) || c.contains("." + FIRST_NAME)) return true;
            if (c.equals(LAST_NAME) || c.contains("." + LAST_NAME)) return true;
            if (c.equals(AGE) || c.contains("." + AGE)) return true;
            if (c.equals(BIRTH_DATE) || c.contains("." + BIRTH_DATE)) return true;
            if (c.equals(HAS_BLUE_EYES) || c.contains("." + HAS_BLUE_EYES)) return true;
            if (c.equals(HEIGHT) || c.contains("." + HEIGHT)) return true;
            if (c.equals(GENDER) || c.contains("." + GENDER)) return true;
            if (c.equals(COUNTRY_CODE) || c.contains("." + COUNTRY_CODE)) return true;
        }
        return false;
    }

}
