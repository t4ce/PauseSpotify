package epx.pub.pausespotify;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import com.spotify.android.appremote.api.*;

public class MainActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "9f1f5bb5f92a43ac804d7887c9dad3fd";
    private static final String REDIRECT_URI = "testschema://callback";

    private static final ConnectionParams connectionParams =
            new ConnectionParams.Builder(CLIENT_ID)
                    .setRedirectUri(REDIRECT_URI)
                    .showAuthView(true)
                    .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final NumberPicker np = findViewById(R.id.np);
        final TextView tf = findViewById(R.id.tf);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Snackbar.make(view, "started " + np.getValue() + " min timer", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            new CountDownTimer(1000 * 60 * np.getValue(), 1000) {
                public void onTick(long millisUntilFinished) {
                    long sec = millisUntilFinished / 1000;
                    long min = sec / 60;
                    tf.setText(String.format(getString(R.string.TimeRemaining), min, sec % 60));
                }
                public void onFinish() {
                    tf.setText(R.string.Finished);
                    sendPause();
                }
            }.start();
            }
        });
        np.setMinValue(0);
        np.setMaxValue(60);
        np.setWrapSelectorWheel(true);
        checkConnect();
    }

    void checkConnect() {
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                    }
                    public void onFailure(Throwable throwable) {
                    }
                });
    }

    private void sendPause() {
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        spotifyAppRemote.getPlayerApi().pause();
                    }
                    public void onFailure(Throwable throwable) {
                    }
                });
    }
}