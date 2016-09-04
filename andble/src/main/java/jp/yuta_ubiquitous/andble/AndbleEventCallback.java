package jp.yuta_ubiquitous.andble;

/**
 * Created by yuta-ta on 16/09/03.
 */
public abstract class AndbleEventCallback {
    public AndbleEventCallback(){};
    public void onScan(){};
    public void onScanFailed(){};
    public void onDiscovery(){};
    public void onNotDiscovery(){};
    public abstract void onConnect();
    public void onConnectFailed(){};
    public abstract void onDisconnect();
}