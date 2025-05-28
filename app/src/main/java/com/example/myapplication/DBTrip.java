package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBTrip {

    private static final String DATABASE_NAME = "new.simple.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_LOCATION = "tableLocation";
    private static final String TABLE_TRIPS = "tableTrips";

    private static final String COLUMN_IDTRIPS = "idTrips";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LATITUDE = "Latitude";
    private static final String COLUMN_LONGITUDE = "Longitude";
    private static final String COLUMN_SPEED = "Speed";
    private static final String COLUMN_TIME_CODE = "TimeKode";
    private static final String COLUMN_AVERAGE_SPEED = "AverageSpeed";
    private static final String COLUMN_MAX_SPEED = "MaxSpeed";
    private static final String COLUMN_TIME = "Time";
    private static final String COLUMN_DISTANCE = "Distance";
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_DATE = "Date";


    private static final int NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_LATITUDE = 1;
    private static final int NUM_COLUMN_LONGITUDE = 2;
    private static final int NUM_COLUMN_SPEED = 3;
    private static final int NUM_COLUMN_TIME_CODE = 4;
    private static final int NUM_COLUMN_IDTRIPS = 5;
    private static final int NUM_COLUMN_NAME = 1;
    private static final int NUM_COLUMN_DATE = 6;
    private static final int NUM_COLUMN_DISTANCE = 2;
    private static final int NUM_COLUMN_TIME = 3;
    private static final int NUM_COLUMN_AVERAGE_SPEED = 4;
    private static final int NUM_COLUMN_MAX_SPEED = 5;

    private SQLiteDatabase mDataBase;

    public DBTrip(Context context) {
        OpenHelper mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }

    public long insert(double latitude,double longitude,int speed,int timeCode,long idTrips) {
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_LATITUDE, latitude);
        cv.put(COLUMN_LONGITUDE, longitude);
        cv.put(COLUMN_SPEED,speed);
        cv.put(COLUMN_TIME_CODE,timeCode);
        cv.put(COLUMN_IDTRIPS,idTrips);
        return mDataBase.insert(TABLE_LOCATION, null, cv);
    }
    public long insert(String name,int distance,int time,int averageSpeed,int maxSpeed,String date) {
        ContentValues cv= new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_DISTANCE, distance);
        cv.put(COLUMN_TIME, time);
        cv.put(COLUMN_AVERAGE_SPEED,averageSpeed);
        cv.put(COLUMN_MAX_SPEED,maxSpeed);
        cv.put(COLUMN_DATE,date);
        return mDataBase.insert(TABLE_TRIPS, null, cv);
    }

    public int update(Trip trip) {
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_NAME, trip.getName());
        cv.put(COLUMN_DISTANCE, trip.getDistance());
        cv.put(COLUMN_TIME, trip.getTime());
        cv.put(COLUMN_AVERAGE_SPEED,trip.getAverageSpeed());
        cv.put(COLUMN_MAX_SPEED,trip.getMaxSpeed());
        cv.put(COLUMN_DATE,trip.getDate());
        return mDataBase.update(TABLE_TRIPS, cv, COLUMN_ID + " = ?",new String[] { String.valueOf(trip.getId())});
    }

    public int update(MyLocation location) {
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_LATITUDE, location.getLatidute());
        cv.put(COLUMN_LONGITUDE, location.getLongitude());
        cv.put(COLUMN_SPEED,location.getSpeed());
        cv.put(COLUMN_TIME_CODE, location.getTimeCode());
        cv.put(COLUMN_IDTRIPS,location.getIdTrips());
        return mDataBase.update(TABLE_LOCATION, cv, COLUMN_ID + " = ?",new String[] { String.valueOf(location.getId())});
    }

    public void deleteTableLocation() {
        mDataBase.delete(TABLE_LOCATION, null, null);
    }
    public void deleteTableTrips() {
        mDataBase.delete(TABLE_TRIPS, null, null);
    }

    public void deleteAtTableLocation(long id) {
        mDataBase.delete(TABLE_LOCATION, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
    }
    public void deleteAtTableTrips(long id) {
        mDataBase.delete(TABLE_TRIPS, COLUMN_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public Trip selectTrip(long id) {
        Cursor mCursor = mDataBase.query(TABLE_TRIPS, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        mCursor.moveToFirst();
        String name = mCursor.getString(NUM_COLUMN_NAME);
        int distance = mCursor.getInt(NUM_COLUMN_DISTANCE);
        int time = mCursor.getInt(NUM_COLUMN_TIME);
        int averageSpeed =mCursor.getInt(NUM_COLUMN_AVERAGE_SPEED);
        int maxSpeed =mCursor.getInt(NUM_COLUMN_MAX_SPEED);
        String date = mCursor.getString(NUM_COLUMN_DATE);
        return new Trip(id, name, distance, time,averageSpeed,maxSpeed, date);
    }
    public ArrayList<MyLocation> selectLocations(long idTrips) {
        Cursor mCursor = mDataBase.query(TABLE_LOCATION, null, COLUMN_IDTRIPS + " = " + idTrips, new String[]{String.valueOf(idTrips)}, null, null, null);
        ArrayList<MyLocation> arrLocations = new ArrayList<>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getInt(NUM_COLUMN_ID);
                double latidute = mCursor.getDouble(NUM_COLUMN_LATITUDE);
                double longitude = mCursor.getDouble(NUM_COLUMN_LONGITUDE);
                int speed = mCursor.getInt(NUM_COLUMN_SPEED);
                int timeCode = mCursor.getInt(NUM_COLUMN_TIME_CODE);
                arrLocations.add(new MyLocation(id,latidute,longitude,speed,timeCode,idTrips));
            } while (mCursor.moveToNext());
        }
        return arrLocations;
    }

    public ArrayList<Trip> selectAllTrips() {
        Cursor mCursor = mDataBase.query(TABLE_TRIPS, null, null, null, null, null, null);

        ArrayList<Trip> arr = new ArrayList<Trip>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                String name = mCursor.getString(NUM_COLUMN_NAME);
                int distance = mCursor.getInt(NUM_COLUMN_DISTANCE);
                int time = mCursor.getInt(NUM_COLUMN_TIME);
                int averageSpeed =mCursor.getInt(NUM_COLUMN_AVERAGE_SPEED);
                int maxSpeed =mCursor.getInt(NUM_COLUMN_MAX_SPEED);
                String date = mCursor.getString(NUM_COLUMN_DATE);
                arr.add(new Trip(id, name, distance, time,averageSpeed,maxSpeed,date));
            } while (mCursor.moveToNext());
        }
        return arr;
    }

    public ArrayList<MyLocation> selectAllLocations() {
        Cursor mCursor = mDataBase.query(TABLE_LOCATION, null, null, null, null, null, null);

        ArrayList<MyLocation> arr = new ArrayList<MyLocation>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getInt(NUM_COLUMN_ID);
                double latidute = mCursor.getDouble(NUM_COLUMN_LATITUDE);
                double longitude = mCursor.getDouble(NUM_COLUMN_LONGITUDE);
                int speed = mCursor.getInt(NUM_COLUMN_SPEED);
                int timeCode = mCursor.getInt(NUM_COLUMN_TIME_CODE);
                long idTrips = mCursor.getLong(NUM_COLUMN_IDTRIPS);
                arr.add(new MyLocation(id,latidute,longitude,speed,timeCode,idTrips));
            } while (mCursor.moveToNext());
        }
        return arr;
    }

    private class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            String tableLocation = "CREATE TABLE " + TABLE_LOCATION + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_LATITUDE+ " RAEL, " +
                    COLUMN_LONGITUDE + " REAL, " +
                    COLUMN_TIME_CODE + " INTEGER, " +
                    COLUMN_SPEED + " INTEGER, " +
                    COLUMN_IDTRIPS + " INTEGER);";
            String tableTrips = "CREATE TABLE " + TABLE_TRIPS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_DISTANCE + " INTEGER, " +
                    COLUMN_TIME+ " INTEGER, " +
                    COLUMN_AVERAGE_SPEED + " INTEGER, " +
                    COLUMN_MAX_SPEED + " INETEGER," +
                    COLUMN_DATE + " TEXT);";
            db.execSQL(tableTrips);
            db.execSQL(tableLocation);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

}
