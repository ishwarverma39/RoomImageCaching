package com.livtech.mobileirontest.listeners;

import android.support.annotation.WorkerThread;

public interface DBInsertListener<RequestType> {
    @WorkerThread
    void saveApiCallResult(RequestType response);

    void updateLiveData();

}
