package com.mieczkowskidev.friendspotter.API;

import com.mieczkowskidev.friendspotter.Objects.User;
import com.mieczkowskidev.friendspotter.Objects.UserLogin;


import retrofit.http.POST;
import rx.Observable;

/**
 * Created by Patryk Mieczkowski on 2015-12-24
 */
public interface RestAPI {

    @POST("/register")
    Observable<User> registerUser(UserLogin userLogin);

    @POST("/login")
    Observable<User> loginUser(UserLogin userLogin);
}
