package service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.example.oldmarket4.R;

// service is an andriod class which is used to play music, we didnt built it
public class MusicService extends Service {

    private static final String TAG = "MusicService";
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize media player
        mediaPlayer = MediaPlayer.create(this, R.raw.kvish_ha_hof); // Ensure you have a sample_music.mp3 file in res/raw
        mediaPlayer.setLooping(true); // Set looping
        Log.d(TAG, "MusicService created");
    }

    // to start music
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start playback
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Log.d(TAG, "Music playback started");
        }
        return START_STICKY;
    }

    // when we want to stop the music
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop playback and release resources
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d(TAG, "Music playback stopped");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // We don't provide binding, so return null
    }
}
