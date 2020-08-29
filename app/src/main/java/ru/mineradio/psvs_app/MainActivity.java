package ru.mineradio.psvs_app;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
<<<<<<< Updated upstream
=======
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
>>>>>>> Stashed changes
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntRange;
import androidx.appcompat.app.AppCompatActivity;
import eu.amirs.JSON;

public class MainActivity extends AppCompatActivity {

    String receiveBuffer;

    int i = 0;
    int passangers = 0;
    int inRange = 0;
    int inRangeInR = 0;
    int inRangeInY = 0;
    int inRangeInG =0;

<<<<<<< Updated upstream
    String TAG = "BT";
    private Bluetooth bt;

=======
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
>>>>>>> Stashed changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the status bar.
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//        decorView.setSystemUiVisibility(uiOptions);

        //for test change picture in location
        bottom = findViewById(R.id.bottomIndicator);
        top = findViewById(R.id.topIndicator);

        soundBut = findViewById(R.id.soundBut);
        addPassBut = findViewById(R.id.addPassBut);

        inrangecount = findViewById(R.id.inRangeCount);
        inredcount = findViewById(R.id.InRedCount);
        inyelcount = findViewById(R.id.inYelCount);
        ingrecount = findViewById(R.id.inGreCount);
        passangerscount = findViewById(R.id.passangersCount);

        audioInit();

        bt = new Bluetooth(this, mHandler);
        connectService();


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                switch (i){
                    case 0:
                        bt.sendMessage("SISKI");
                        bottom.setImageResource(R.drawable.bottom1);
                        top.setImageResource(R.drawable.top2);
                        break;
                    case 1:
                        bottom.setImageResource(R.drawable.bottom2);
                        top.setImageResource(R.drawable.top3);

                        break;
                    case 2:
                        bottom.setImageResource(R.drawable.bottom3);
                        top.setImageResource(R.drawable.top1);
                        break;
                    default:
                        break;
                }
                if (i<2){
                    i++;
                }
                else{
                    i= 0;
                }
                updateCounter();
                handler.postDelayed(this, 500);
            }
        }, 500);


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
                passangerscount.setText(Integer.toString(inRangeInR));
                inRangeInR = 0;

                Context context = getApplicationContext();
                CharSequence text = getString(R.string.addpassengers);
                int duration = Toast.LENGTH_SHORT;

                Toast.makeText(context, text, duration).show();
            }
        });

    }


    public void connectService(){
        try {
//            status.setText("Connecting...");
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled()) {
                bt.start();
                bt.connectDevice("MLT-BT05");  //DESKTOP-RGRSMV5
                Log.d(TAG, "Btservice started - listening");
//                status.setText("Connected");
            } else {
                Log.w(TAG, "Btservice started - bluetooth is not enabled");
//                status.setText("Bluetooth Not enabled");
            }
        } catch(Exception e){
            Log.e(TAG, "Unable to start bt ",e);
//            status.setText("Unable to connect " +e);
        }
    }
    private final Handler mHandler = new Handler() {
        @Override
<<<<<<< Updated upstream
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Bluetooth.MESSAGE_STATE_CHANGE:
                    Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    break;
                case Bluetooth.MESSAGE_WRITE:
                    Log.d(TAG, "MESSAGE_WRITE ");
                    break;
                case Bluetooth.MESSAGE_READ:
                    Log.d(TAG, "MESSAGE_READ ");
                    break;
                case Bluetooth.MESSAGE_DEVICE_NAME:
                    Log.d(TAG, "MESSAGE_DEVICE_NAME "+msg);
                    break;
                case Bluetooth.MESSAGE_TOAST:
                    Log.d(TAG, "MESSAGE_TOAST "+msg);
                    break;
=======
        public void onReceive(Context context, Intent intent) {

            Log.d("BLE2", "onReceive");

            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String tempReceiveBuffer = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                receiveBuffer=receiveBuffer+tempReceiveBuffer;
                if(tempReceiveBuffer.contains("\n")) {
                    receiveBuffer = receiveBuffer.substring(0, receiveBuffer.length() - 1);
                    if (receiveBuffer != null) {
                        Log.d("BLE2",receiveBuffer);
                        JSON psvsjson = new JSON(receiveBuffer);
                        updatePSVSData(psvsjson);
                    }
                    receiveBuffer = "";
                    //mBluetoothLeService.writeCharacteristic(receiveBuffer);
                }
>>>>>>> Stashed changes
            }
        }
    };

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
        inrangecount.setText(Integer.toString(inRangeInG+inRangeInR+inRangeInY));
        inredcount.setText(Integer.toString(inRangeInR));
        inyelcount.setText(Integer.toString(inRangeInY));
        ingrecount.setText(Integer.toString(inRangeInG));
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

