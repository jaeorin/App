package com.iot.eround.Util;

public interface RetroCallback<T> {

    void onError(Throwable t);
    void onSuccess(int code, T receivedData);
    void onFailure(int code);

}
