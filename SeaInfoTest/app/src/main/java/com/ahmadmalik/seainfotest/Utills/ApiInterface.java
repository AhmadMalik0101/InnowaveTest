package com.ahmadmalik.seainfotest.Utills;


import com.ahmadmalik.seainfotest.DataModel.UserResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiInterface {


    @GET("search/users")
    Call<UserResponse> getEmailResponse(@Query("q") String email);

    @GET()
    @Streaming
    Call<ResponseBody> downloadImage(@Url String fileUrl);

}
