package jp.yuta_ubiquitous.example.andble;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import jp.yuta_ubiquitous.andble.AndbleDevice;
import jp.yuta_ubiquitous.andble.AndbleEventCallback;
import jp.yuta_ubiquitous.andble.AndbleNotificationCallback;
import jp.yuta_ubiquitous.andble.AndbleResultCallback;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private final String MANUFACTURER_NAME_STRING = "00002A29-0000-1000-8000-00805F9B34FB";
    private static final String DEVICE_HEART_RATE_CHARACTERISTIC_UUID = "00002a37-0000-1000-8000-00805f9b34fb";

    private AndbleNotificationCallback notificationCallback = new AndbleNotificationCallback() {
        @Override
        public void onNotify(int propaties, byte[] values) {
            Log.d(TAG, "onNotify");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String macAddress = "**:**:**:**:**:**";

        final AndbleDevice device = new AndbleDevice(macAddress, this, new AndbleEventCallback() {
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

        int timeout = 30000;
        device.connect(timeout, new AndbleResultCallback() {
            @Override
            public void onSuccess(int operation) {
                device.read(MANUFACTURER_NAME_STRING, new AndbleResultCallback() {

                    @Override
                    public void onSuccess(int operation, byte[] values) {
                        byte[] readByte = values;
                        String read_str = "";
                        for(byte b : readByte ){
                            read_str += String.format("%02x", b);
                        }
                        Log.d(TAG, read_str);

                        device.setNotification( DEVICE_HEART_RATE_CHARACTERISTIC_UUID, notificationCallback, null );
                    }

                });
            }
        });
    }
}
