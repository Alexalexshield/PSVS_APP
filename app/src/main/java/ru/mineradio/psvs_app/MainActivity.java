package ru.mineradio.psvs_app;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static ru.mineradio.psvs_app.DeviceControlActivity.mBluetoothLeService;
import static ru.mineradio.psvs_app.DeviceControlActivity.mDeviceAddress;


public class MainActivity extends AppCompatActivity {

    int i = 0;
    int passangers = 0;
    int inRange = 0;
    int inRangeInR = 0;
    int inRangeInY = 0;
    int inRangeInG =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        //for test change picture in location
        final ImageView bottom = findViewById(R.id.bottomIndicator);
        final ImageView top = findViewById(R.id.topIndicator);

        final Button soundBut = findViewById(R.id.soundBut);
        final Button addPassBut = findViewById(R.id.addPassBut);

        final TextView inrangecount = findViewById(R.id.inRangeCount);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                switch (i){
                    case 0:
                        bottom.setImageResource(R.drawable.bottom1);
                        top.setImageResource(R.drawable.top2);
                        inrangecount.setText(Integer.toString(inRange));
                        break;
                    case 1:
                        bottom.setImageResource(R.drawable.bottom2);
                        top.setImageResource(R.drawable.top3);
                        inrangecount.setText(Integer.toString(inRange+2));
                        break;
                    case 2:
                        bottom.setImageResource(R.drawable.bottom3);
                        top.setImageResource(R.drawable.top1);
                        inrangecount.setText(Integer.toString(inRange-1));
                        break;
                    default:
                        break;
                }
                if (i<2){
                    i++;
                    inRange++;
                }
                else{
                    i= 0;
                    inRange = 0;
                }

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
            }
        });

        addPassBut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                passangers = inRangeInR;

                Context context = getApplicationContext();
                CharSequence text = getString(R.string.addpassengers);
                int duration = Toast.LENGTH_SHORT;

                Toast.makeText(context, text, duration).show();
            }
        });

        Intent intent = new Intent(this, DeviceScanActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        registerReceiver(mGattUpdateReceiverMain, DeviceControlActivity.makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d("BLE2", "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mGattUpdateReceiverMain);
    }

    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e("BLE2", "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    final BroadcastReceiver mGattUpdateReceiverMain = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("BLE2", "onReceive");

            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String receiveBuffer = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if(receiveBuffer.contains("\n")) {
                    receiveBuffer = receiveBuffer.substring(0, receiveBuffer.length() - 1);
                    if (receiveBuffer != null) {
                        Log.d("BLE2",receiveBuffer);
                    }
                    mBluetoothLeService.writeCharacteristic(receiveBuffer);
                }
            }
        }
    };
}
