package com.scoreboard.badminton;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.scoreboard.badminton.Model.Game;
import com.scoreboard.badminton.Model.Match;
import com.scoreboard.badminton.Model.Team;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by HOME on 12/26/2018.
 */

public class PreMatchStartActivity extends Activity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, TextWatcher, AdapterView.OnItemSelectedListener{

    ImageButton ib_left_reverse, ib_right_reverse, ib_center_reverse, ib_start, ib_next;
    EditText et_left_court_left_player, et_left_court_right_player, et_right_court_left_player, et_right_court_right_player/*, et_match_number*/;
    RadioGroup rg_serve;
    TextView tv_heading;
    RadioButton rb_left, rb_right;
    RelativeLayout left_even_court, right_even_court;
    Spinner sp_match_number;
    int servingTeam = 0, curYear = 0;
    String heading = "", year_offset = "", edition = "";
    Team team1, team2, temp_team;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_match_start);
        BSBGlobalVariable.nextMatchTeam = new ArrayList<>();
        dbHelper = new DBHelper(this);
        initializeViews();
        setUpHeading();
        setTeamDetails();
        initializeSpinnerData();
    }

    private void setUpHeading(){
        edition = dbHelper.getEdition();

        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int yr = 0;
        if(edition.equalsIgnoreCase("") || edition.equals(null)){
            yr = 10 + (curYear - 2021); // get edition of tournament.e.g 10th,11th etc. Taking 2021 as 10th year
        }
        else{
            yr = Integer.parseInt(edition);
        }
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

    private void initializeSpinnerData() {
        // Spinner Drop down elements
        List<String> match_names = new ArrayList<String>();
        match_names.add("Quater Final 1");
        match_names.add("Quater Final 2");
        match_names.add("Quater Final 3");
        match_names.add("Quater Final 4");
        match_names.add("Semi Final 1");
        match_names.add("Semi Final 2");
        match_names.add("Final");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, match_names);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout);

        // attaching data adapter to spinner
        sp_match_number.setAdapter(dataAdapter);

    }

    private void setTeamDetails() {
        if(BSBGlobalVariable.nextMatchTeam.size() > 0){
            et_left_court_left_player.setText(BSBGlobalVariable.nextMatchTeam.get(0).getLeftPlayer());
            et_left_court_right_player.setText(BSBGlobalVariable.nextMatchTeam.get(0).getRightPlayer());
            et_right_court_left_player.setText(BSBGlobalVariable.nextMatchTeam.get(1).getLeftPlayer());
            et_right_court_right_player.setText(BSBGlobalVariable.nextMatchTeam.get(1).getRightPlayer());
        }
    }

    private void initializeViews() {
        ib_center_reverse = (ImageButton) findViewById(R.id.ib_center_reverse);
        ib_left_reverse   = (ImageButton) findViewById(R.id.ib_left_reverse);
        ib_right_reverse  = (ImageButton) findViewById(R.id.ib_right_reverse);
        ib_start          = (ImageButton) findViewById(R.id.ib_start);
        ib_next           = (ImageButton) findViewById(R.id.ib_next);
        sp_match_number   = (Spinner) findViewById(R.id.spinner_match_number);

        tv_heading = (TextView) findViewById(R.id.textView);

        left_even_court     = (RelativeLayout) findViewById(R.id.left_even_court);
        right_even_court    = (RelativeLayout) findViewById(R.id.right_even_court);

        et_left_court_left_player   = (EditText) findViewById(R.id.et_left_court_left_player);
        et_left_court_right_player  = (EditText) findViewById(R.id.et_left_court_right_player);
        et_right_court_left_player  = (EditText) findViewById(R.id.et_right_court_left_player);
        et_right_court_right_player = (EditText) findViewById(R.id.et_right_court_right_player);
        //et_match_number  = (EditText) findViewById(R.id.et_match_number);

        rg_serve = (RadioGroup) findViewById(R.id.radio_serve);
        rb_left = (RadioButton) findViewById(R.id.radioLeft);
        rb_right = (RadioButton) findViewById(R.id.radioRight);

        left_even_court.setBackgroundColor(ContextCompat.getColor(this, R.color.pointViewHighlightedColor));

        team1 = new Team();
        team2 = new Team();

        ib_center_reverse.setOnClickListener(this);
        ib_right_reverse.setOnClickListener(this);
        ib_left_reverse.setOnClickListener(this);
        ib_start.setOnClickListener(this);
        ib_next.setOnClickListener(this);
        rg_serve.setOnCheckedChangeListener(this);
        sp_match_number.setOnItemSelectedListener(this);
        et_left_court_left_player.addTextChangedListener(this);
        et_left_court_right_player.addTextChangedListener(this);
        et_right_court_left_player.addTextChangedListener(this);
        et_right_court_right_player.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
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
        if(v == ib_center_reverse){
            team1.setLeftPlayer(et_left_court_left_player.getText().toString().trim());
            team1.setRightPlayer(et_left_court_right_player.getText().toString().trim());
            team2.setLeftPlayer(et_right_court_left_player.getText().toString().trim());
            team2.setRightPlayer(et_right_court_right_player.getText().toString().trim());
            swapTeams();
        }
        if(v == ib_start){
            if(!team1.equals(null) && !team2.equals(null)){
                /*String matchNum = et_match_number.getText().toString().trim();*/
                String matchNum = sp_match_number.getSelectedItem().toString().trim();
                if(!matchNum.equals(null) && !matchNum.equals("")){

                    Match currentMatch = new Match();
                    Game game = new Game();

                    List<Team> teamList = new ArrayList<>();
                    List<Game> gameList = new ArrayList<>();

                    teamList.add(team1);
                    teamList.add(team2);

                    List<List<Integer>> pointsList = new ArrayList<List<Integer>>();
                    List<Integer> list0 = new ArrayList<>();
                    list0.add(0);
                    List<Integer> list1 = new ArrayList<>();
                    list1.add(0);
                    pointsList.add(list0);
                    pointsList.add(list1);
                    game.setServingTeam(servingTeam);
                    game.setTeamList(teamList);
                    BSBGlobalVariable.gameNumber = 1;
                    game.setGame_number(BSBGlobalVariable.gameNumber);
                    game.setPointsList(pointsList);
                    BSBGlobalVariable.currentGame = game;
                    /*gameList.add(game);*/

                    currentMatch.setGameList(gameList);
                    currentMatch.setTeamList(teamList);
                    /*currentMatch.setMatchID(et_match_number.getText().toString().trim());*/
                    currentMatch.setMatchID(sp_match_number.getSelectedItem().toString().trim());
                    //-------------------------------
                    String[][] points = new String[3][2];
                    for (int i = 0; i < points.length; i++) {   // initialize 0
                        for (int j = 0; j < points[i].length; j++) {
                            points[i][j] = "0";
                        }
                    }
                    currentMatch.setPoints(points);
                    //--------------------------------
                    currentMatch.setMatchDuration((long) 0);
                    List<String> gameWinnerTeam = new ArrayList<>();
                    currentMatch.setGameWinnerTeam(gameWinnerTeam);
                    BSBGlobalVariable.currentMatch = currentMatch;

                    BSBGlobalVariable.scoreBoardTeamMap = new LinkedHashMap<>();
                    BSBGlobalVariable.scoreBoardTeamMap.put(team1, "L");
                    BSBGlobalVariable.scoreBoardTeamMap.put(team2, "R");

                    Intent intent = new Intent(PreMatchStartActivity.this, GameActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(PreMatchStartActivity.this, "Enter match Number", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(PreMatchStartActivity.this, "Please enter player details", Toast.LENGTH_SHORT).show();
            }
        }
        if(v == ib_next){
            setNextMatchTeam();
        }
    }

    private void setNextMatchTeam() {
        final Dialog dialog = new Dialog(PreMatchStartActivity.this); // Context, this, etc.
        dialog.setContentView(R.layout.next_match_layout);
        dialog.setTitle("Next Match Teams");

        final EditText et_first_team_player1 = (EditText) dialog.findViewById(R.id.et_first_team_player1);
        final EditText et_first_team_player2 = (EditText) dialog.findViewById(R.id.et_first_team_player2);
        final EditText et_second_team_player1 = (EditText) dialog.findViewById(R.id.et_second_team_player1);
        final EditText et_second_team_player2 = (EditText) dialog.findViewById(R.id.et_second_team_player2);

        if(BSBGlobalVariable.nextMatchTeam.size() > 0){
            et_first_team_player1.setText(BSBGlobalVariable.nextMatchTeam.get(0).getLeftPlayer());
            et_first_team_player2.setText(BSBGlobalVariable.nextMatchTeam.get(0).getRightPlayer());
            et_second_team_player1.setText(BSBGlobalVariable.nextMatchTeam.get(1).getLeftPlayer());
            et_second_team_player2.setText(BSBGlobalVariable.nextMatchTeam.get(1).getRightPlayer());
        }
        BSBGlobalVariable.nextMatchTeam = new ArrayList<>();

        ((Button) dialog.findViewById(R.id.bt_clear)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            et_first_team_player1.setText("");
            et_first_team_player2.setText("");
            et_second_team_player1.setText("");
            et_second_team_player2.setText("");
            BSBGlobalVariable.nextMatchTeam = new ArrayList<>();
            }
        });
        ((Button) dialog.findViewById(R.id.bt_done)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String first_team_player1 = et_first_team_player1.getText().toString().trim();
            String first_team_player2 = et_first_team_player2.getText().toString().trim();
            String second_team_player1 = et_second_team_player1.getText().toString().trim();
            String second_team_player2 = et_second_team_player2.getText().toString().trim();

            if(!second_team_player2.equals("") && !first_team_player1.equals("") &&
                    !first_team_player2.equals("") && !second_team_player1.equals("")){
                Team team1 = new Team();
                team1.setLeftPlayer(first_team_player1);
                team1.setRightPlayer(first_team_player2);
                Team team2 = new Team();
                team2.setLeftPlayer(second_team_player1);
                team2.setRightPlayer(second_team_player2);
                BSBGlobalVariable.nextMatchTeam.add(team1);
                BSBGlobalVariable.nextMatchTeam.add(team2);
                dialog.dismiss();
            }else{
                Toast.makeText(PreMatchStartActivity.this, "Enter Team Details", Toast.LENGTH_SHORT).show();
            }


            }
        });
        dialog.show();
    }

    private void swapTeams() {
        if(!team1.equals(null) && !team2.equals(null)){
            temp_team = team1;
            team1 = team2;
            team2 = temp_team;

            et_left_court_left_player.setText(team1.getLeftPlayer());
            et_left_court_right_player.setText(team1.getRightPlayer());
            et_right_court_left_player.setText(team2.getLeftPlayer());
            et_right_court_right_player.setText(team2.getRightPlayer());
        }
        else{
            Toast.makeText(PreMatchStartActivity.this, "Please enter player details", Toast.LENGTH_SHORT).show();
        }
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
            Toast.makeText(PreMatchStartActivity.this, "Please enter player details", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(PreMatchStartActivity.this, "Please enter player details", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(group == rg_serve){
            switch (checkedId){
                case R.id.radioLeft:
                    servingTeam = 0;
                    left_even_court.setBackgroundColor(ContextCompat.getColor(this, R.color.pointViewHighlightedColor));
                    right_even_court.setBackgroundColor(ContextCompat.getColor(this, R.color.courtColor));
                    break;
                case R.id.radioRight:
                    servingTeam = 1;
                    left_even_court.setBackgroundColor(ContextCompat.getColor(this, R.color.courtColor));
                    right_even_court.setBackgroundColor(ContextCompat.getColor(this, R.color.pointViewHighlightedColor));
                    break;
                default:
                    //servingTeam = 0;
                    break;
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.hashCode() == et_left_court_left_player.getText().hashCode()){
            team1.setLeftPlayer(et_left_court_left_player.getText().toString().trim());
        }
        if(s.hashCode() == et_left_court_right_player.getText().hashCode()){
            team1.setRightPlayer(et_left_court_right_player.getText().toString().trim());
        }
        if(s.hashCode() == et_right_court_right_player.getText().hashCode()){
            team2.setRightPlayer(et_right_court_right_player.getText().toString().trim());
        }
        if(s.hashCode() == et_right_court_left_player.getText().hashCode()){
            team2.setLeftPlayer(et_right_court_left_player.getText().toString().trim());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
