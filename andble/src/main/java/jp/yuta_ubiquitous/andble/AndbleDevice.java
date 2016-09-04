package jp.yuta_ubiquitous.andble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AndbleDevice {

    private String TAG = this.getClass().getSimpleName();
    String CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    private Context context;

    private String address;
    private BluetoothLeScanner bluetoothLeScanner;
    private AndbleEventCallback andbleEventCallback;
    private boolean isDiscovery;
    private BluetoothGatt bluetoothGatt;
    private HashMap<UUID, AndbleNotificationCallback> notificationCallbackHashMap;

    // temporal
    private UUID characteristicUuid;
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
                        if( newState == BluetoothProfile.STATE_CONNECTED ){
                            andbleEventCallback.onConnect();

                            if( currentOperation == AndbleResultCallback.CONNECT ){
                                andbleResultCallback.onSuccess( currentOperation );
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
                        Log.d(TAG, "onServicesDiscoverd()");
                        if (status == BluetoothGatt.GATT_SUCCESS ){
                            List<BluetoothGattService> services = gatt.getServices();
                            for( BluetoothGattService service : services ){
                                BluetoothGattCharacteristic characteristic = service.getCharacteristic( characteristicUuid );
                                // find characteristic
                                if( characteristic != null ){
                                    switch (currentOperation){
                                        case AndbleResultCallback.READ:
                                            bluetoothGatt.readCharacteristic( characteristic );
                                            break;
                                        case AndbleResultCallback.SET_NOTIFICATION:
                                            // TODO registeredの分岐
                                            boolean registerd = bluetoothGatt.setCharacteristicNotification( characteristic, true );
                                            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                                                    UUID.fromString( CHARACTERISTIC_CONFIG )
                                            );
                                            descriptor.setValue( BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE );
                                            gatt.writeDescriptor( descriptor );
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                        super.onCharacteristicRead(gatt, characteristic, status);
                        byte[] values = characteristic.getValue();
                        andbleResultCallback.onSuccess( andbleResultCallback.READ, values );
                    }

                    @Override
                    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                        super.onCharacteristicWrite(gatt, characteristic, status);
                        if( currentOperation == AndbleResultCallback.SET_NOTIFICATION){
                            andbleResultCallback.onSuccess( AndbleResultCallback.SET_NOTIFICATION );
                        }
                    }

                    @Override
                    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                        super.onCharacteristicChanged(gatt, characteristic);
                        if( notificationCallbackHashMap.containsKey( characteristic.getUuid() ) ){
                            byte[] notifyValues = characteristic.getValue();
                            int properties = characteristic.getProperties();
                            notificationCallbackHashMap.get( characteristic.getUuid() ).onNotify( properties, notifyValues );
                        }
                    }
                } );
                bluetoothLeScanner.stopScan( this );
            }
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
        this.notificationCallbackHashMap = new HashMap<>();
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
            }
        }, timeout);
    }

    public void disconnect(){
        bluetoothGatt.disconnect();
    }

    public void read( String uuidStr, AndbleResultCallback callback ){
        UUID uuid =  UUID.fromString( uuidStr );
        this.characteristicUuid = uuid;

        this.andbleResultCallback = callback;

        this.currentOperation = AndbleResultCallback.READ;
        bluetoothGatt.discoverServices();
    }

    public void setNotification( String uuidStr, AndbleNotificationCallback notificationCallback, AndbleResultCallback resultCallback){
        UUID uuid = UUID.fromString( uuidStr );
        this.characteristicUuid = uuid;

        this.andbleResultCallback = resultCallback;

        this.notificationCallbackHashMap.put(uuid, notificationCallback);

        this.currentOperation = AndbleResultCallback.SET_NOTIFICATION;
        bluetoothGatt.discoverServices();
    }

    private boolean checkAddress( String address ){
        if( this.address.equals( address ) ){
            return true;
        }else{
            return false;
        }
    }

    // unuse
    private void resetOperation(){
        Log.d(TAG, "resetOperation()");
        this.currentOperation = 0;
        this.andbleResultCallback = null;
    }
}