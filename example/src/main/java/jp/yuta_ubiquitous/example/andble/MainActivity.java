package jp.yuta_ubiquitous.example.andble;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import jp.yuta_ubiquitous.andble.AndbleDevice;
import jp.yuta_ubiquitous.andble.AndbleEventCallback;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        AndbleDevice device = new AndbleDevice("DE:BA:08:61:27:01", manager.getAdapter(), new AndbleEventCallback() {
            @Override
            public void onConnect() {
                super.onConnect();
                Log.d(TAG, "onConnect()");
            }

            @Override
            public void onDisconnect() {
                super.onDisconnect();
                Log.d(TAG, "onDisconnect()");
            }

            @Override
            public void onConnectFailed() {
                super.onConnectFailed();
                Log.d(TAG, "onConnectFailed()");
            }

            @Override
            public void onDiscovery() {
                super.onDiscovery();
                Log.d(TAG, "onDiscovery()");
            }

            @Override
            public void onNotDiscovery() {
                super.onNotDiscovery();
                Log.d(TAG, "onNotDiscovery()");
            }

            @Override
            public void onScan() {
                super.onScan();
                Log.d(TAG, "onScan()");
            }

            @Override
            public void onScanFailed() {
                super.onScanFailed();
                Log.d(TAG, "onScanFailed()");
            }
        });
        Log.d(TAG, "connect()");
        device.connect(30000);
    }
}
