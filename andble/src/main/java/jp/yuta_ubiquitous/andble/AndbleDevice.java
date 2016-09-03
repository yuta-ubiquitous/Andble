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
    private UUID uuid;
    private BluetoothLeScanner bluetoothLeScanner;
    private AndbleEventCallback andbleEventCallback;

    final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            ParcelUuid[] uuids = device.getUuids();
            String uuid = "";
            if (uuids != null) {
                for (ParcelUuid puuid : uuids) {
                    uuid += puuid.toString() + " ";
                }
            }
            String msg = "name=" + device.getName() + ", bondStatus="
                    + device.getBondState() + ", address="
                    + device.getAddress() + ", type" + device.getType()
                    + ", uuids=" + uuid;
            Log.d(TAG, msg);

            byte[] scanRecord = result.getScanRecord().getBytes();
            String serviceUuid = String.format("%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x",
                    scanRecord[ 5] & 0xff,
                    scanRecord[ 6] & 0xff,
                    scanRecord[ 7] & 0xff,
                    scanRecord[ 8] & 0xff,

                    scanRecord[ 9] & 0xff,
                    scanRecord[10] & 0xff,

                    scanRecord[11] & 0xff,
                    scanRecord[12] & 0xff,

                    scanRecord[13] & 0xff,
                    scanRecord[14] & 0xff,

                    scanRecord[15] & 0xff,
                    scanRecord[16] & 0xff,
                    scanRecord[17] & 0xff,
                    scanRecord[18] & 0xff,
                    scanRecord[19] & 0xff,
                    scanRecord[20] & 0xff
            );
            Log.d(TAG, serviceUuid);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    public AndbleDevice(String uuid, BluetoothAdapter adapter, AndbleEventCallback callback){
        //this.uuid = UUID.fromString(uuid);
        this.bluetoothLeScanner = adapter.getBluetoothLeScanner();
        this.andbleEventCallback = callback;
    }

    public void connect( int timeout ){

        bluetoothLeScanner.startScan( scanCallback );
        Handler timeHandler = new Handler();
        timeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bluetoothLeScanner.stopScan( scanCallback );
            }
        }, timeout);
    }
}
