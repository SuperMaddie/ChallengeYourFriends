package com.example.android.csula.challengefriends.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Savin on 6/5/2016.
 */
public class ChallengeFriendHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 5;

    public static final String DATABASE_NAME = "challenge_friends.db";

    public ChallengeFriendHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_CHALLENGES_TABLE = "CREATE TABLE " + ChallengeFriendContract.Challenges.TABLE_NAME + "(" +
                ChallengeFriendContract.Challenges.COLUMN_CHALLENGE_ID + " INTEGER PRIMARY KEY," +
                ChallengeFriendContract.Challenges.COLUMN_CHALLENGE_TITLE + " TEXT NOT NULL," +
                ChallengeFriendContract.Challenges.COLUMN_CHALLENGE_DESCRIPTION + " TEXT NOT NULL" +
                ")";

        final String SQL_CREATE_RECEIVED_CHALLENGES_TABLE = "CREATE TABLE " + ChallengeFriendContract.ReceivedChallenges.TABLE_NAME + "(" +
                ChallengeFriendContract.ReceivedChallenges.COLUMN_CHALLENGE_ID + " INTEGER PRIMARY KEY," +
                ChallengeFriendContract.ReceivedChallenges.COLUMN_CHALLENGE_TITLE + " TEXT NOT NULL," +
                ChallengeFriendContract.ReceivedChallenges.COLUMN_CHALLENGE_DESCRIPTION + " TEXT NOT NULL, " +
                ChallengeFriendContract.ReceivedChallenges.COLUMN_SENDER_NAME + " TEXT NOT NULL" +
                ")";

        db.execSQL(SQL_CREATE_CHALLENGES_TABLE);
        db.execSQL(SQL_CREATE_RECEIVED_CHALLENGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ChallengeFriendContract.Challenges.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ChallengeFriendContract.ReceivedChallenges.TABLE_NAME);
        onCreate(db);
    }

}












