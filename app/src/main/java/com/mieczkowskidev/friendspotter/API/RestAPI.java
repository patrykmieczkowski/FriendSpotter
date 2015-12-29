package com.mieczkowskidev.friendspotter.API;

import com.mieczkowskidev.friendspotter.Objects.Event;
import com.mieczkowskidev.friendspotter.Objects.User;
import com.mieczkowskidev.friendspotter.Objects.UserLogin;

import java.util.List;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
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

    @Multipart
    @POST("/add-event")
    Observable<Response> addEvent(@Part("image") TypedFile photo,
                                  @Part("title") String title,
                                  @Part("location") String location,
                                  @Part("description") String description,
                                  @Part("lat") Double lat,
                                  @Part("lon") Double lon,
                                  @Part("members") List<String> members);

    @GET("/test")
    Observable<Response> test();
}
