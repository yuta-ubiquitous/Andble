package jp.yuta_ubiquitous.andble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by yuta-ta on 16/09/03.
 */
public class AndbleDevice {

    private String TAG = this.getClass().getSimpleName();

    private Context context;
    private String address;
    private BluetoothLeScanner bluetoothLeScanner;
    private AndbleEventCallback andbleEventCallback;
    private boolean isDiscovery;

    final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            BluetoothDevice device = result.getDevice();
            String address = device.getAddress();

            if( checkAddress( address ) ){
                andbleEventCallback.onDiscovery();
                isDiscovery = true;
                bluetoothLeScanner.stopScan( this );
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            andbleEventCallback.onScanFailed();
        }
    };

    public AndbleDevice(String address, BluetoothAdapter adapter, AndbleEventCallback callback){
        this.address = address;
        this.bluetoothLeScanner = adapter.getBluetoothLeScanner();
        this.andbleEventCallback = callback;
        this.isDiscovery = false;
    }

    public void connect( int timeout ){
        bluetoothLeScanner.startScan( scanCallback );
        andbleEventCallback.onScan();

        Handler timeHandler = new Handler();
        timeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isDiscovery){
                    andbleEventCallback.onNotDiscovery();
                }
                bluetoothLeScanner.stopScan( scanCallback );
            }
        }, timeout);
    }

    private boolean checkAddress( String address ){
        if( this.address.equals( address ) ){
            return true;
        }else{
            return false;
        }
    }
}
