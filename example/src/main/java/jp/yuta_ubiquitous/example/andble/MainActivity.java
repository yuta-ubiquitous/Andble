package jp.yuta_ubiquitous.example.andble;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import jp.yuta_ubiquitous.andble.AndbleDevice;
import jp.yuta_ubiquitous.andble.AndbleEventCallback;
import jp.yuta_ubiquitous.andble.AndbleResultCallback;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private final String MANUFACTURER_NAME_STRING = "00002A29-0000-1000-8000-00805F9B34FB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AndbleDevice device = new AndbleDevice("C3:F8:BC:A5:0B:FD", this, new AndbleEventCallback() {
            @Override
            public void onConnect() {
                Log.d(TAG, "onConnect()");
            }

            @Override
            public void onDisconnect() {
                Log.d(TAG, "onDisconnect()");
            }

            @Override
            public void onConnectFailed() {
                Log.d(TAG, "onConnectFailed()");
            }

            @Override
            public void onDiscovery() {
                Log.d(TAG, "onDiscovery()");
            }

            @Override
            public void onNotDiscovery() {
                Log.d(TAG, "onNotDiscovery()");
            }

            @Override
            public void onScan() {
                Log.d(TAG, "onScan()");
            }

            @Override
            public void onScanFailed() {
                Log.d(TAG, "onScanFailed()");
            }
        });

        device.connect(30000, new AndbleResultCallback() {
            @Override
            public void onSuccess(int operation) {
                device.read(MANUFACTURER_NAME_STRING, new AndbleResultCallback() {
                    @Override
                    public void onSuncess(int operation, byte[] values) {
                        byte[] readByte = values;
                        Log.d(TAG, "size:" + values.length);
                        String read_str = "";
                        for(byte b : readByte ){
                            read_str += String.format("%02x", b);
                        }
                        Log.d(TAG, read_str);
                    }
                });
            }

            @Override
            public void onFailed(int operation) {
            }
        });
    }
}
