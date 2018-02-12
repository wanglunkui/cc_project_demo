package com.wlk.android.club.http;

import android.content.Context;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by wanglunkui on 2018/1/18.
 */

public class HttpUtil {
    public static final String HTTP_HOST = "https://union.codoon.com";

    public static void login(String accout, String password, Context context){
        ILogin service = ServiceFactory.getInstance(context).getClient(ILogin.class);
        AuthorizationRequest req = new AuthorizationRequest();
        req.email = accout;
        req.password = password;
        req.grant_type = "password";
        req.client_id = ServiceFactory.CLUB_KEY;
        req.scope = "user";
        Call<AuthorizationJSON> call = service.AuthorizationHttp(req);
        call.enqueue(new Callback<AuthorizationJSON>() {
            @Override
            public void onResponse(Call<AuthorizationJSON> call, Response<AuthorizationJSON> response) {
                Log.w("wlk","login return response");
            }

            @Override
            public void onFailure(Call<AuthorizationJSON> call, Throwable t) {
                Log.w("wlk","login failure");
            }
        });
    }

    interface ILogin{
        @POST(HTTP_HOST + "/token")
        Call<AuthorizationJSON> AuthorizationHttp(@Body AuthorizationRequest req);
    }

    static class AuthorizationRequest {
        public String email;
        public String password;
        public String grant_type;
        public String client_id;
        public String scope;
    }
}
