package com.scoreboard.badminton;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.scoreboard.badminton.Model.Match;

import java.util.ArrayList;

/**
 * Created by HOME on 1/4/2019.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "BadmintonScoreBoard.db";
    public static final String MATCH_TABLE_NAME = "match";
    public static final String TOURNAMENT_EDITION_TABLE_NAME = "edition_table";
    public static final String id = "id";
    public static final String matchID = "matchID";
    public static final String editionNumber = "editionNumber";
    public static final String matchDuration = "matchDuration";
    public static final String team1 = "team1";
    public static final String team2 = "team2";
    public static final String game1Point = "game1Point";
    public static final String game3Point = "game3Point";
    public static final String game2Point = "game2Point";
    public static final String winnerTeam = "winnerTeam";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
            "CREATE TABLE " + MATCH_TABLE_NAME +
             "("
                + id + " INTEGER PRIMARY KEY," + matchID + " TEXT,"
                + matchDuration + " INTEGER," + winnerTeam + " TEXT,"
                + team2 + " TEXT," + team1 + " TEXT,"
                + game1Point + " TEXT," + game2Point + " TEXT,"
                + game3Point + " TEXT " +
             ")"
        );
        db.execSQL(
                "CREATE TABLE " + TOURNAMENT_EDITION_TABLE_NAME +
                        "("
                        + id + " INTEGER PRIMARY KEY," + editionNumber + " TEXT "+
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + MATCH_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TOURNAMENT_EDITION_TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<Match> getAllMatches() {
        ArrayList<Match> array_list = new ArrayList<Match>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from match", null );
        res.moveToFirst();
        String allPoints = "";

        while(res.isAfterLast() == false){
            Match match = new Match();
            match.setMatchID(res.getString(res.getColumnIndex(matchID)));
            match.setTeam1(res.getString(res.getColumnIndex(team1)));
            match.setTeam2(res.getString(res.getColumnIndex(team2)));
            match.setWinnerTeam((res.getString(res.getColumnIndex(winnerTeam))));
            match.setMatchDuration(Long.valueOf(res.getString(res.getColumnIndex(matchDuration))));

            allPoints = "";
            allPoints = res.getString(res.getColumnIndex(game1Point)) + " , " + res.getString(res.getColumnIndex(game2Point));
            if(!res.getString(res.getColumnIndex(game3Point)).equals("0 - 0")){
                allPoints = allPoints + " , " + res.getString(res.getColumnIndex(game3Point));
            }
            match.setAllPoints(allPoints);
            array_list.add(match);
            res.moveToNext();
        }
        return array_list;
    }

    public boolean insertContact (Match match) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(matchID, match.getMatchID());
        contentValues.put(matchDuration, match.getMatchDuration());
        contentValues.put(team1, match.getTeamList().get(0).getLeftPlayer() + " / " + match.getTeamList().get(0).getRightPlayer());
        contentValues.put(team2, match.getTeamList().get(1).getLeftPlayer() + " / " + match.getTeamList().get(1).getRightPlayer());
        contentValues.put(game1Point, match.getPoints()[0][0] + " - " + match.getPoints()[0][1]);
        contentValues.put(game2Point, match.getPoints()[1][0] + " - " + match.getPoints()[1][1]);
        contentValues.put(game3Point, match.getPoints()[2][0] + " - " + match.getPoints()[2][1]);
        contentValues.put(winnerTeam, match.getWinnerTeam());
        db.insert(MATCH_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertTournamentEdition (String edition) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(editionNumber, edition);
        db.insert(TOURNAMENT_EDITION_TABLE_NAME, null, contentValues);
        return true;
    }

    public String getEdition() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from edition_table", null );
        res.moveToFirst();
        String edition = "";
        while(res.isAfterLast() == false){
            edition = res.getString(res.getColumnIndex(editionNumber));
            res.moveToNext();
        }
        return edition;
    }

}
