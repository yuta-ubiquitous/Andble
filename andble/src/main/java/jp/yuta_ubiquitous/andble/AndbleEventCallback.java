package jp.yuta_ubiquitous.andble;

/**
 * Created by yuta-ta on 16/09/03.
 */
public abstract class AndbleEventCallback {
    public AndbleEventCallback(){};

    public void onConnect(){};
    public void onDisconnect(){};
    public void onConnectFailed(){};
    public void onDiscovery(){};
}
