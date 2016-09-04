# Andble
Andble is a bluetooth LE library for Android.  
This make easy to write BLE connection processes.  
It is suitable to develop a prototype or research App.  

## Requirements
Android OS 5.0 ~  
Bluetooth LE  

## Usage

### Instantiate

```java
AndbleDevice device = new AndbleDevice(macAddress, this, new AndbleEventCallback() {
	@Override
	public void onConnect() {

	}

	@Override
	public void onDisconnect() {

	}

	@Override
	public void onConnectFailed() {
	
	}

	@Override
	public void onDiscovery() {
	
	}

	@Override
	public void onNotDiscovery() {

	}

	@Override
	public void onScan() {
	
	}

	@Override
	public void onScanFailed() {
	
	}
});
```

### Connect to BLE device

```java
int timeout = 10000;
device.connect(timeout, new AndbleResultCallback() {
	@Override
	public void onSuccess(int operation) {

	}

	@Override
	public void onFailed(){

	}
});
```

### Disconnect
```java
device.disconnect();
```

### Read characteristic
```java
device.read(uuid, new AndbleResultCallback() {
	@Override
	public void onSuccess(int operation, byte[] values) {
		// values are read values;
	}

	@Override
	public void onFailed(){

	}
});
```

### Enable notification
```java
private AndbleNotificationCallback notificationCallback = new AndbleNotificationCallback() {
	@Override
	public void onNotify(int propaties, byte[] values) {
	
	}
};

// uuid is a characteristic uuid
device.setNotification(uuid, notificationCallback, new AndbleResultCallback() {
	@Override
	public void onSuccess(int operation) {

	}

	@Override
	public void onFailed(){

	}
});
```

#### Operation definitions
AndbleResultCallback.java
```java
public static final int CONNECT = 1;
public static final int READ = 2;
public static final int WRITE = 3;
public static final int SET_NOTIFICATION = 4;
```

## Sample
- [sample](https://github.com/yuta-ubiquitous/Andble/tree/master/example)

## License

```
Copyright 2016 yuta-ubiquitous

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```