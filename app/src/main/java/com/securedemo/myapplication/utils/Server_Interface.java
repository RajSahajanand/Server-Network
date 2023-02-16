package com.securedemo.myapplication.utils;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface Server_Interface {

    @DELETE()
    Call<TraficLimitResponse> Call_Delete_Trafic(@Url String url);

    @POST()
    Call<TraficLimitResponse> Call_Add_Trafic(@Url String url);

}