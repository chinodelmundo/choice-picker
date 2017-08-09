package com.example.chinodelmundo.choicepicker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Chino.DelMundo on 8/3/2017.
 */

public final class FeedReaderContract {
    private FeedReaderContract() {}
    private static final String SQL_CREATE_QUESTIONS =
            "CREATE TABLE " + FeedEntry.QUESTIONS_TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.QUESTIONS_COLUMN_NAME_TEXT + " TEXT)";

    private static final String SQL_DELETE_QUESTIONS =
            "DROP TABLE IF EXISTS " + FeedEntry.QUESTIONS_TABLE_NAME;

    private static final String SQL_CREATE_CHOICES =
            "CREATE TABLE " + FeedEntry.CHOICES_TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.CHOICES_COLUMN_NAME_QUESTION_ID + " INTEGER," +
                    FeedEntry.CHOICES_COLUMN_NAME_TEXT + " TEXT)";

    private static final String SQL_DELETE_CHOICES =
            "DROP TABLE IF EXISTS " + FeedEntry.CHOICES_TABLE_NAME;

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String QUESTIONS_TABLE_NAME = "QUESTIONS";
        public static final String QUESTIONS_COLUMN_NAME_TEXT = "TEXT";

        public static final String CHOICES_TABLE_NAME = "CHOICES";
        public static final String CHOICES_COLUMN_NAME_QUESTION_ID = "QUESTION_ID";
        public static final String CHOICES_COLUMN_NAME_TEXT = "TEXT";
    }

    public static class FeedReaderDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FeedReader.db";

        public FeedReaderDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_QUESTIONS);
            db.execSQL(SQL_CREATE_CHOICES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_QUESTIONS);
            db.execSQL(SQL_DELETE_CHOICES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}