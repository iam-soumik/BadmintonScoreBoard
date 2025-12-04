package com.scoreboard.badminton;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.scoreboard.badminton.Model.Game;
import com.scoreboard.badminton.Model.Match;
import com.scoreboard.badminton.Model.Team;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by HOME on 12/26/2018.
 */

public class GameActivity extends Activity implements View.OnClickListener, TextToSpeech.OnInitListener{

    ImageButton ib_left_reverse, ib_right_reverse, ib_center_reverse, ib_start, ib_undo, ib_sound_off, ib_audio, ib_next_audio;
    EditText et_left_court_left_player, et_left_court_right_player, et_right_court_left_player, et_right_court_right_player;
    TextView tv_match_name, tv_team_one, tv_team_two, tv_game_one_team_one, tv_game_one_team_two,
            tv_game_two_team_one, tv_game_two_team_two, tv_game_three_team_one, tv_game_three_team_two,
            tv_game_point, tv_next_match, tv_left_point_count, tv_right_point_count,
            tv_right_point, tv_left_point, tv_heading;
    Chronometer cm_timer;
    RelativeLayout left_third_court, right_third_court, left_odd_court, left_even_court, right_even_court, right_odd_court;
    int currentServingTeam = 0, previousServingTeam = 0, previousServingSideLayoutId = 0, serviceCounter = -1,
            leftTeampoint = 0, rightTeampoint = 0;
    List<Integer> currentServingTeamList;
    boolean isGameOver = false, isSetupReady = false, isSoundActive = true;
    Team team1, team2;
    private TextToSpeech tts;
    DBHelper dbHelper;
    String currentPointToAnnounce = "", nextMatchTeams = "", heading = "", year_offset = "";
    int curYear = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        tts = new TextToSpeech(this, this);
        dbHelper = new DBHelper(this);

        initializeData();
        initializeViews();
        setUpHeading();
        displayNextTeam();
    }

    private void setUpHeading(){
        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int yr = 10 + (curYear - 2021); // get edition of tournament.e.g 10th,11th etc. Taking 2021 as 10th year
        if(yr < 21){
            year_offset = yr + "th";
        }else{
            if(yr % 10 == 1){
                year_offset = yr + "st";
            }else if(yr % 10 == 2){
                year_offset = yr + "nd";
            }
            else if(yr % 10 == 3){
                year_offset = yr + "rd";
            }else{
                year_offset = yr + "th";
            }
        }
        heading = getString(R.string.app_heading) + " " + curYear + "(" + year_offset + " year" + ")";
        tv_heading.setText(heading);
    }
    private void setServingIndicator() {
        int point = BSBGlobalVariable.currentGame.getPointsList().get(currentServingTeam).
                get(BSBGlobalVariable.currentGame.getPointsList().get(currentServingTeam).size()-1);
        if(currentServingTeam == 0){    //Left
            if(point % 2 == 0){

                if(previousServingSideLayoutId != 0){
                    ((RelativeLayout) findViewById(previousServingSideLayoutId)).
                            setBackgroundColor(ContextCompat.getColor(this,R.color.courtColor));
                }
                left_even_court.setBackgroundColor(ContextCompat.getColor(this, R.color.pointViewHighlightedColor));
                previousServingSideLayoutId = left_even_court.getId();
            }else{

                if(previousServingSideLayoutId != 0){
                    ((RelativeLayout) findViewById(previousServingSideLayoutId)).
                            setBackgroundColor(ContextCompat.getColor(this,R.color.courtColor));
                }
                left_odd_court.setBackgroundColor(ContextCompat.getColor(this, R.color.pointViewHighlightedColor));
                previousServingSideLayoutId = left_odd_court.getId();
            }
        }
        if(currentServingTeam == 1){    //Right
            if(point % 2 == 0){

                if(previousServingSideLayoutId != 0){
                    ((RelativeLayout) findViewById(previousServingSideLayoutId)).
                            setBackgroundColor(ContextCompat.getColor(this,R.color.courtColor));
                }
                right_even_court.setBackgroundColor(ContextCompat.getColor(this, R.color.pointViewHighlightedColor));
                previousServingSideLayoutId = right_even_court.getId();
            }else{

                if(previousServingSideLayoutId != 0){
                    ((RelativeLayout) findViewById(previousServingSideLayoutId)).
                            setBackgroundColor(ContextCompat.getColor(this,R.color.courtColor));
                }
                right_odd_court.setBackgroundColor(ContextCompat.getColor(this, R.color.pointViewHighlightedColor));
                previousServingSideLayoutId = right_odd_court.getId();
            }
        }
    }

    private void initializeData() {
        isSetupReady = true;
        currentServingTeamList = new ArrayList<>();
        currentServingTeam = BSBGlobalVariable.currentGame.getServingTeam();
        currentServingTeamList.add(++serviceCounter, currentServingTeam);
    }

    private void initializeViews() {
        ib_center_reverse   = (ImageButton) findViewById(R.id.ib_center_reverse);
        ib_left_reverse     = (ImageButton) findViewById(R.id.ib_left_reverse);
        ib_right_reverse    = (ImageButton) findViewById(R.id.ib_right_reverse);
        ib_start   = (ImageButton) findViewById(R.id.ib_start);
        ib_audio   = (ImageButton) findViewById(R.id.ib_audio);
        ib_undo    = (ImageButton) findViewById(R.id.ib_undo);
        ib_sound_off    = (ImageButton) findViewById(R.id.ib_sound_off);
        ib_next_audio   = (ImageButton) findViewById(R.id.ib_next_audio);
        left_third_court    = (RelativeLayout) findViewById(R.id.left_third_court);
        right_third_court   = (RelativeLayout) findViewById(R.id.right_third_court);
        left_odd_court      = (RelativeLayout) findViewById(R.id.left_odd_court);
        left_even_court     = (RelativeLayout) findViewById(R.id.left_even_court);
        right_even_court    = (RelativeLayout) findViewById(R.id.right_even_court);
        right_odd_court     = (RelativeLayout) findViewById(R.id.right_odd_court);
        tv_heading = (TextView) findViewById(R.id.textView);
        cm_timer = (Chronometer) findViewById(R.id.cm_timer);
        et_left_court_left_player   = (EditText) findViewById(R.id.et_left_court_left_player);
        et_left_court_right_player  = (EditText) findViewById(R.id.et_left_court_right_player);
        et_right_court_left_player  = (EditText) findViewById(R.id.et_right_court_left_player);
        et_right_court_right_player = (EditText) findViewById(R.id.et_right_court_right_player);

        tv_match_name  = (TextView) findViewById(R.id.tv_match_name);
        tv_team_one    = (TextView) findViewById(R.id.tv_team_one);
        tv_team_two    = (TextView) findViewById(R.id.tv_team_two);
        tv_game_one_team_one    = (TextView) findViewById(R.id.tv_game_one_team_one);
        tv_game_one_team_two    = (TextView) findViewById(R.id.tv_game_one_team_two);
        tv_game_two_team_one    = (TextView) findViewById(R.id.tv_game_two_team_one);
        tv_game_two_team_two    = (TextView) findViewById(R.id.tv_game_two_team_two);
        tv_game_three_team_one  = (TextView) findViewById(R.id.tv_game_three_team_one);
        tv_game_three_team_two  = (TextView) findViewById(R.id.tv_game_three_team_two);
        tv_game_point    = (TextView) findViewById(R.id.tv_game_point);
        tv_next_match    = (TextView) findViewById(R.id.tv_next_match);
        tv_left_point_count    = (TextView) findViewById(R.id.tv_left_point_count);
        tv_right_point_count   = (TextView) findViewById(R.id.tv_right_point_count);
        tv_left_point   = (TextView) findViewById(R.id.tv_left_point);
        tv_right_point  = (TextView) findViewById(R.id.tv_right_point);

        et_left_court_left_player.setEnabled(false);
        et_left_court_right_player.setEnabled(false);
        et_right_court_left_player.setEnabled(false);
        et_right_court_right_player.setEnabled(false);
        ib_left_reverse.setVisibility(View.GONE);
        ib_right_reverse.setVisibility(View.GONE);
        ib_center_reverse.setVisibility(View.GONE);
        tv_left_point_count.setVisibility(View.VISIBLE);
        tv_right_point_count.setVisibility(View.VISIBLE);
        tv_left_point.setVisibility(View.VISIBLE);
        tv_right_point.setVisibility(View.VISIBLE);

        ib_center_reverse.setOnClickListener(this);
        ib_right_reverse.setOnClickListener(this);
        ib_left_reverse.setOnClickListener(this);
        ib_next_audio.setOnClickListener(this);
        left_third_court.setOnClickListener(this);
        right_third_court.setOnClickListener(this);
        //ib_start.setOnClickListener(this);
        ib_undo.setOnClickListener(this);
        ib_sound_off.setOnClickListener(this);
        ib_audio.setOnClickListener(this);
        cm_timer.setBase(SystemClock.elapsedRealtime());
        cm_timer.start();

        tv_match_name.setText(BSBGlobalVariable.currentMatch.getMatchID());
        setPlayerName();
        setPlayerNameInScoreBoard();
        setGamePointDisplay(leftTeampoint,rightTeampoint, false);
        setScoreBoardPoint("L", leftTeampoint);
        setScoreBoardPoint("R", rightTeampoint);
        setServingIndicator();
    }

    private void setPlayerName() {
        et_left_court_left_player.setText(BSBGlobalVariable.currentGame.getTeamList().get(0).getLeftPlayer());
        et_left_court_right_player.setText(BSBGlobalVariable.currentGame.getTeamList().get(0).getRightPlayer());
        et_right_court_left_player.setText(BSBGlobalVariable.currentGame.getTeamList().get(1).getLeftPlayer());
        et_right_court_right_player.setText(BSBGlobalVariable.currentGame.getTeamList().get(1).getRightPlayer());
    }

    private void setPlayerNameInScoreBoard(){
        tv_team_one.setText(BSBGlobalVariable.currentGame.getTeamList().get(0).getLeftPlayer() + "/" +
                BSBGlobalVariable.currentGame.getTeamList().get(0).getRightPlayer());
        tv_team_two.setText(BSBGlobalVariable.currentGame.getTeamList().get(1).getRightPlayer() + "/" +
                BSBGlobalVariable.currentGame.getTeamList().get(1).getLeftPlayer());
    }

    @Override
    public void onClick(View v) {
        if(v == right_third_court) {
            if(isSetupReady){
                if (isGameOver == false) {
                    incrementRightCourtPoint();
                }
                else{
                    checkWiningGamePoint();
                }
            }else{
                Toast.makeText(this, "Complete setup and start game", Toast.LENGTH_SHORT).show();
            }
        }
        if(v == left_third_court){
            if(isSetupReady){
                if(isGameOver == false){
                    incrementLeftCourtPoint();
                }
                else{
                    checkWiningGamePoint();
                }
            }else{
                Toast.makeText(this, "Complete setup and start game", Toast.LENGTH_SHORT).show();
            }
        }
        if(v == ib_undo){
            if(isSetupReady){
                undoPreviousChange();
            }
        }
        if(v == ib_audio){
            announceCurrentPoint();
        }
        if(v == ib_left_reverse){
            team1.setLeftPlayer(et_left_court_left_player.getText().toString().trim());
            team1.setRightPlayer(et_left_court_right_player.getText().toString().trim());
            swapLeftTeamPlayers();
        }
        if(v == ib_right_reverse){
            team2.setLeftPlayer(et_right_court_left_player.getText().toString().trim());
            team2.setRightPlayer(et_right_court_right_player.getText().toString().trim());
            swapRightTeamPlayers();
        }
        if(v == ib_start){ // Not applicable for first game
            if(BSBGlobalVariable.gameNumber > 1){
                displayGameStartAlert();
            }
        }
        if(v == ib_next_audio){
            if(!nextMatchTeams.equalsIgnoreCase(null) && !nextMatchTeams.equals("")){
                String replacedString = nextMatchTeams.replace("/", " and ");
                replacedString = replacedString.replace(" VS ", " Versus ");
                //Log.d("================ ","nextMatchTeams : "  + nextMatchTeams + " ||| replacedString : " + replacedString);
                speakOut("Next match : " + replacedString + ". Please prepare and be ready");
            }
        }
        if(v == ib_sound_off){
            if(isSoundActive == true){
                isSoundActive = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ib_sound_off.setImageDrawable(getResources().getDrawable(R.drawable.volume_mute, getApplicationContext().getTheme()));
                } else {
                    ib_sound_off.setImageDrawable(getResources().getDrawable(R.drawable.volume_mute));
                }
            }else if(isSoundActive == false){
                isSoundActive = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ib_sound_off.setImageDrawable(getResources().getDrawable(R.drawable.volume_unmute, getApplicationContext().getTheme()));
                } else {
                    ib_sound_off.setImageDrawable(getResources().getDrawable(R.drawable.volume_unmute));
                }
            }
        }
    }

    private void announceCurrentPoint() {
        speakOut(currentPointToAnnounce);
    }
    private void announceWinner(String message) {
        String modifiedMsg = message.replace("/"," and ");
        speakOut(modifiedMsg);
    }
    private void announceCourtSwap(String message) {
        speakOut(message);
    }


    private void incrementLeftCourtPoint() {
        int left_point = BSBGlobalVariable.currentGame.getPointsList().get(0).
                get(BSBGlobalVariable.currentGame.getPointsList().get(0).size()-1) + 1;
        int right_point = BSBGlobalVariable.currentGame.getPointsList().get(1).
                get(BSBGlobalVariable.currentGame.getPointsList().get(1).size()-1);

        leftTeampoint = left_point;
        rightTeampoint = right_point;

        BSBGlobalVariable.currentGame.getPointsList().get(0).add(left_point);
        BSBGlobalVariable.currentGame.getPointsList().get(1).add(right_point);

        tv_left_point.setText(String.valueOf(left_point));
        previousServingTeam = currentServingTeam;
        currentServingTeam = 0;

        setScoreBoardPoint(getTeamPosition(BSBGlobalVariable.currentGame.getTeamList().get(0)), left_point);
        setGamePointDisplay(left_point,right_point, true);

        if(BSBGlobalVariable.gameNumber == 3){ //FINAL GAME, SWAP COURT
            if(leftTeampoint == 11 && rightTeampoint < 11){
                swapTeamDuring3rdSet();
                if(previousServingTeam == 1){
                    previousServingTeam = 0;
                }
                else{
                    previousServingTeam = 1;
                }
                currentServingTeam = 1;

                swapPointListDuringThirdGame();
                tv_left_point.setText(String.valueOf(right_point));  // Swap point display in court
                tv_right_point.setText(String.valueOf(left_point));
            }
        }
        currentServingTeamList.add(++serviceCounter, currentServingTeam);
        setServingIndicator();
        swapTeamPlayersDuringServeContinue(currentServingTeam);
        checkWiningGamePoint();
    }

    private void incrementRightCourtPoint() {
        int left_point = BSBGlobalVariable.currentGame.getPointsList().get(0).
                get(BSBGlobalVariable.currentGame.getPointsList().get(0).size()-1);
        int right_point = BSBGlobalVariable.currentGame.getPointsList().get(1).
                get(BSBGlobalVariable.currentGame.getPointsList().get(1).size()-1) + 1;

        leftTeampoint = left_point;
        rightTeampoint = right_point;
        BSBGlobalVariable.currentGame.getPointsList().get(0).add(left_point);
        BSBGlobalVariable.currentGame.getPointsList().get(1).add(right_point);

        tv_right_point.setText(String.valueOf(right_point));
        previousServingTeam = currentServingTeam;
        currentServingTeam = 1;

        setScoreBoardPoint(getTeamPosition(BSBGlobalVariable.currentGame.getTeamList().get(1)), right_point);
        setGamePointDisplay(left_point,right_point, true);
        if(BSBGlobalVariable.gameNumber == 3){ //FINAL GAME, SWAP COURT
            if(leftTeampoint < 11 && rightTeampoint == 11){
                swapTeamDuring3rdSet();
                if(previousServingTeam == 0){
                    previousServingTeam = 1;
                }
                else{
                    previousServingTeam = 0;
                }
                currentServingTeam = 0;

                swapPointListDuringThirdGame();
                tv_left_point.setText(String.valueOf(right_point));  // Swap point display in court
                tv_right_point.setText(String.valueOf(left_point));
            }
        }
        currentServingTeamList.add(++serviceCounter, currentServingTeam);
        setServingIndicator();
        swapTeamPlayersDuringServeContinue(currentServingTeam);
        checkWiningGamePoint();
    }

    private String getTeamPosition(Team team) {
        String teamposition = "";
        teamposition = BSBGlobalVariable.scoreBoardTeamMap.get(team);
        return teamposition;
    }


    private void checkWiningGamePoint() {
        int left_point = BSBGlobalVariable.currentGame.getPointsList().get(0).
                get(BSBGlobalVariable.currentGame.getPointsList().get(0).size()-1);
        int right_point = BSBGlobalVariable.currentGame.getPointsList().get(1).
                get(BSBGlobalVariable.currentGame.getPointsList().get(1).size()-1);

        if(left_point == 30 && right_point == 29){
            isGameOver = true;
            Team team = new Team();
            team = BSBGlobalVariable.currentGame.getTeamList().get(0);
            BSBGlobalVariable.currentMatch.getGameWinnerTeam().add(getTeamPosition(team));  // Left Team winner of the Game
            BSBGlobalVariable.currentMatch.getGameList().add(BSBGlobalVariable.currentGame);
            displayGameEndAlert(left_point, right_point, 0);
        }else if(left_point >= 21 && left_point - right_point >= 2){
            isGameOver = true;
            Team team = new Team();
            team = BSBGlobalVariable.currentGame.getTeamList().get(0);
            BSBGlobalVariable.currentMatch.getGameWinnerTeam().add(getTeamPosition(team));  // Left Team winner of the Game
            BSBGlobalVariable.currentMatch.getGameList().add(BSBGlobalVariable.currentGame);
            displayGameEndAlert(left_point, right_point, 0);
        }else if(right_point == 30 && left_point == 29){
            isGameOver = true;
            Team team = new Team();
            team = BSBGlobalVariable.currentGame.getTeamList().get(1);
            BSBGlobalVariable.currentMatch.getGameWinnerTeam().add(getTeamPosition(team));  // Right Team winner of the Game
            BSBGlobalVariable.currentMatch.getGameList().add(BSBGlobalVariable.currentGame);
            displayGameEndAlert(right_point, left_point, 1);
        }else if(right_point >= 21 && right_point - left_point >= 2){
            isGameOver = true;
            Team team = new Team();
            team = BSBGlobalVariable.currentGame.getTeamList().get(1);
            BSBGlobalVariable.currentMatch.getGameWinnerTeam().add(getTeamPosition(team));  // Right Team winner of the Game
            BSBGlobalVariable.currentMatch.getGameList().add(BSBGlobalVariable.currentGame);
            displayGameEndAlert(right_point, left_point, 1);
        }
    }



    private void swapPointListDuringThirdGame() {
        Collections.swap(BSBGlobalVariable.currentGame.getPointsList(), 0, 1);
    }

    private void undoPreviousChange() {
        //Delete last entry from PointList
        if(serviceCounter >=0){
            if(serviceCounter >= 0 ){
                previousServingTeam = currentServingTeamList.get(serviceCounter);
            }else{
                previousServingTeam = 0;
            }
            if(BSBGlobalVariable.gameNumber == 3){
                int previous_left_point = BSBGlobalVariable.currentGame.getPointsList().get(0).
                        get(BSBGlobalVariable.currentGame.getPointsList().get(0).size()-1);
                int previous_right_point = BSBGlobalVariable.currentGame.getPointsList().get(1).
                        get(BSBGlobalVariable.currentGame.getPointsList().get(1).size()-1);
                if(previous_left_point == 11 && previous_right_point < 11 && previousServingTeam == 0) {
                    swapTeamDuring3rdSet();

                    if(previousServingTeam == 0){
                        previousServingTeam = 1;
                    }
                    else{
                        previousServingTeam = 0;
                    }
                    currentServingTeam = 1;

                    swapPointListDuringThirdGame();
                    tv_left_point.setText(String.valueOf(previous_right_point));  // Swap point display in court
                    tv_right_point.setText(String.valueOf(previous_left_point));
                }else if(previous_left_point < 11 && previous_right_point == 11 && previousServingTeam == 1) {
                    swapTeamDuring3rdSet();

                    if(previousServingTeam == 1){
                        previousServingTeam = 0;
                    }
                    else{
                        previousServingTeam = 1;
                    }
                    currentServingTeam = 0;
                    swapPointListDuringThirdGame();
                    tv_left_point.setText(String.valueOf(previous_right_point));  // Swap point display in court
                    tv_right_point.setText(String.valueOf(previous_left_point));
                }
            }

            BSBGlobalVariable.currentGame.getPointsList().get(0).
                    remove(BSBGlobalVariable.currentGame.getPointsList().get(0).size()-1);
            BSBGlobalVariable.currentGame.getPointsList().get(1).
                    remove(BSBGlobalVariable.currentGame.getPointsList().get(1).size()-1);

            int left_point = BSBGlobalVariable.currentGame.getPointsList().get(0).
                    get(BSBGlobalVariable.currentGame.getPointsList().get(0).size()-1);
            int right_point = BSBGlobalVariable.currentGame.getPointsList().get(1).
                    get(BSBGlobalVariable.currentGame.getPointsList().get(1).size()-1);


            leftTeampoint = left_point;
            rightTeampoint = right_point;
            currentServingTeam = currentServingTeamList.get(--serviceCounter);

            setScoreBoardPoint(getTeamPosition(BSBGlobalVariable.currentGame.getTeamList().get(1)), right_point);
            setScoreBoardPoint(getTeamPosition(BSBGlobalVariable.currentGame.getTeamList().get(0)), left_point);

            if((left_point == 0 && right_point != 0 && currentServingTeam == 0) ||
                    (right_point == 0 && left_point != 0 && currentServingTeam == 1)){  // During undo points, when one side reach to '0', then swap side.
                previousServingTeam = currentServingTeam;
                if(currentServingTeam == 0){
                    currentServingTeam = 1;
                }else if(currentServingTeam == 1){
                    currentServingTeam = 0;
                }
            }
            setGamePointDisplay(left_point, right_point, false);
            swapTeamPlayersDuringServeContinue(currentServingTeam);
            setServingIndicator();
            tv_left_point.setText(String.valueOf(left_point));
            tv_right_point.setText(String.valueOf(right_point));
            isGameOver = false;
        }
        else{
            Toast.makeText(GameActivity.this, "Can't Undo", Toast.LENGTH_SHORT).show();
        }
    }

    private void setGamePointDisplay(int left_point, int right_point, boolean shouldSpeakout) {
        String game_point = "";
        if(currentServingTeam != previousServingTeam){
            game_point = "Service Change ";
        }
        if(left_point == 0 && right_point == 0){
            game_point = "Love All";
        }else if(left_point == right_point){
            game_point = game_point + String.valueOf(right_point) + " All";
            if(left_point == 29 && right_point == 29){
                game_point = game_point + " . Game point ";
            }
        }else{
            if(currentServingTeam == 0){
                if(left_point == 0 && right_point > 0){
                    game_point = game_point + " Love, " + String.valueOf(right_point);
                }else if(left_point > 0 && right_point == 0){
                    game_point = game_point + String.valueOf(left_point) + ", Love";
                }else{
                    game_point = game_point + String.valueOf(left_point) + " , " + String.valueOf(right_point);
                }

                if(left_point == 30 && right_point == 29){
                    game_point = game_point + " . Game Over ";
                    game_point = game_point.replace("Service Change ", "");
                }else if(left_point >= 21 && left_point - right_point >= 2){
                    game_point = game_point + " . Game Over ";
                    game_point = game_point.replace("Service Change ", "");
                }else if(left_point >= 20 && left_point - right_point >= 1){
                    game_point = game_point + " . Game point ";
                }
            }
            else if(currentServingTeam == 1){
                if(left_point == 0 && right_point > 0){
                    game_point = game_point + String.valueOf(right_point) + ", Love";
                }else if(left_point > 0 && right_point == 0){
                    game_point = game_point + "Love , " + String.valueOf(left_point);
                }else{
                    game_point = game_point + String.valueOf(right_point)+ " , " +String.valueOf(left_point);
                }

                if(right_point == 30 && left_point == 29){
                    game_point = game_point + " . Game Over ";
                    game_point = game_point.replace("Service Change ", "");
                }else if(right_point >= 21 && right_point - left_point >= 2){
                    game_point = game_point + " . Game Over ";
                    game_point = game_point.replace("Service Change ", "");
                }else if(right_point >= 20 && right_point - left_point >= 1){
                    game_point = game_point + " . Game point ";
                }
            }
        }
        tv_game_point.setText(game_point);

        currentPointToAnnounce = game_point;
        if(shouldSpeakout){
            speakOut(game_point);
        }
    }

    private void setScoreBoardPoint(String team, int point) {
        if(team == "L"){
            switch (BSBGlobalVariable.gameNumber-1){
                case 0:
                    tv_game_one_team_one.setText(String.valueOf(point));
                    break;
                case 1:
                    tv_game_two_team_one.setText(String.valueOf(point));
                    break;
                case 2:
                    tv_game_three_team_one.setText(String.valueOf(point));
                    break;
                default:
                    break;
            }
        }
        if(team == "R"){
            switch (BSBGlobalVariable.gameNumber-1){
                case 0:
                    tv_game_one_team_two.setText(String.valueOf(point));
                    break;
                case 1:
                    tv_game_two_team_two.setText(String.valueOf(point));
                    break;
                case 2:
                    tv_game_three_team_two.setText(String.valueOf(point));
                    break;
                default:
                    break;
            }
        }
    }

    private void swapTeamPlayersDuringServeContinue(int servingTeam) {
        if(previousServingTeam == currentServingTeam){  // Swap Players
            Team team = BSBGlobalVariable.currentGame.getTeamList().get(servingTeam);
            if(!team.equals(null)) {
                String temp_player = team.getLeftPlayer();
                team.setLeftPlayer(team.getRightPlayer());
                team.setRightPlayer(temp_player);
                if (servingTeam == 0) {
                    et_left_court_left_player.setText(team.getLeftPlayer());
                    et_left_court_right_player.setText(team.getRightPlayer());
                } else if (servingTeam == 1) {
                    et_right_court_left_player.setText(team.getLeftPlayer());
                    et_right_court_right_player.setText(team.getRightPlayer());
                }
                BSBGlobalVariable.currentGame.getTeamList().get(servingTeam).setLeftPlayer(team.getLeftPlayer());
                BSBGlobalVariable.currentGame.getTeamList().get(servingTeam).setRightPlayer(team.getRightPlayer());
            }
        }
    }

    public void displayGameEndAlert(int winner_point,int runner_point, int wining_team){
        Game mGame = BSBGlobalVariable.currentGame;
        String message = "Game " + String.valueOf(mGame.getGame_number()) + " won by ";
        message = message + mGame.getTeamList().get(wining_team).getLeftPlayer() + " / " +
                mGame.getTeamList().get(wining_team).getRightPlayer();
        message = message + " (" + winner_point + " - " +runner_point + ")";

        if(BSBGlobalVariable.gameNumber == 3){
            message = message + "\n" + "Match Won By ";
            message = message + mGame.getTeamList().get(wining_team).getLeftPlayer() + " / " +
                    mGame.getTeamList().get(wining_team).getRightPlayer();
        }

        //this.announceWinner(message);

        BSBGlobalVariable.currentMatch.setWinnerTeam(mGame.getTeamList().get(wining_team).getLeftPlayer() + " / " +
                mGame.getTeamList().get(wining_team).getRightPlayer());
        if(BSBGlobalVariable.gameNumber == 1){
            BSBGlobalVariable.currentMatch.getPoints()[BSBGlobalVariable.gameNumber-1][0] = tv_game_one_team_one.getText().toString();
            BSBGlobalVariable.currentMatch.getPoints()[BSBGlobalVariable.gameNumber-1][1] = tv_game_one_team_two.getText().toString();;
        }else if(BSBGlobalVariable.gameNumber == 2){
            BSBGlobalVariable.currentMatch.getPoints()[BSBGlobalVariable.gameNumber-1][0] = tv_game_two_team_one.getText().toString();
            BSBGlobalVariable.currentMatch.getPoints()[BSBGlobalVariable.gameNumber-1][1] = tv_game_two_team_two.getText().toString();;
        }
        else if(BSBGlobalVariable.gameNumber == 3){
            BSBGlobalVariable.currentMatch.getPoints()[BSBGlobalVariable.gameNumber-1][0] = tv_game_three_team_one.getText().toString();
            BSBGlobalVariable.currentMatch.getPoints()[BSBGlobalVariable.gameNumber-1][1] = tv_game_three_team_two.getText().toString();;
        }

        long elapsedMillis = (int) (SystemClock.elapsedRealtime() - cm_timer.getBase());
        BSBGlobalVariable.currentMatch.setMatchDuration(elapsedMillis);
        //BSBGlobalVariable.currentMatch.getGameWinnerTeam().add(wining_team);
        final Dialog dialog = new Dialog(GameActivity.this);
        dialog.setContentView(R.layout.alert_layout);
        dialog.setTitle("Game Complete");
        ((TextView) dialog.findViewById(R.id.dialog_info)).setText(message);
        if((BSBGlobalVariable.gameNumber == 1)/*// 1-0 / 0-1*/||
                (BSBGlobalVariable.gameNumber == 2 && (Collections.frequency(BSBGlobalVariable.currentMatch.getGameWinnerTeam(), "L") ==
                        Collections.frequency(BSBGlobalVariable.currentMatch.getGameWinnerTeam(), "R"))) /* 1-1*/){
            ((Button) dialog.findViewById(R.id.dialog_finish)).setVisibility(View.GONE);
            ((Button) dialog.findViewById(R.id.dialog_continue)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    BSBGlobalVariable.gameNumber++;
                    setUpNewGame();
                    resetData();
                }
            });
        }
        else{ //MATCH END 3RD SET
            ((Button) dialog.findViewById(R.id.dialog_continue)).setVisibility(View.GONE);
            Toast.makeText(GameActivity.this,"Game Over", Toast.LENGTH_SHORT).show();
            ((Button) dialog.findViewById(R.id.dialog_finish)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Match match = BSBGlobalVariable.currentMatch;
                    dbHelper.insertContact(match);
                    resetGlobalData();
                    Intent intent = new Intent(GameActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        }
        dialog.show();
    }

    private void resetGlobalData() {
        BSBGlobalVariable.currentGame = new Game();
        BSBGlobalVariable.gameNumber = 0;
        BSBGlobalVariable.currentMatch = new Match();
        BSBGlobalVariable.scoreBoardTeamMap = new LinkedHashMap<>();
    }

    private void setUpNewGame() {
        team1 = new Team();
        team2 = new Team();
        team1 = BSBGlobalVariable.currentMatch.getGameList().get(BSBGlobalVariable.currentMatch.getGameList().size()-1).getTeamList().get(0);
        team2 = BSBGlobalVariable.currentMatch.getGameList().get(BSBGlobalVariable.currentMatch.getGameList().size()-1).getTeamList().get(1);
        swapTeams();
        //ib_center_reverse.setVisibility(View.VISIBLE);
        ib_left_reverse.setVisibility(View.VISIBLE);
        ib_right_reverse.setVisibility(View.VISIBLE);
        ib_right_reverse.setOnClickListener(this);
        ib_left_reverse.setOnClickListener(this);
        ib_start.setOnClickListener(this);

        Game game = new Game();
        List<Team> teamList = new ArrayList<>();

        teamList.add(team1);
        teamList.add(team2);
        List<List<Integer>> pointsList = new ArrayList<List<Integer>>();
        List<Integer> list0 = new ArrayList<>();
        list0.add(0);
        List<Integer> list1 = new ArrayList<>();
        list1.add(0);
        pointsList.add(list0);
        pointsList.add(list1);
        game.setServingTeam(currentServingTeam);
        game.setTeamList(teamList);
        game.setGame_number(BSBGlobalVariable.gameNumber);
        game.setPointsList(pointsList);
        BSBGlobalVariable.currentGame = game;
    }

    private void resetData() {
        isGameOver = false;
        isSetupReady = false;
        if(currentServingTeam == 0){
            currentServingTeam = 1;
            previousServingTeam =1;
        }else if(currentServingTeam == 1){
            currentServingTeam = 0;
            previousServingTeam = 0;
        }

        previousServingSideLayoutId = 0;
        serviceCounter = -1;
        leftTeampoint = 0;
        rightTeampoint = 0;
        /*if(BSBGlobalVariable.gameNumber < 3){
            BSBGlobalVariable.gameNumber++;
        }*/
        //setPlayerName();
        setGamePointDisplay(leftTeampoint,rightTeampoint, false);
        setScoreBoardPoint("L", leftTeampoint);
        setScoreBoardPoint("R", rightTeampoint);
        //---------------------------------------- RESET INDICATOR COLOR ------------------------
        left_even_court.setBackgroundColor(ContextCompat.getColor(this, R.color.courtColor));
        left_odd_court.setBackgroundColor(ContextCompat.getColor(this, R.color.courtColor));
        right_even_court.setBackgroundColor(ContextCompat.getColor(this, R.color.courtColor));
        right_odd_court.setBackgroundColor(ContextCompat.getColor(this, R.color.courtColor));
        if(BSBGlobalVariable.gameNumber == 2){
            tv_game_one_team_one.setBackground(ContextCompat.getDrawable(this, R.drawable.textview_boarder));
            tv_game_one_team_two.setBackground(ContextCompat.getDrawable(this, R.drawable.textview_boarder));
            tv_game_two_team_one.setBackground(ContextCompat.getDrawable(this, R.drawable.textview_boarder_highlighted));
            tv_game_two_team_two.setBackground(ContextCompat.getDrawable(this, R.drawable.textview_boarder_highlighted));
        }
        if(BSBGlobalVariable.gameNumber == 3){
            tv_game_two_team_two.setBackground(ContextCompat.getDrawable(this, R.drawable.textview_boarder));
            tv_game_two_team_one.setBackground(ContextCompat.getDrawable(this, R.drawable.textview_boarder));
            tv_game_three_team_one.setBackground(ContextCompat.getDrawable(this, R.drawable.textview_boarder_highlighted));
            tv_game_three_team_two.setBackground(ContextCompat.getDrawable(this, R.drawable.textview_boarder_highlighted));
        }
        tv_left_point.setText(String.valueOf(leftTeampoint));
        tv_right_point.setText(String.valueOf(rightTeampoint));
        //----------------------------------------
        setServingIndicator();
    }

    private void swapLeftTeamPlayers() {
        if(!team1.equals(null)){
            String temp_player = team1.getLeftPlayer();
            team1.setLeftPlayer(team1.getRightPlayer());
            team1.setRightPlayer(temp_player);

            et_left_court_left_player.setText(team1.getLeftPlayer());
            et_left_court_right_player.setText(team1.getRightPlayer());
        }
        else{
            Toast.makeText(GameActivity.this, "Please enter player details", Toast.LENGTH_SHORT).show();
        }
    }

    private void swapRightTeamPlayers() {
        if(!team2.equals(null)){
            String temp_player = team2.getLeftPlayer();
            team2.setLeftPlayer(team2.getRightPlayer());
            team2.setRightPlayer(temp_player);

            et_right_court_left_player.setText(team2.getLeftPlayer());
            et_right_court_right_player.setText(team2.getRightPlayer());
        }
        else{
            Toast.makeText(GameActivity.this, "Please enter player details", Toast.LENGTH_SHORT).show();
        }
    }
    private void swapTeams() {
        Team temp_team = new Team();
        if(!team1.equals(null) && !team2.equals(null)){
            temp_team = team1;
            team1 = team2;
            team2 = temp_team;
            BSBGlobalVariable.currentGame.getTeamList().set(0,team1);
            BSBGlobalVariable.currentGame.getTeamList().set(1,team2);
            et_left_court_left_player.setText(team1.getLeftPlayer());
            et_left_court_right_player.setText(team1.getRightPlayer());
            et_right_court_left_player.setText(team2.getLeftPlayer());
            et_right_court_right_player.setText(team2.getRightPlayer());
        }
        else{
            Toast.makeText(GameActivity.this, "Please enter player details", Toast.LENGTH_SHORT).show();
        }
    }

    private void swapTeamDuring3rdSet() {

        Team temp_team = new Team();
        Team team1 = BSBGlobalVariable.currentGame.getTeamList().get(0);
        Team team2 = BSBGlobalVariable.currentGame.getTeamList().get(1);
        if(!team1.equals(null) && !team2.equals(null)){
            temp_team = team1;
            team1 = team2;
            team2 = temp_team;
            BSBGlobalVariable.currentGame.getTeamList().set(0,team1);
            BSBGlobalVariable.currentGame.getTeamList().set(1,team2);
            et_left_court_left_player.setText(team1.getLeftPlayer());
            et_left_court_right_player.setText(team1.getRightPlayer());
            et_right_court_left_player.setText(team2.getLeftPlayer());
            et_right_court_right_player.setText(team2.getRightPlayer());
        }
        else{
            Toast.makeText(GameActivity.this, "Please enter player details", Toast.LENGTH_SHORT).show();
        }
        this.displayCourtSwapAlert();
    }

    public void displayCourtSwapAlert(){
        String message = "Please Change the court";
        final Dialog dialog = new Dialog(GameActivity.this); // Context, this, etc.
        dialog.setContentView(R.layout.alert_layout);
        dialog.setTitle("Court Swap Alert");
        ((TextView) dialog.findViewById(R.id.dialog_info)).setText(message);
        ((Button) dialog.findViewById(R.id.dialog_finish)).setVisibility(View.GONE);
        ((Button) dialog.findViewById(R.id.dialog_continue)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String msg = "Please Change the court.";
                GameActivity.this.announceCourtSwap(msg);
            }
        });

        dialog.show();
    }

    public void displayGameStartAlert(){
        Game mGame = BSBGlobalVariable.currentGame;
        String message = "Game " + String.valueOf(mGame.getGame_number()) + " will start " + "\n" +
                "Left  :- " + mGame.getTeamList().get(0).getLeftPlayer() + " / " + mGame.getTeamList().get(0).getRightPlayer() + "\n" +
                "Right :- " + mGame.getTeamList().get(1).getLeftPlayer() + " / " + mGame.getTeamList().get(1).getRightPlayer() + "\n\n" +
                " Wish To Continue ?";

        final Dialog dialog = new Dialog(GameActivity.this); // Context, this, etc.
        dialog.setContentView(R.layout.alert_layout);
        dialog.setTitle("New Game Start");
        ((TextView) dialog.findViewById(R.id.dialog_info)).setText(message);

        ((Button) dialog.findViewById(R.id.dialog_continue)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isSetupReady = true;
                ib_left_reverse.setVisibility(View.GONE);
                ib_right_reverse.setVisibility(View.GONE);
            }
        });
        ((Button) dialog.findViewById(R.id.dialog_finish)).setText("Cancel");
        ((Button) dialog.findViewById(R.id.dialog_finish)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void displayNextTeam() {
        if(BSBGlobalVariable.nextMatchTeam.size() > 0){

            nextMatchTeams = BSBGlobalVariable.nextMatchTeam.get(0).getLeftPlayer() + "/" +
                    BSBGlobalVariable.nextMatchTeam.get(0).getRightPlayer() + " VS " +
                    BSBGlobalVariable.nextMatchTeam.get(1).getLeftPlayer() +  "/" +
                    BSBGlobalVariable.nextMatchTeam.get(1).getRightPlayer();
            tv_next_match.setText(nextMatchTeams);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    private void speakOut(String text) {
        if(isSoundActive){
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.UK);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported");
            } else {
                //btnSpeak.setEnabled(true);
                //speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed");
        }
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
    }
}
