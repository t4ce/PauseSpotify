package com.example.pausespotify;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.TextView;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "9f1f5bb5f92a43ac804d7887c9dad3fd";
    private static final String REDIRECT_URI = "testschema://callback";

    CountDownTimer cdt;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Resources res = getResources();
        Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.background, null);

        final NumberPicker np = findViewById(R.id.np);
        final TextView tf = findViewById(R.id.tf);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "started " + np.getValue() + " min timer", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                if (cdt != null){
                    cdt.cancel();
                    tf.setText("");
                }

                CountDownTimer cdt = new CountDownTimer(1000 * 60 * np.getValue(), 1000) {
                    public void onTick(long millisUntilFinished) {
                        long sec = millisUntilFinished / 1000;
                        long min = sec / 60;
                        tf.setText(String.format(Locale.GERMAN,"%s %02d:%02d", getString(R.string.TimeRemaining), min, sec % 60));
                    }
                    public void onFinish() {
                        try {
                            tf.setText(getString(R.string.Finished));
                            sendPause();
                        } catch (Exception e){
                            System.out.println();
                        }
                    }
                }.start();

                setCDT(cdt);
            }
        });
        // number picker min/max
        np.setMinValue(0);
        np.setMaxValue(60);
        //after end continue to scroll from beginning
        np.setWrapSelectorWheel(true);
    }

    void setCDT(CountDownTimer cdt){
        this.cdt = cdt;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void sendPause() {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        spotifyAppRemote.getPlayerApi().pause();
                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("pause spotify", throwable.getMessage(), throwable);
                    }
                });
    }
}