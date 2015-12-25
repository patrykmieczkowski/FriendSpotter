package com.mieczkowskidev.friendspotter.API;

import com.mieczkowskidev.friendspotter.Objects.User;
import com.mieczkowskidev.friendspotter.Objects.UserLogin;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by Patryk Mieczkowski on 2015-12-24
 */
public interface RestAPI {

    @POST("/register")
    Observable<User> registerUser(@Body UserLogin userLogin);

    @POST("/login")
    Observable<User> loginUser(@Body UserLogin userLogin);

    @GET("/test")
    Observable<Response> test();
}
