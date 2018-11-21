package com.livtech.mobileirontest.tasks;

import android.os.AsyncTask;

import com.livtech.mobileirontest.listeners.DBInsertListener;

public class DBInsertTask<RequestType> extends AsyncTask<Void, Void, Void> {
    private DBInsertListener dbInsertListener;
    private RequestType response;

    public DBInsertTask(DBInsertListener dbInsertListener, RequestType response) {
        this.dbInsertListener = dbInsertListener;
        this.response = response;
    }

    @Override
    protected Void doInBackground(Void... params) {
        dbInsertListener.saveApiCallResult(response);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        dbInsertListener.updateLiveData();
    }
}