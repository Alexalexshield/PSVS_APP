package ru.mineradio.psvs_app;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntRange;
import androidx.appcompat.app.AppCompatActivity;
import eu.amirs.JSON;
import ru.mineradio.psvs_app.ui.BleMainActivity;

public class MainActivity extends AppCompatActivity {

    String receiveBuffer;

    int i = 0;
    int passangers = 0;
    int inRange = 0;
    int inRangeInR = 0;
    int inRangeInY = 0;
    int inRangeInG =0;


    private ImageView bottom;
    private ImageView top;

    private Button soundBut;
    private Button addPassBut;

    private TextView inrangecount;
    private TextView inredcount;
    private TextView inyelcount;
    private TextView ingrecount;
    private TextView passangerscount;


    // Stream type.
    private static final int streamType = AudioManager.STREAM_MUSIC;
    private SoundPool soundPool;
    private AudioManager audioManager;
    private int soundIdAlarm;
    private float volume;
    private boolean loaded;
    // Maximumn sound stream.
    private static final int MAX_STREAMS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        //for test change picture in location
        bottom = findViewById(R.id.bottomIndicator);
        top = findViewById(R.id.topIndicator);
        //left
        //right

        soundBut = findViewById(R.id.soundBut);
        addPassBut = findViewById(R.id.addPassBut);

        inrangecount = findViewById(R.id.inRangeCount);
        inredcount = findViewById(R.id.inRedCount);
        inyelcount = findViewById(R.id.inYelCount);
        ingrecount = findViewById(R.id.inGreCount);
        passangerscount = findViewById(R.id.passangersCount);

        audioInit();

        soundBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = getApplicationContext();
                CharSequence text = getString(R.string.soundbut);
                int duration = Toast.LENGTH_SHORT;

                Toast.makeText(context, text, duration).show();
                playSoundAlarm();
            }
        });

        addPassBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passangerscount.setText(String.valueOf(inRangeInR));//Integer.toString(inRangeInR));

                Context context = getApplicationContext();
                CharSequence text = getString(R.string.addpassengers);
                int duration = Toast.LENGTH_SHORT;

                Toast.makeText(context, text, duration).show();
            }
        });

        //BLE GATT SERVICES
        //todo make button entry
        final Intent intent = new Intent(this, BleMainActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        
    }

    public void updatePSVSData(JSON psvsjson) {
        if(psvsjson.key("IRR").exist()){
            inRangeInR = psvsjson.key("IRR").intValue();

        }
        if(psvsjson.key("IRY").exist()){
            inRangeInY = psvsjson.key("IRY").intValue();

        }
        if(psvsjson.key("IRG").exist()){
            inRangeInG = psvsjson.key("IRG").intValue();

        }
    }

    private void updateCounter() {

        inrangecount.setText(String.valueOf(inRangeInG+inRangeInR+inRangeInY));
        inredcount.setText(String.valueOf(inRangeInR));
        inyelcount.setText(String.valueOf(inRangeInY));
        ingrecount.setText(String.valueOf(inRangeInG));
    }


    private void audioInit() {

        // AudioManager audio settings for adjusting the volume
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Current volume Index of particular stream type.
        float currentVolumeIndex = (float) audioManager.getStreamVolume(streamType);

        // Get the maximum volume index for a particular stream type.
        float maxVolumeIndex  = (float) audioManager.getStreamMaxVolume(streamType);

        // Volumn (0 --> 1)
        this.volume = currentVolumeIndex / maxVolumeIndex;

        // Suggests an audio stream whose volume should be changed by
        // the hardware volume controls.
        this.setVolumeControlStream(streamType);

        // For Android SDK >= 21
        if (Build.VERSION.SDK_INT >= 21 ) {
            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            SoundPool.Builder builder= new SoundPool.Builder();
            builder.setAudioAttributes(audioAttrib).setMaxStreams(MAX_STREAMS);

            this.soundPool = builder.build();
        }
        // for Android SDK < 21
        else {
            // SoundPool(int maxStreams, int streamType, int srcQuality)
            this.soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }

        // When Sound Pool load complete.
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });

        // Load sound file (alarm.wav) into SoundPool.
        this.soundIdAlarm = this.soundPool.load(this, R.raw.alarm,1);
    }

    // When users click on the button "Gun"
    public void playSoundAlarm()  {
        if(loaded)  {
            float leftVolumn = volume;
            float rightVolumn = volume;
            // Play sound of Alarm.
            int streamId = this.soundPool.play(this.soundIdAlarm,leftVolumn, rightVolumn, 1, 0, 1f);
        }
    }

}

