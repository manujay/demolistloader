package com.demoapp.ceinfo.demolistloader.provider.location;

// @formatter:off

import android.support.annotation.NonNull;

/**
 * Realtime Location Updates from device
 */
@SuppressWarnings({"WeakerAccess", "unused", "ConstantConditions"})
public class LocationBean implements LocationModel {
    private long mId;
    private double mRLat;
    private double mRLong;
    private float mRSpeed;
    private long mRTime;
    private String mRProvider;

    /**
     * Instantiate a new LocationBean with specified values.
     */
    @NonNull
    public static LocationBean newInstance(long id, double rLat, double rLong, float rSpeed, long rTime, @NonNull String rProvider) {
        if (rProvider == null) throw new IllegalArgumentException("rProvider must not be null");
        LocationBean res = new LocationBean();
        res.mId = id;
        res.mRLat = rLat;
        res.mRLong = rLong;
        res.mRSpeed = rSpeed;
        res.mRTime = rTime;
        res.mRProvider = rProvider;
        return res;
    }

    /**
     * Instantiate a new LocationBean with all the values copied from the given model.
     */
    @NonNull
    public static LocationBean copy(@NonNull LocationModel from) {
        LocationBean res = new LocationBean();
        res.mId = from.getId();
        res.mRLat = from.getRLat();
        res.mRLong = from.getRLong();
        res.mRSpeed = from.getRSpeed();
        res.mRTime = from.getRTime();
        res.mRProvider = from.getRProvider();
        return res;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Primary key.
     */
    @Override
    public long getId() {
        return mId;
    }

    /**
     * Primary key.
     */
    public void setId(long id) {
        mId = id;
    }

    /**
     * Latitude
     */
    @Override
    public double getRLat() {
        return mRLat;
    }

    /**
     * Latitude
     */
    public void setRLat(double rLat) {
        mRLat = rLat;
    }

    /**
     * Longitude
     */
    @Override
    public double getRLong() {
        return mRLong;
    }

    /**
     * Longitude
     */
    public void setRLong(double rLong) {
        mRLong = rLong;
    }

    /**
     * Get the {@code r_speed} value.
     */
    @Override
    public float getRSpeed() {
        return mRSpeed;
    }

    /**
     * Set the {@code r_speed} value.
     */
    public void setRSpeed(float rSpeed) {
        mRSpeed = rSpeed;
    }

    /**
     * Get the {@code r_time} value.
     */
    @Override
    public long getRTime() {
        return mRTime;
    }

    /**
     * Set the {@code r_time} value.
     */
    public void setRTime(long rTime) {
        mRTime = rTime;
    }

    /**
     * Provider for the Location
     * Cannot be {@code null}.
     */
    @NonNull
    @Override
    public String getRProvider() {
        return mRProvider;
    }

    /**
     * Provider for the Location
     * Must not be {@code null}.
     */
    public void setRProvider(@NonNull String rProvider) {
        if (rProvider == null) throw new IllegalArgumentException("rProvider must not be null");
        mRProvider = rProvider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationBean bean = (LocationBean) o;
        return mId == bean.mId;
    }

    @Override
    public int hashCode() {
        return (int) (mId ^ (mId >>> 32));
    }

    public static class Builder {
        private LocationBean mRes = new LocationBean();

        /**
         * Primary key.
         */
        public Builder id(long id) {
            mRes.mId = id;
            return this;
        }

        /**
         * Latitude
         */
        public Builder rLat(double rLat) {
            mRes.mRLat = rLat;
            return this;
        }

        /**
         * Longitude
         */
        public Builder rLong(double rLong) {
            mRes.mRLong = rLong;
            return this;
        }

        /**
         * Set the {@code r_speed} value.
         */
        public Builder rSpeed(float rSpeed) {
            mRes.mRSpeed = rSpeed;
            return this;
        }

        /**
         * Set the {@code r_time} value.
         */
        public Builder rTime(long rTime) {
            mRes.mRTime = rTime;
            return this;
        }

        /**
         * Provider for the Location
         * Must not be {@code null}.
         */
        public Builder rProvider(@NonNull String rProvider) {
            if (rProvider == null) throw new IllegalArgumentException("rProvider must not be null");
            mRes.mRProvider = rProvider;
            return this;
        }

        /**
         * Get a new LocationBean built with the given values.
         */
        public LocationBean build() {
            if (mRes.mRProvider == null)
                throw new IllegalArgumentException("rProvider must not be null");
            return mRes;
        }
    }
}
