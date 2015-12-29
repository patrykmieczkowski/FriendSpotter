package com.mieczkowskidev.friendspotter.API;

import com.google.gson.JsonElement;
import com.mieczkowskidev.friendspotter.Objects.Event;
import com.mieczkowskidev.friendspotter.Objects.User;
import com.mieczkowskidev.friendspotter.Objects.UserLogin;

import java.util.List;

import javax.security.auth.callback.Callback;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import rx.Observable;

/**
 * Created by Patryk Mieczkowski on 2015-12-24
 */
public interface RestAPI {

    @POST("/register")
    Observable<User> registerUser(@Body UserLogin userLogin);

    @POST("/login")
    Observable<User> loginUser(@Body UserLogin userLogin);

    @POST("/add-event")
    Observable<Response> addEvent(@Header("AuthToken") String token,
                                  @Body Event event);

    @GET("/get-events")
    Observable<Response> getEvents(@Header("AuthToken") String token,
                                   @Query("lat") Double lat,
                                   @Query("lon") Double lon,
                                   @Query("radius") int radius,
                                   @Query("timeOffset") int timeOffset);
}
