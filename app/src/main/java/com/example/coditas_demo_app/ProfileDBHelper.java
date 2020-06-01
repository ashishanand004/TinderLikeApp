package com.example.coditas_demo_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class ProfileDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ProfileDB";
    private static final String TABLE_NAME = "Profiles";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_DOB = "dob";
    private static final String KEY_IMAGE_URL = "imageurl";
    private static final String[] COLUMNS = { KEY_ID, KEY_NAME, KEY_LOCATION,
        KEY_DOB, KEY_IMAGE_URL };

    public ProfileDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE Profiles ( "
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT, " + "dob TEXT, "
            + "location TEXT, " + "imageurl TEXT )";

        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void deleteOne(Profile profile) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[] { String.valueOf(profile.getId()) });
        db.close();
    }

    public Profile getProfile(int id) {
        if (id == 0) {
            return null;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // a. table
            COLUMNS, // b. column names
            " id = ?", // c. selections
            new String[] { String.valueOf(id) }, // d. selections args
            null, // e. group by
            null, // f. having
            null, // g. order by
            null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        Profile profile = new Profile();
        profile.setId(Integer.parseInt(cursor.getString(0)));
        profile.setName(cursor.getString(1));
        profile.setDob(cursor.getString(2));
        profile.setLocation(cursor.getString(3));
        profile.setImageUrl(cursor.getString(4));

        return profile;
    }

    public List<Profile> getAllDBProfiles() {

        List<Profile> profiles = new LinkedList<Profile>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Profile profile = null;

        if (cursor.moveToFirst()) {
            do {
                profile = new Profile();
                profile.setId(Integer.parseInt(cursor.getString(0)));
                profile.setName(cursor.getString(1));
                profile.setDob(cursor.getString(2));
                profile.setLocation(cursor.getString(3));
                profile.setImageUrl(cursor.getString(4));
                profiles.add(profile);
            } while (cursor.moveToNext());
        }

        return profiles;
    }

    public void addProfile(Profile profile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, profile.getName());
        values.put(KEY_DOB, profile.getDob());
        values.put(KEY_LOCATION, profile.getLocation());
        values.put(KEY_IMAGE_URL, profile.getImageUrl());
        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }
}
