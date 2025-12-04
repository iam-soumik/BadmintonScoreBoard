package com.scoreboard.badminton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.scoreboard.badminton.Model.Match;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{

    Button bt_new_game;
    ListView list;
    ArrayList<Match> matchList;
    DBHelper dbHelper;
    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        bt_new_game = (Button)findViewById(R.id.bt_new_game);
        list = (ListView) findViewById(R.id.list);
        bt_new_game.setOnClickListener(this);
        dbHelper = new DBHelper(DashboardActivity.this);

        matchList = dbHelper.getAllMatches();
        if(matchList.size() > 0){
            adapter= new CustomAdapter(matchList,getApplicationContext());
            list.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == bt_new_game){
            Intent intent = new Intent(DashboardActivity.this, PreMatchStartActivity.class);
            startActivity(intent);
        }
    }
}
