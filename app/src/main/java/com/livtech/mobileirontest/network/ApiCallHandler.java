package com.livtech.mobileirontest.network;

import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.livtech.mobileirontest.apiservices.TweeterService;
import com.livtech.mobileirontest.models.TwitterTokenType;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiCallHandler {
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private static final String TAG = "ApiCallHandler";
    private static final String BASE_URL = "https://api.twitter.com";
    private static final String GRANT_TYPE_VALUE = "client_credentials";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String BASIC = "Basic ";
    private static final String CONSUMER_KEY = "68y5E7dCm96CddcBPhlGVABVg";
    private static final String CONSUMER_SECRET = "ulKHCdAEvRrlEK2B06j2jND5q24UXNpnM6pI06G3PMB00N8xW6";
    private static String accessToken = "4273204042-owKAWePIv5Zj42ERGbof4nXoRi1e1cj1Zi6dO06";


    public static ApiCallHandler newInstance() {
        return new ApiCallHandler();
    }

    private ApiCallHandler() {
        initOkHttpClient();
        initRetrofit();
    }

    private void initOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.addInterceptor(interceptor);
        builder.authenticator(authenticator);
        okHttpClient = builder.build();
    }

    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    private Interceptor interceptor = chain -> {
        Request request;
        try {
            request = getRequest(chain);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            request = chain.request();
        }
        return chain.proceed(request);
    };

    private Authenticator authenticator = new Authenticator() {
        @Nullable
        @Override
        public Request authenticate(Route route, Response response) throws IOException {
            accessToken = getAccessToken();
            Request.Builder original = response.request().newBuilder();
            original.header(AUTHORIZATION, getAuthorization(accessToken));
            return original.build();
        }
    };

    private Request getRequest(Interceptor.Chain chain) {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder();
        requestBuilder.addHeader(AUTHORIZATION, getAuthorization(accessToken));
        HttpUrl originalHttpUrl = original.url();
        HttpUrl.Builder builder = originalHttpUrl.newBuilder();
        builder.addQueryParameter("lang", "en");
        builder.addQueryParameter("result_type", "popular");
        requestBuilder.url(builder.build());
        return requestBuilder.build();
    }

    public <T> T createService(Class<T> service) {
        return retrofit.create(service);
    }

    private String getAuthorization(String token) {
        return BEARER + token;
    }

    private String getAccessToken() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TweeterService service = retrofit.create(TweeterService.class);
        Call<TwitterTokenType> call = service.getToken(getTokenAuthorization(), GRANT_TYPE_VALUE);
        try {
            retrofit2.Response<TwitterTokenType> response = call.execute();
            if (response != null && response.body() != null)
                accessToken = response.body().accessToken;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    private String getTokenAuthorization() {
        String encodedKey = BASIC;
        String value = CONSUMER_KEY + ":" + CONSUMER_SECRET;
        try {
            encodedKey = encodedKey + Base64.encodeToString(value.getBytes("UTF-8"), Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodedKey + value;
    }

}
