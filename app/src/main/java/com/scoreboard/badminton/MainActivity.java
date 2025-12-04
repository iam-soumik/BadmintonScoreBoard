package com.scoreboard.badminton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    DBHelper dbHelper;
    Button bt_continue;
    EditText et_edition;
    String edition = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(this);
        bt_continue = (Button)findViewById(R.id.bt_continue);
        et_edition = (EditText) findViewById(R.id.et_edition);

        bt_continue.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view == bt_continue){
            edition = et_edition.getText().toString();
            if(edition.equalsIgnoreCase("")){
                Toast.makeText(getApplicationContext(),"Please enter Tournament edition", Toast.LENGTH_LONG);
            }
            else{
                dbHelper.insertTournamentEdition(edition);
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        }

    }
}