package jp.yuta_ubiquitous.andble;

/**
 * Created by yuta-ta on 16/09/03.
 */
public interface AndbleEventCallback {
    void onConnect();
    void onDisconnect();
    void onConnectFailed();
}
