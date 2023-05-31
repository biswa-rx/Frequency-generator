package com.example.frequencygenerator;

import static com.example.frequencygenerator.App.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class WaveGeneratorService extends Service implements LifecycleOwner {
    private static final String TAG = "WaveGeneratorService";
    private LifecycleRegistry lifecycleRegistry;
    private boolean ThreadTermineterFlag = false;

    final int sampleRate = 44100; // Hz
    final int durationMs = 2000; // milliseconds
    int numSamples = (int) ((double) sampleRate * durationMs / 1000.0);
    AudioTrack audioTrack;

    @Override
    public void onCreate() {
        super.onCreate();
        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.markState(Lifecycle.State.CREATED);

        ThreadTermineterFlag = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int waveFrequency = intent.getIntExtra("wave_frequency", 450);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Example Service")
                .setContentText(waveFrequency + "")
                .setSmallIcon(R.drawable.sine_wave)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        Thread workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Wave generation code here
                double[] buffer = new double[numSamples];
                double value;
                double period;
                // Calculate the frequency of the sine wave
                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, numSamples * 2,
                        AudioTrack.MODE_STREAM);
                while (ThreadTermineterFlag) {
                    double frequency = MainActivity.currentFrequency; // Hz
                    String waveType = MainActivity.waveType;
                    switch(waveType){
                        case "SINE_WAVE":
                            // Generate the audio data
                            for (int i = 0; i < numSamples; i++) {
                                double time = (double) i / (double) sampleRate;
                                buffer[i] = Math.sin(2.0 * Math.PI * frequency * time);
                            }
                            break;
                        case "SQUARE_WAVE":
                            period = (int) (sampleRate / frequency);
                            for (int i = 0; i < buffer.length; i++) {
                                buffer[i] = (i % period < period / 2) ? Short.MAX_VALUE : Short.MIN_VALUE;
                            }
                            break;
                        case "TRIANGLE_WAVE":
                            period = (int) (sampleRate / frequency);
                            int halfPeriod = (int)period / 2;
                            double delta = (double) (Short.MAX_VALUE * 2) / halfPeriod;
                            value = -Short.MAX_VALUE;
                            for (int i = 0; i < buffer.length; i++) {
                                buffer[i] = (short) value;
                                if (i % period < halfPeriod) {
                                    value += delta;
                                } else {
                                    value -= delta;
                                }
                            }
                            break;
                        case "SAWTOOTH_WAVE":
                            period = sampleRate / frequency;
                            double amplitude = Short.MAX_VALUE;
                            double slope = amplitude / period;
                            value = 0;
                            for (int i = 0; i < buffer.length; i++) {
                                buffer[i] = (short) value;
                                value += slope;
                                if (value > amplitude) {
                                    value -= 2 * amplitude;
                                }
                            }
                            break;
                    }


                    // Convert the audio data to bytes
                    byte[] byteData = new byte[numSamples * 2];
                    for (int i = 0; i < numSamples; i++) {
                        short val = (short) (buffer[i] * 32767);
                        byteData[i * 2] = (byte) (val & 0xff);
                        byteData[i * 2 + 1] = (byte) ((val >> 8) & 0xff);
                    }
                    // Create an AudioTrack object and play the audio
//                    audioTrack.write(byteData, 0, byteData.length);
                    audioTrack.play();
                    MainActivity.FrequencyRefreshFlag = false;
//                    while (!MainActivity.FrequencyRefreshFlag) {
                    Log.d(TAG, "run: sound write");
                        audioTrack.write(byteData, 0, byteData.length);
//                    }
//                    audioTrack.pause();
//                    SystemClock.sleep(500);
                    Log.d(TAG, "run: sound out");
                }
                audioTrack.pause(); // pause playback
                audioTrack.flush(); // clear playback queue
                audioTrack.release();
            }
        });
        workerThread.start();


        return START_STICKY;
    }

    @Override
    public void onDestroy() {//cause due to it run in main thread
        super.onDestroy();
        ThreadTermineterFlag = false;
        MainActivity.FrequencyRefreshFlag = true;
        Log.d(TAG, "onDestroy: called");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }
}
