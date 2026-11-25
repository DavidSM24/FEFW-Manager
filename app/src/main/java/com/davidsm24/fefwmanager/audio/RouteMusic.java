package com.davidsm24.fefwmanager.audio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.util.EnumMap;
import java.util.EnumSet;

public final class RouteMusic {
    private static final String TAG = "RouteMusic";
    private static final long FRAME_INTERVAL_MILLIS = 16;
    private static final long LOAD_TIMEOUT_MILLIS = 10_000;

    private enum State { EMPTY, PREPARING, READY, PLAYING, SEEKING }

    private final Context app;
    private final AudioManager audio;
    private final Handler playback;
    private final AudioAttributes attributes = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build();

    // MediaPlayer callbacks and all playback state run on the same handler.
    private final EnumMap<MusicTrack, MediaPlayer> players = new EnumMap<>(MusicTrack.class);
    private final EnumSet<MusicTrack> awaiting = EnumSet.noneOf(MusicTrack.class);
    private final MusicCrossfade crossfade = new MusicCrossfade();
    private final Runnable fadeFrame = this::advanceFade;
    private final Runnable loadTimeout = () -> fail("Timed out preparing or seeking the soundtrack", null);
    private State state = State.EMPTY;
    private MusicTrack selectedTrack = MusicTrack.MAIN_SCREEN;
    private int visibleScreens;
    private int cycleDuration;
    private int savedPosition;
    private boolean needsSeek;
    private boolean focusAvailable;
    private AudioManager.OnAudioFocusChangeListener focusListener;
    private AudioFocusRequest focusRequest;

    public RouteMusic(Context context) {
        app = context.getApplicationContext();
        audio = app.getSystemService(AudioManager.class);
        HandlerThread thread = new HandlerThread("route-music");
        thread.start();
        playback = new Handler(thread.getLooper());
    }

    public void screenStarted() {
        playback.post(() -> visibleScreens++);
    }

    public void switchTrack(MusicTrack track) {
        playback.post(() -> {
            if (state == State.PLAYING) {
                if (selectedTrack != track) {
                    selectedTrack = track;
                    crossfade.begin(SystemClock.uptimeMillis());
                    playback.removeCallbacks(fadeFrame);
                    playback.post(fadeFrame);
                }
                return;
            }
            selectedTrack = track;
            if (visibleScreens > 0 && state == State.EMPTY) {
                prepareTracks();
            }
            startWhenReady();
        });
    }

    public void screenStopped() {
        playback.post(() -> {
            visibleScreens--;
            if (visibleScreens == 0) {
                savePositionAndPause();
                abandonFocus();
            }
        });
    }

    private void prepareTracks() {
        state = State.PREPARING;
        cycleDuration = Integer.MAX_VALUE;
        awaiting.addAll(EnumSet.allOf(MusicTrack.class));
        playback.postDelayed(loadTimeout, LOAD_TIMEOUT_MILLIS);
        try {
            for (MusicTrack track : awaiting) {
                MediaPlayer player = new MediaPlayer();
                players.put(track, player);
                player.setAudioAttributes(attributes);
                try (AssetFileDescriptor resource = app.getResources().openRawResourceFd(track.audioResource)) {
                    player.setDataSource(resource.getFileDescriptor(), resource.getStartOffset(),
                            resource.getLength());
                }
                player.setOnPreparedListener(ready -> trackPrepared(track, ready));
                player.setOnSeekCompleteListener(ready -> seekCompleted(track, ready));
                player.setOnCompletionListener(completed -> trackCompleted(track, completed));
                player.setOnErrorListener((failed, what, extra) -> {
                    if (players.get(track) == failed) {
                        fail("Playback failed for " + track + " (" + what + ", " + extra + ")", null);
                    }
                    return true;
                });
                player.prepareAsync();
            }
        } catch (IOException | RuntimeException failure) {
            fail("Unable to prepare the soundtrack", failure);
        }
    }

    private void trackPrepared(MusicTrack track, MediaPlayer ready) {
        if (state != State.PREPARING || players.get(track) != ready || !awaiting.remove(track)) {
            return;
        }
        try {
            cycleDuration = Math.min(cycleDuration, ready.getDuration());
        } catch (IllegalStateException failure) {
            fail("Unable to read the soundtrack duration", failure);
            return;
        }
        if (awaiting.isEmpty()) {
            readyToStart();
        }
    }

    private void readyToStart() {
        playback.removeCallbacks(loadTimeout);
        state = State.READY;
        startWhenReady();
    }

    private void startWhenReady() {
        if (state != State.READY || visibleScreens == 0) {
            return;
        }
        if (focusListener == null) {
            requestFocus();
        }
        if (!focusAvailable) {
            return;
        }
        if (needsSeek) {
            seekTogether(savedPosition);
            return;
        }
        try {
            crossfade.setSolo(selectedTrack);
            applyVolumes();
            for (MediaPlayer player : players.values()) {
                player.start();
            }
            state = State.PLAYING;
        } catch (IllegalStateException failure) {
            fail("Unable to start the soundtrack", failure);
        }
    }

    private void advanceFade() {
        if (state != State.PLAYING) {
            return;
        }
        try {
            boolean moreFrames = crossfade.advance(selectedTrack, SystemClock.uptimeMillis());
            applyVolumes();
            if (moreFrames) {
                playback.postDelayed(fadeFrame, FRAME_INTERVAL_MILLIS);
            }
        } catch (IllegalStateException failure) {
            fail("Unable to update the soundtrack mix", failure);
        }
    }

    private void applyVolumes() {
        players.forEach((track, player) -> {
            float volume = crossfade.volume(track);
            player.setVolume(volume, volume);
        });
    }

    private void trackCompleted(MusicTrack track, MediaPlayer completed) {
        if (state != State.PLAYING || players.get(track) != completed) {
            return;
        }
        try {
            // Ignore stale completion callbacks if this player has already restarted.
            if (!completed.isPlaying()) {
                pauseTracks();
                seekTogether(0);
            }
        } catch (IllegalStateException failure) {
            fail("Unable to restart the soundtrack", failure);
        }
    }

    private void savePositionAndPause() {
        if (state != State.PLAYING) {
            return;
        }
        try {
            int position = players.get(selectedTrack).getCurrentPosition();
            savedPosition = position < cycleDuration ? position : 0;
            pauseTracks();
            needsSeek = true;
            state = State.READY;
        } catch (IllegalStateException failure) {
            fail("Unable to pause the soundtrack", failure);
        }
    }

    private void pauseTracks() {
        playback.removeCallbacks(fadeFrame);
        for (MediaPlayer player : players.values()) {
            if (player.isPlaying()) {
                player.pause();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void seekTogether(int position) {
        state = State.SEEKING;
        needsSeek = false;
        awaiting.addAll(players.keySet());
        playback.postDelayed(loadTimeout, LOAD_TIMEOUT_MILLIS);
        try {
            for (MediaPlayer player : players.values()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    player.seekTo(position, MediaPlayer.SEEK_CLOSEST);
                } else {
                    player.seekTo(position);
                }
            }
        } catch (IllegalStateException failure) {
            fail("Unable to seek the soundtrack", failure);
        }
    }

    private void seekCompleted(MusicTrack track, MediaPlayer ready) {
        if (state == State.SEEKING && players.get(track) == ready
                && awaiting.remove(track) && awaiting.isEmpty()) {
            readyToStart();
        }
    }

    @SuppressWarnings("deprecation")
    private void requestFocus() {
        focusListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int change) {
                playback.post(() -> {
                    if (focusListener == this) {
                        onFocusChanged(change);
                    }
                });
            }
        };
        int result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(attributes)
                    .setWillPauseWhenDucked(true)
                    .setOnAudioFocusChangeListener(focusListener, playback)
                    .build();
            result = audio.requestAudioFocus(focusRequest);
        } else {
            result = audio.requestAudioFocus(focusListener, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
        }
        focusAvailable = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        if (!focusAvailable) {
            focusListener = null;
            focusRequest = null;
        }
    }

    private void onFocusChanged(int change) {
        if (change == AudioManager.AUDIOFOCUS_GAIN) {
            focusAvailable = true;
            startWhenReady();
        } else {
            focusAvailable = false;
            savePositionAndPause();
            if (change == AudioManager.AUDIOFOCUS_LOSS) {
                abandonFocus();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void abandonFocus() {
        if (focusListener != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audio.abandonAudioFocusRequest(focusRequest);
            } else {
                audio.abandonAudioFocus(focusListener);
            }
        }
        focusListener = null;
        focusRequest = null;
        focusAvailable = false;
    }

    private void fail(String message, Throwable failure) {
        Log.w(TAG, message, failure);
        playback.removeCallbacks(fadeFrame);
        playback.removeCallbacks(loadTimeout);
        state = State.EMPTY;
        players.values().forEach(MediaPlayer::release);
        players.clear();
        awaiting.clear();
        needsSeek = false;
        abandonFocus();
    }
}
