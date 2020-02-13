package com.bask.studios.depremBilgi.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RegistrationProvider extends ContentProvider {

    private static final String PROVIDER_NAME = "edu.itu.csc.quakenweather.registration";
    private static final String URL = "content://" + PROVIDER_NAME + "/registration";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String _ID = "_id";
    public static final String APP_NAME = "app_name";
    public static final String INSTALL_DATE = "install_date";
    public static final String LAST_DATE = "last_date";

    private RegistrationDataHelper registrationDataHelper = null;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        registrationDataHelper = new RegistrationDataHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Cursor cursor = registrationDataHelper.getEntry(uri.getPathSegments().get(1), null, null, null, null);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Uri result = null;
        long _id = registrationDataHelper.addEntry(contentValues);
        if ( _id > 0 )
            result = ContentUris.withAppendedId(CONTENT_URI, _id);
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        String id = uri.getPathSegments().get(1);
        int result = registrationDataHelper.deleteEntry(id);
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        String id = id = uri.getPathSegments().get(1);
        int result = registrationDataHelper.updateEntry(id, contentValues);
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }
}
