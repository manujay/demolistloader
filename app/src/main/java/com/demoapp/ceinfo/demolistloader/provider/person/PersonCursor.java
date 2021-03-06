package com.demoapp.ceinfo.demolistloader.provider.person;

// @formatter:off

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.demoapp.ceinfo.demolistloader.provider.base.AbstractCursor;

import java.util.Date;

/**
 * Cursor wrapper for the {@code person} table.
 */
@SuppressWarnings({"WeakerAccess", "unused", "UnnecessaryLocalVariable"})
public class PersonCursor extends AbstractCursor implements PersonModel {
    public PersonCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    @Override
    public long getId() {
        Long res = getLongOrNull(PersonColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * First name of this person. For instance, John.
     * Cannot be {@code null}.
     */
    @NonNull
    @Override
    public String getFirstName() {
        String res = getStringOrNull(PersonColumns.FIRST_NAME);
        if (res == null)
            throw new NullPointerException("The value of 'first_name' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Last name (a.k.a. Given name) of this person. For instance, Smith.
     * Cannot be {@code null}.
     */
    @NonNull
    @Override
    public String getLastName() {
        String res = getStringOrNull(PersonColumns.LAST_NAME);
        if (res == null)
            throw new NullPointerException("The value of 'last_name' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code age} value.
     */
    @Override
    public int getAge() {
        Integer res = getIntegerOrNull(PersonColumns.AGE);
        if (res == null)
            throw new NullPointerException("The value of 'age' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code birth_date} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Date getBirthDate() {
        Date res = getDateOrNull(PersonColumns.BIRTH_DATE);
        return res;
    }

    /**
     * If {@code true}, this person has blue eyes. Otherwise, this person doesn't have blue eyes.
     */
    @Override
    public boolean getHasBlueEyes() {
        Boolean res = getBooleanOrNull(PersonColumns.HAS_BLUE_EYES);
        if (res == null)
            throw new NullPointerException("The value of 'has_blue_eyes' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code height} value.
     * Can be {@code null}.
     */
    @Nullable
    @Override
    public Float getHeight() {
        Float res = getFloatOrNull(PersonColumns.HEIGHT);
        return res;
    }

    /**
     * Get the {@code gender} value.
     * Cannot be {@code null}.
     */
    @NonNull
    @Override
    public Gender getGender() {
        Integer intValue = getIntegerOrNull(PersonColumns.GENDER);
        if (intValue == null)
            throw new NullPointerException("The value of 'gender' in the database was null, which is not allowed according to the model definition");
        return Gender.values()[intValue];
    }

    /**
     * Get the {@code country_code} value.
     * Cannot be {@code null}.
     */
    @NonNull
    @Override
    public String getCountryCode() {
        String res = getStringOrNull(PersonColumns.COUNTRY_CODE);
        if (res == null)
            throw new NullPointerException("The value of 'country_code' in the database was null, which is not allowed according to the model definition");
        return res;
    }
}
