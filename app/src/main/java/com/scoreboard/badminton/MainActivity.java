package com.scoreboard.badminton;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    DBHelper dbHelper;
    Button bt_continue;
    EditText et_edition;
    String edition = "";
    private int tapCount = 0;
    private static final String PIN_HASH = "8899"; // later you can hash

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BuildConfig.RESTRICTED_BUILD) {
            // Tournament build → no restriction ever
            setContentView(R.layout.activity_main);
            initUI();
            return;
        }

        // Restricted build only
        if (isUnlocked()) {
            setContentView(R.layout.activity_main);
            initUI();
        } else {
            checkExpiryUsingServerTime();
        }
    }


    @Override
    public void onClick(View view) {
        if(view == bt_continue){
            edition = et_edition.getText().toString();
            if(edition.equalsIgnoreCase("")){
                Toast.makeText(getApplicationContext(),"Please enter Tournament edition", Toast.LENGTH_LONG).show();
            }
            else{
                dbHelper.insertTournamentEdition(edition);
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        }

    }

    private void checkExpiryUsingServerTime() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            boolean expired = false;

            try {
                URL url = new URL("https://www.google.com");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.connect();

                long serverTime = conn.getDate(); // server time in millis

                Calendar expiry = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                expiry.set(2026, Calendar.APRIL, 9, 23, 59, 59);

                expired = serverTime > expiry.getTimeInMillis();

            } catch (Exception e) {
                // fallback to device time
                Calendar expiry = Calendar.getInstance();
                expiry.set(2026, Calendar.APRIL, 9, 23, 59, 59);
                expired = Calendar.getInstance().after(expiry);
            }

            boolean finalExpired = expired;
            handler.post(() -> {
                // Always load UI so unlock is possible
                setContentView(R.layout.activity_main);
                initUI();

                if (finalExpired && !isUnlocked()) {
                    bt_continue.setEnabled(false);   // 🚫 BLOCK USER
                    bt_continue.setAlpha(0.5f);      // 👀 visually disabled
                    showExpiredDialog();             // 🔓 admin can still unlock
                }
            });
        });
    }

    private void showExpiredDialog() {
        new AlertDialog.Builder(this)
                .setTitle("App Expired")
                .setMessage(
                        "The app has expired. Need to update the app to continue."
                )
                .setCancelable(true) // ✅ allow dismiss
                .setPositiveButton("OK", (d, w) -> d.dismiss())
                .setNegativeButton("Exit", (d, w) -> finish())
                .show();
    }
    private void initUI() {

        dbHelper = new DBHelper(this);
        bt_continue = findViewById(R.id.bt_continue);
        et_edition = findViewById(R.id.et_edition);
        bt_continue.setOnClickListener(this);
        setupSecretUnlock(et_edition);
    }

    private boolean isUnlocked() {
        return getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getBoolean("unlocked", false);
    }
    private void setupSecretUnlock(View view) {
        view.setOnLongClickListener(v -> {
            tapCount++;
            if (tapCount >= 5) {
                showUnlockDialog();
                tapCount = 0;
            }
            return true;
        });
    }
    private void showUnlockDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter PIN");

        new AlertDialog.Builder(this)
                .setTitle("Admin Unlock")
                .setView(input)
                .setPositiveButton("Unlock", (d, w) -> {
                    if (PIN_HASH.equals(input.getText().toString())) {
                        getSharedPreferences("app_prefs", MODE_PRIVATE)
                                .edit()
                                .putBoolean("unlocked", true)
                                .apply();

                        Toast.makeText(this, "Unlocked", Toast.LENGTH_LONG).show();
                        bt_continue.setEnabled(true); // ✅ ENABLE
                        bt_continue.setAlpha(1f);
                        recreate();
                    } else {
                        Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

}