package com.bask.studios.depremBilgi.models;

import android.util.Log;

import com.bask.studios.depremBilgi.activities.MainActivity;
import com.bask.studios.depremBilgi.utilities.Utility;


public class Quake {

    private String magnitude;

    private double longitude;

    private double latitude;

    private String place;

    private double depth;

    private String time;

    private String url;

    private String significance;

    private String eventId;

    private String status;

    private String title;

    public String getUrl() {
        return url;
    }

    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getMagnitude() {
        return magnitude;
    }

    public String getPlace() {
        return place;
    }

    public String getFormattedPlace() { return Utility.getPlace(place); }

    public double getDepth() {
        return depth;
    }

    public String getTime() {
        return time;
    }

    public String getFormattedTime() {
        String formattedTime = "N/A";
        try {
            formattedTime =  Utility.getDateTime(Long.parseLong(time));
        } catch (Exception exception) {
            Log.d(MainActivity.APP_TAG, "Quake getFormattedTime: " + exception.toString());
        }
        return formattedTime;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getSignificance() { return significance; }

    public String getEventId() { return eventId; }

    public String getFormattedCoordinates() {return this.getLatitude() + "\u00B0, " + this.getLongitude() + "\u00B0"; }

    public Quake(String magnitude, double longitude, double latitude, String place, double depth, String time, String url, String status, String title, String significance, String eventId) {
        this.magnitude = magnitude;
        this.longitude = longitude;
        this.latitude = latitude;
        this.place = place;
        this.depth = depth;
        this.time = time;
        this.url = url;
        this.status = status;
        this.title = title;
        this.significance = significance;
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "Quake{" +
                "magnitude='" + magnitude + '\'' +
                ", boylam=" + longitude +
                ", Enlem=" + latitude +
                ", place='" + place + '\'' +
                ", derinlik='" + depth + '\'' +
                ", zaman='" + time + '\'' +
                ", url='" + url + '\'' +
                ", önem='" + significance + '\'' +
                ", eventId='" + eventId + '\'' +
                ", durum='" + status + '\'' +
                ", başlık='" + title + '\'' +
                '}';
    }
}
