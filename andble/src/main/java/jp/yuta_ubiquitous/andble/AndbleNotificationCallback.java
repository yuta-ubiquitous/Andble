package jp.yuta_ubiquitous.andble;

public interface AndbleNotificationCallback {
    public abstract void onNotify( int properties, byte[] values );
}
