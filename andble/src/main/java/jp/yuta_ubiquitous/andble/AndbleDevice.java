package jp.yuta_ubiquitous.andble;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

/**
 * Created by yuta-ta on 16/09/03.
 */
public class AndbleDevice {

    private Context context;
    private String uuid;
    private BluetoothAdapter bluetoothAdapter;
    private AndbleEventCallback andbleEventCallback;

    AndbleDevice(String uuid, BluetoothAdapter adapter, AndbleEventCallback callback){
        this.uuid = uuid;
        this.bluetoothAdapter = adapter;
        this.andbleEventCallback = callback;
    }

    void connect(){

    }
}
