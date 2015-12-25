package com.mieczkowskidev.friendspotter.Utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by Patryk Mieczkowski on 2015-12-24
 */
public class GenericConverter<T> {

    private RestAdapter adapter;

    public RestAdapter getRestAdapter() {
        return adapter;
    }

    public GenericConverter(String endpoint, final Class<T> templateClass) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(templateClass, new Deserializer<T>() {
            @Override
            public Class<T> setDestinationClass() {
                return templateClass;
            }
        });
        Gson gson = builder.create();
        GsonConverter converter = new GsonConverter(gson);
        adapter = new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setConverter(converter)
                .setRequestInterceptor(requestInterceptor)
                .build();
    }

    RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("Content-Type", "application/json");
        }
    };

    private static abstract class Deserializer<T> implements JsonDeserializer<T> {

        public abstract Class<T> setDestinationClass();

        @Override
        public T deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            Log.d("GenericConverter", "deserialize: " + json);
            return new Gson().fromJson(json, setDestinationClass());
        }
    }

}
