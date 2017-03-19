package com.example.android.quakereport;

/**
 * {@link Earthquake} is an object to store
 * all needed information about one earthquake occurrence.
 */
public class Earthquake {

    private String mLocation;
    private double mMagnitude;
    private long mTimeInMilliseconds;
    private String mUrl;

    /** Create a new {@link Earthquake} object to accept given information:
     *
     * @param location is where the earthquake occurred.
     * @param magnitude is the magnitude of the earthquake.
     * @param time is when the earthquake occurred.
     * @param url is the website address for more details on the earthquake.
     */
    public Earthquake(String location, double magnitude, long time, String url){
        this.mLocation = location;
        this.mMagnitude = magnitude;
        this.mTimeInMilliseconds = time;
        this.mUrl = url;
    }

    /** Get the location */
    public String getLocation() {
        return mLocation;
    }

    /** Get the date */
    public long getmTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    /** Get the magnitude */
    public double getMagnitude() {
        return mMagnitude;
    }

    /** Get the url */
    public String getUrl() {
        return mUrl;
    }
}
