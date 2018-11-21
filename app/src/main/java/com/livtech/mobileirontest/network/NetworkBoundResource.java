package com.livtech.mobileirontest.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.livtech.mobileirontest.listeners.DBInsertListener;
import com.livtech.mobileirontest.models.Resource;
import com.livtech.mobileirontest.tasks.DBInsertTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class NetworkBoundResource<ResultType, RequestType> implements DBInsertListener<RequestType> {
    protected final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    public NetworkBoundResource() {
        result.setValue(Resource.loading(null));
        LiveData<ResultType> dbSource = loadFromDb();
        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource);
            } else {
                result.addSource(dbSource, newData -> result.setValue(Resource.success(newData)));
            }
        });
    }

    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
        result.addSource(dbSource, newData -> result.setValue(Resource.loading(newData)));
        createCall().enqueue(new Callback<RequestType>() {
            @Override
            public void onResponse(Call<RequestType> call, Response<RequestType> response) {
                if (response.isSuccessful()) {
                    result.removeSource(dbSource);
                    saveResultAndReInit(response.body());
                } else {
                    onFetchFailed(getErrorMessage());
                    result.removeSource(dbSource);
                    result.addSource(dbSource, newData -> result.setValue(Resource.error(getErrorMessage(), newData)));
                }
            }

            @Override
            public void onFailure(Call<RequestType> call, Throwable t) {
                onFetchFailed(t.getMessage());
                result.removeSource(dbSource);
                result.addSource(dbSource, newData -> result.setValue(Resource.error(t.getMessage(), newData)));
            }
        });
    }

    @MainThread
    private void saveResultAndReInit(RequestType response) {
        new DBInsertTask<>(this, response).execute();
    }

    @MainThread
    protected boolean shouldFetch(@Nullable ResultType data) {
        return true;
    }

    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    @NonNull
    @MainThread
    protected abstract Call<RequestType> createCall();

    @MainThread
    protected void onFetchFailed(String message) {
        //sub classes should handle if needed
    }

    public final LiveData<Resource<ResultType>> getAsLiveData() {
        return result;
    }

    private String getErrorMessage() {
        return "Some thing went wrong";
    }

    @MainThread
    @Override
    public void updateLiveData() {
        result.addSource(loadFromDb(), new Observer<ResultType>() {
            @Override
            public void onChanged(@Nullable ResultType resultType) {
                result.setValue(Resource.success(resultType));
            }
        });
    }
}