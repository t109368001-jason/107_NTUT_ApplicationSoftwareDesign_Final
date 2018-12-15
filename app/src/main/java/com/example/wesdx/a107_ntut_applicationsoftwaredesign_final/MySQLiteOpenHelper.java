package com.example.wesdx.a107_ntut_applicationsoftwaredesign_final;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.API;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailDailyTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailGeneralTimetable;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.RailStation;
import com.example.wesdx.a107_ntut_applicationsoftwaredesign_final.PTXAPI.StationOfLine;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME  = "mdatabase.db";
    private static final int DATABASE_VERSION  = 1;

    private static final String RAILSTATION_TABLE = "railStationTable";
    private static final String KEY_TYPE_AND_ID = "typeAndId";
    private static final String KEY_OPERATOR_ID = "OperatorID";
    private static final String KEY_GSON = "Gson";

    private static final String RAILDAILYTIMETABLE_TABLE = "railDailyTimetableTable";
    private static final String KEY_TRAINDATE_AND_TRAINNO = "trainDateAndTrainNo";
    private static final String KEY_TRAINDATE = "trainDate";

    private static final String RAILGENERALTIMETABLE_TABLE = "railGeneralTimetableTable";
    private static final String KEY_OPERATOR_ID_AND_TRAINNO = "operatorIDAndTrainNo";

    private static final String STATIONOFLINE_TABLE = "stationOfLineTable";
    private static final String KEY_LINENO = "lineNo";

    private static final String TAG = "DEBUG_MyRailStationSQL";


    MySQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION );
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ RAILSTATION_TABLE + "(" + KEY_TYPE_AND_ID + " text PRIMARY KEY, " + KEY_OPERATOR_ID + " text NO NULL, " + KEY_GSON + " text NO NULL)");
        db.execSQL("CREATE TABLE "+ RAILDAILYTIMETABLE_TABLE + "(" + KEY_TRAINDATE_AND_TRAINNO + " text PRIMARY KEY, " + KEY_TRAINDATE + " text NO NULL, " + KEY_OPERATOR_ID + " text NO NULL, " + KEY_GSON + " text NO NULL)");
        db.execSQL("CREATE TABLE "+ RAILGENERALTIMETABLE_TABLE + "(" + KEY_OPERATOR_ID_AND_TRAINNO + " text PRIMARY KEY, " + KEY_OPERATOR_ID + " text NO NULL, " + KEY_GSON + " text NO NULL)");
        db.execSQL("CREATE TABLE "+ STATIONOFLINE_TABLE + "(" + KEY_LINENO + " text PRIMARY KEY, " + KEY_GSON + " text NO NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RAILSTATION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + RAILDAILYTIMETABLE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + RAILGENERALTIMETABLE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + STATIONOFLINE_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    public long addOrUpdateStationOfLine(@NonNull StationOfLine stationOfLine) {
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;

        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(KEY_LINENO, stationOfLine.LineNo);
        values.put(KEY_GSON, new Gson().toJson(stationOfLine));

        int rows = db.update(STATIONOFLINE_TABLE, values, KEY_LINENO + "= ?", new String[]{stationOfLine.LineNo});

        if (rows != 1) {
            userId = db.insertOrThrow(STATIONOFLINE_TABLE, null, values);
            db.setTransactionSuccessful();
        }
        db.endTransaction();
        return userId;
    }

    public List<StationOfLine> getAllStationOfLine() {
        List<StationOfLine> stationOfLineList = null;
        String railStationsSelectQuery = String.format("SELECT %s FROM %s", KEY_GSON, STATIONOFLINE_TABLE);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(railStationsSelectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                StationOfLine stationOfLine;
                String railStationGson = cursor.getString(cursor.getColumnIndex(KEY_GSON));
                stationOfLine = new Gson().fromJson(railStationGson, new TypeToken<StationOfLine>() {}.getType());
                if(stationOfLineList == null ) stationOfLineList = new ArrayList<>();
                stationOfLineList.add(stationOfLine);
            } while(cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return stationOfLineList;
    }

    public long addOrUpdateRailStation(@NonNull RailStation railStation) {
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;

        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE_AND_ID, railStation.OperatorID + railStation.StationID);
        values.put(KEY_OPERATOR_ID, railStation.OperatorID);
        values.put(KEY_GSON, new Gson().toJson(railStation));

        int rows = db.update(RAILSTATION_TABLE, values, KEY_TYPE_AND_ID + "= ?", new String[]{railStation.OperatorID + railStation.StationID});

        if (rows != 1) {
            userId = db.insertOrThrow(RAILSTATION_TABLE, null, values);
            db.setTransactionSuccessful();
        }
        db.endTransaction();
        return userId;
    }

    public List<RailStation> getAllRailStations(String transportation) {
        List<RailStation> railStationList = null;
        String railStationsSelectQuery = String.format("SELECT %s FROM %s %s", KEY_GSON, RAILSTATION_TABLE, (transportation.equals(API.TRA_AND_THSR) ? "" : " WHERE " + KEY_OPERATOR_ID + "='" + transportation + "'"));
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(railStationsSelectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                RailStation railStation;
                String railStationGson = cursor.getString(cursor.getColumnIndex(KEY_GSON));
                railStation = new Gson().fromJson(railStationGson, new TypeToken<RailStation>() {}.getType());
                if(railStationList == null ) railStationList = new ArrayList<>();
                railStationList.add(railStation);
            } while(cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return railStationList;
    }

    public long addOrUpdateRailDailyTimetable(String transportation, @NonNull RailDailyTimetable railDailyTimetable) {
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;

        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(KEY_TRAINDATE_AND_TRAINNO, railDailyTimetable.TrainDate + railDailyTimetable.DailyTrainInfo.TrainNo);
        values.put(KEY_TRAINDATE, railDailyTimetable.TrainDate);
        values.put(KEY_OPERATOR_ID, transportation);
        values.put(KEY_GSON, new Gson().toJson(railDailyTimetable));

        int rows = db.update(RAILDAILYTIMETABLE_TABLE, values, KEY_TRAINDATE_AND_TRAINNO + "= ?", new String[]{railDailyTimetable.TrainDate + railDailyTimetable.DailyTrainInfo.TrainNo});

        if (rows != 1) {
            userId = db.insertOrThrow(RAILDAILYTIMETABLE_TABLE, null, values);
            db.setTransactionSuccessful();
        }
        db.endTransaction();
        return userId;
    }

    public List<RailDailyTimetable> getAllRailDailyTimetable(String transportation, String trainDate) {
        List<RailDailyTimetable> railDailyTimetableList = null;
        String filter1 = (transportation.equals(API.TRA_AND_THSR) ? null : KEY_OPERATOR_ID + "='" + transportation + "'");
        String filter2 = (trainDate == null ? null : KEY_TRAINDATE + "='" + trainDate + "'");
        String railStationsSelectQuery = String.format("SELECT %s FROM %s %s", KEY_GSON, RAILDAILYTIMETABLE_TABLE, (filter1 == null ? (filter2 == null ? "" : " WHERE " + filter2) : (" WHERE " + filter1 + (filter2 == null ? "" :  " AND " + filter2))));
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(railStationsSelectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                RailDailyTimetable railDailyTimetable;
                String railDailyTimetableGson = cursor.getString(cursor.getColumnIndex(KEY_GSON));
                railDailyTimetable = new Gson().fromJson(railDailyTimetableGson, new TypeToken<RailDailyTimetable>() {}.getType());
                if(railDailyTimetableList == null ) railDailyTimetableList = new ArrayList<>();
                railDailyTimetableList.add(railDailyTimetable);
            } while(cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return railDailyTimetableList;
    }

    public long addOrUpdateRailGeneralTimetable(String transportation, @NonNull RailGeneralTimetable railGeneralTimetable) {
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;

        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(KEY_OPERATOR_ID_AND_TRAINNO, transportation + railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.TrainNo);
        values.put(KEY_OPERATOR_ID, transportation);
        values.put(KEY_GSON, new Gson().toJson(railGeneralTimetable));

        int rows = db.update(RAILGENERALTIMETABLE_TABLE, values, KEY_OPERATOR_ID_AND_TRAINNO + "= ?", new String[]{transportation + railGeneralTimetable.GeneralTimetable.GeneralTrainInfo.TrainNo});

        if (rows != 1) {
            userId = db.insertOrThrow(RAILGENERALTIMETABLE_TABLE, null, values);
            db.setTransactionSuccessful();
        }
        db.endTransaction();
        return userId;
    }

    public List<RailGeneralTimetable> getAllRailGeneralTimetables(String transportation) {
        if(transportation.equals(API.TRA_AND_THSR)) return null;
        List<RailGeneralTimetable> railGeneralTimetableList = null;
        String railStationsSelectQuery = String.format("SELECT %s FROM %s %s", KEY_GSON, RAILGENERALTIMETABLE_TABLE, " WHERE " + KEY_OPERATOR_ID + "='" + transportation + "'");
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(railStationsSelectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                RailGeneralTimetable railGeneralTimetable;
                String railStationGson = cursor.getString(cursor.getColumnIndex(KEY_GSON));
                railGeneralTimetable = new Gson().fromJson(railStationGson, new TypeToken<RailGeneralTimetable>() {}.getType());
                if(railGeneralTimetableList == null ) railGeneralTimetableList = new ArrayList<>();
                railGeneralTimetableList.add(railGeneralTimetable);
            } while(cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return railGeneralTimetableList;
    }

}
