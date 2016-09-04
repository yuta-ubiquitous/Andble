package jp.yuta_ubiquitous.andble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
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
    private BluetoothGatt bluetoothGatt;
    private AndbleResultCallback andbleResultCallback;
    private int currentOperation;

    final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            BluetoothDevice device = result.getDevice();
            String address = device.getAddress();

            if( checkAddress( address ) && !isDiscovery ){
                andbleEventCallback.onDiscovery();
                isDiscovery = true;
                bluetoothGatt = device.connectGatt(context, false, new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        super.onConnectionStateChange(gatt, status, newState);
                        // Log.d(TAG, "newstatus=" + newState);
                        if( newState == BluetoothProfile.STATE_CONNECTED ){
                            andbleEventCallback.onConnect();

                            if( currentOperation == AndbleResultCallback.CONNECT ){
                                andbleResultCallback.onSuccess( currentOperation );
                                resetOperation();
                            }

                        }else if( newState == BluetoothProfile.STATE_DISCONNECTED ){
                            andbleEventCallback.onDisconnect();
                            bluetoothGatt = null;
                            isDiscovery = false;
                        }
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        super.onServicesDiscovered(gatt, status);
                    }

                    @Override
                    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                        super.onCharacteristicRead(gatt, characteristic, status);
                    }

                    @Override
                    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                        super.onCharacteristicWrite(gatt, characteristic, status);
                    }

                    @Override
                    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                        super.onCharacteristicChanged(gatt, characteristic);
                    }

                    @Override
                    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                        super.onDescriptorRead(gatt, descriptor, status);
                    }

                    @Override
                    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                        super.onDescriptorWrite(gatt, descriptor, status);
                    }

                    @Override
                    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                        super.onReliableWriteCompleted(gatt, status);
                    }

                    @Override
                    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                        super.onReadRemoteRssi(gatt, rssi, status);
                    }

                    @Override
                    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                        super.onMtuChanged(gatt, mtu, status);
                    }
                } );
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

    public AndbleDevice(String address, Context context, AndbleEventCallback callback){
        this.address = address;
        this.context = context;
        BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothLeScanner = manager.getAdapter().getBluetoothLeScanner();
        this.andbleEventCallback = callback;
        this.isDiscovery = false;
        this.bluetoothGatt = null;
    }

    public void connect( int timeout, AndbleResultCallback callback){
        this.andbleResultCallback = callback;
        this.currentOperation = AndbleResultCallback.CONNECT;
        bluetoothLeScanner.startScan( scanCallback );
        andbleEventCallback.onScan();

        Handler timeHandler = new Handler();
        timeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isDiscovery) {
                    andbleEventCallback.onNotDiscovery();
                    bluetoothLeScanner.stopScan( scanCallback );
                    andbleResultCallback.onFailed( AndbleResultCallback.CONNECT );
                }
                resetOperation();
            }
        }, timeout);
    }

    public  void disconnect(){
        bluetoothGatt.disconnect();
    }

    private boolean checkAddress( String address ){
        if( this.address.equals( address ) ){
            return true;
        }else{
            return false;
        }
    }

    private void resetOperation(){
        this.currentOperation = 0;
        this.andbleResultCallback = null;
    }
}
